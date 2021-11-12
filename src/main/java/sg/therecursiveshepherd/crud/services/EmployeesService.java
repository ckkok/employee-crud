package sg.therecursiveshepherd.crud.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.DataValidationException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeIdAlreadyExistsException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeLoginNonUniqueException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;
import sg.therecursiveshepherd.crud.repositories.EmployeesRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.services.validators.EmployeeCustomValidator;
import sg.therecursiveshepherd.crud.utils.RangeQuery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeesService {

  private static final BigDecimal MAX_SALARY_CAP = BigDecimal.valueOf(Double.MAX_VALUE);

  private final ObjectReader employeeCsvMapper;
  private final EmployeesRepository employeesRepository;
  private final EmployeeMapper employeeMapper;
  private final EmployeeCustomValidator employeeCustomValidator;

  public EmployeesService(
    @Qualifier("employeeCsvMapper") ObjectReader employeeCsvMapper,
    EmployeeCustomValidator employeeCustomValidator,
    EmployeesRepository employeesRepository,
    EmployeeMapper employeeMapper) {
    this.employeeCsvMapper = employeeCsvMapper;
    this.employeesRepository = employeesRepository;
    this.employeeMapper = employeeMapper;
    this.employeeCustomValidator = employeeCustomValidator;
  }

  @Transactional
  public long handleFileUpload(MultipartFile file) throws IOException {
    log.debug("Received file: {}", file.getOriginalFilename());
    var employeeDtos = mapCsvDataToDtos(file.getBytes());
    log.debug("Found {} records", employeeDtos.size());
    var validationErrors = employeeCustomValidator.validateDtoList(employeeDtos);
    if (validationErrors.isPresent()) {
      var errors = String.join(",\n", validationErrors.get());
      log.error("Validation failed for {}. Reason(s): {}", file.getOriginalFilename(), errors);
      throw new DataValidationException(errors);
    }
    var numUpdatedOrInserted = batchUpsert(employeeDtos);
    log.debug("Saved {} records", numUpdatedOrInserted);
    return numUpdatedOrInserted;
  }

  public List<EmployeeDto> findAllEmployees(BigDecimal minSalary, BigDecimal maxSalary, int offset, int limit) {
    var actualMinSalary = Objects.requireNonNullElse(minSalary, BigDecimal.ZERO);
    var actualMaxSalary = Objects.requireNonNullElse(maxSalary, MAX_SALARY_CAP);
    return employeesRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(
        actualMinSalary, actualMaxSalary,
        RangeQuery.of(offset, 0, limit, Sort.by(Direction.ASC, Employee.FIELD_ID)))
      .stream()
      .map(employeeMapper::toDto)
      .collect(Collectors.toList());
  }

  public Optional<EmployeeDto> findEmployeeById(String id) {
    return employeesRepository.findById(id)
      .map(employeeMapper::toDto);
  }

  @Transactional
  public void createEmployee(EmployeeDto dto) {
    validateNoExistingIdOrLogin(dto);
    var employeeEntity = employeeMapper.toEntity(dto);
    employeesRepository.save(employeeEntity);
  }

  @Transactional
  public void replaceEmployeeById(String id, EmployeeDto dto) {
    var existingRecord = employeesRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
    var updatedRecord = employeeMapper.toEntity(dto, existingRecord);
    employeesRepository.save(updatedRecord);
  }

  @Transactional
  public void updateEmployeeById(String id, EmployeeDto dto) {
    var existingRecord = employeesRepository.findById(id).orElseThrow(EmployeeNotFoundException::new);
    var updatedRecord = employeeMapper.patchEntity(dto, existingRecord);
    employeesRepository.save(updatedRecord);
  }

  @Transactional
  public void deleteEmployeeById(String id) {
    try {
      employeesRepository.deleteById(id);
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
      .map(dto -> dto.getId().orElse(null))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    Map<String, Employee> employees = employeesRepository.findByIdIn(inputIdList).stream()
      .collect(Collectors.toMap(Employee::getId, Function.identity()));
    for (var dto : dtos) {
      var id = dto.getId().orElse(null);
      if (!StringUtils.hasText(id)) {
        continue;
      }
      var employee = employees.getOrDefault(id, null);
      if (shouldUpdate(dto, employee)) {
        log.debug("Updating {}", dto.getId());
        numUpdatedOrInserted += 1L;
      }
      employees.put(id, employeeMapper.toEntity(dto, employee));
    }
    log.info("Upserting {} entries", numUpdatedOrInserted);
    employeesRepository.saveAllAndFlush(employees.values());
    return numUpdatedOrInserted;
  }

  private boolean shouldUpdate(EmployeeDto dto, Employee entity) {
    if (entity == null) {
      return true;
    }
    var idEqual = dto.getId().map(id -> id.equals(entity.getId())).orElse(false);
    var loginEqual = dto.getLogin().map(login -> login.equals(entity.getLogin())).orElse(false);
    var nameEqual = dto.getName().map(name -> name.equals(entity.getName())).orElse(false);
    var salaryEqual = dto.getSalary().map(salary -> salary.compareTo(entity.getSalary()) == 0).orElse(false);
    var startDateEqual = dto.getStartDate().map(date -> date.equals(entity.getStartDate())).orElse(false);
    return !(idEqual && loginEqual && nameEqual && salaryEqual && startDateEqual);
  }

  private void validateNoExistingIdOrLogin(EmployeeDto dto) {
    var id = dto.getId().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_ID));
    var login = dto.getLogin().orElseThrow(() -> new DataValidationException(Content.ERROR_INVALID_LOGIN));
    var existingEmployees = employeesRepository.findByIdOrLogin(id, login);
    for (var employee : existingEmployees) {
      if (employee.getId().equals(id)) {
        throw new EmployeeIdAlreadyExistsException();
      }
      if (employee.getLogin().equals(login)) {
        throw new EmployeeLoginNonUniqueException();
      }
    }
  }

}
