package sg.therecursiveshepherd.crud.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.*;
import sg.therecursiveshepherd.crud.repositories.employees.write.EmployeeWriteRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.services.validators.EmployeeCustomValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Profile("write")
@Slf4j
public class EmployeeWriteService {

  private final ObjectReader employeeCsvMapper;
  private final EmployeeWriteRepository employeeWriteRepository;
  private final EmployeeMapper employeeMapper;
  private final EmployeeCustomValidator employeeCustomValidator;

  public EmployeeWriteService(
    @Qualifier("employeeCsvMapper") ObjectReader employeeCsvMapper,
    EmployeeCustomValidator employeeCustomValidator,
    EmployeeWriteRepository employeeWriteRepository,
    EmployeeMapper employeeMapper) {
    this.employeeCsvMapper = employeeCsvMapper;
    this.employeeWriteRepository = employeeWriteRepository;
    this.employeeMapper = employeeMapper;
    this.employeeCustomValidator = employeeCustomValidator;
  }

  @Transactional
  @Retryable(value = {TransientDataAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000L, maxDelay = 3000L))
  public long handleFileUpload(MultipartFile file) throws IOException {
    log.debug("Received file: {}", file.getOriginalFilename());
    var employeeDtos = mapCsvDataToDtos(file.getBytes());
    log.debug("Found {} records", employeeDtos.size());
    var validationErrors = employeeCustomValidator.validateDtoList(employeeDtos);
    if (!CollectionUtils.isEmpty(validationErrors)) {
      log.error("Validation failed for {}. Reason(s): {}", file.getOriginalFilename(), String.join(",", validationErrors));
      throw new EmployeeCsvValidationException(validationErrors);
    }
    var numUpdatedOrInserted = batchUpsert(employeeDtos);
    log.debug("Saved {} records", numUpdatedOrInserted);
    return numUpdatedOrInserted;
  }

  @Transactional
  @Retryable(value = {TransientDataAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000L, maxDelay = 3000L))
  public void createEmployee(EmployeeDto dto) {
    validateNoExistingIdOrLogin(dto);
    saveEmployeeDto(dto, null);
  }

  @Transactional
  @Retryable(value = {TransientDataAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000L, maxDelay = 3000L))
  public boolean replaceEmployeeById(String id, EmployeeDto dto) {
    if (!Objects.equals(id, dto.getId())) {
      throw new EmployeeIdMismatchException();
    }
    var existingRecord = employeeWriteRepository.findById(id);
    var isExistingRecord = existingRecord.isPresent();
    saveEmployeeDto(dto, existingRecord.orElse(null));
    return !isExistingRecord;
  }



  @Transactional
  @Retryable(value = {TransientDataAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000L, maxDelay = 3000L))
  public void updateEmployeeById(String id, EmployeeDto dto) {
    Objects.requireNonNull(id, "Id is required");
    Objects.requireNonNull(dto, "EmployeeDto required for updating");
    var dtoId = dto.getId();
    if (dtoId != null && !dtoId.equals(id)) {
      throw new EmployeeIdMismatchException();
    }
    var isAnyFieldNonNull = employeeCustomValidator.validateNotAllNull(dto);
    if (!CollectionUtils.isEmpty(isAnyFieldNonNull)) {
      log.warn("No update found");
      return;
    }
    var dtoLogin = dto.getLogin();
    log.info("Found {}", dtoLogin);
    var existingRecord = employeeWriteRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
    if (dtoLogin != null) {
      employeeWriteRepository.findByIdNotAndLogin(id, dtoLogin).ifPresent(e -> {
        throw new EmployeeLoginNonUniqueException();
      });
    }
    var updatedRecord = employeeMapper.patchEntity(dto, existingRecord);
    employeeWriteRepository.save(updatedRecord);
  }

  @Transactional
  @Retryable(value = {TransientDataAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000L, maxDelay = 3000L))
  public void deleteEmployeeById(String id) {
    try {
      employeeWriteRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new EmployeeNotFoundException(e);
    }
  }

  private List<EmployeeDto> mapCsvDataToDtos(byte[] csvByteContents) throws IOException {
    var inputStream = new ByteArrayInputStream(csvByteContents);
    MappingIterator<EmployeeDto> it = employeeCsvMapper.readValues(inputStream.readAllBytes());
    return it.readAll();
  }

  private long batchUpsert(List<EmployeeDto> dtos) {
    long numUpdatedOrInserted = 0L;
    var inputIdList = dtos.stream()
      .map(EmployeeDto::getId)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    Map<String, Employee> employees = employeeWriteRepository.findByIdIn(inputIdList).stream()
      .collect(Collectors.toMap(Employee::getId, Function.identity()));
    for (var dto : dtos) {
      var id = dto.getId();
      var employee = employees.getOrDefault(id, null);
      if (shouldUpdate(dto, employee)) {
        log.debug("Updating {}", dto.getId());
        numUpdatedOrInserted += 1L;
      }
      employees.put(id, employeeMapper.toEntity(dto, employee));
    }
    log.info("Upserting {} entries", numUpdatedOrInserted);
    try {
      employeeWriteRepository.saveAllAndFlush(employees.values());
      return numUpdatedOrInserted;
    } catch (DataIntegrityViolationException exception) {
      log.error("Error: {}", exception.getMostSpecificCause().getMessage());
      throw new EmployeeCsvValidationException(List.of(Content.ERROR_EMPLOYEE_LOGIN_NOT_UNIQUE));
    }

  }

  private boolean shouldUpdate(EmployeeDto dto, Employee entity) {
    if (entity == null) {
      return true;
    }
    var idEqual = dto.getId().equals(entity.getId());
    var loginEqual = dto.getLogin().equals(entity.getLogin());
    var nameEqual = dto.getName().equals(entity.getName());
    var salaryEqual = dto.getSalary().compareTo(entity.getSalary()) == 0;
    var startDateEqual = dto.getStartDate().equals(entity.getStartDate());
    return !(idEqual && loginEqual && nameEqual && salaryEqual && startDateEqual);
  }

  private void validateNoExistingIdOrLogin(EmployeeDto dto) {
    var id = dto.getId();
    var login = dto.getLogin();
    var existingEmployees = employeeWriteRepository.findByIdOrLogin(id, login);
    for (var employee : existingEmployees) {
      if (employee.getId().equals(id)) {
        throw new EmployeeIdAlreadyExistsException();
      } else if (employee.getLogin().equals(login)) {
        throw new EmployeeLoginNonUniqueException();
      }
    }
  }

  private void saveEmployeeDto(EmployeeDto dto, Employee existingEntity) {
    var employeeEntity = employeeMapper.toEntity(dto, existingEntity);
    employeeWriteRepository.save(employeeEntity);
  }
}
