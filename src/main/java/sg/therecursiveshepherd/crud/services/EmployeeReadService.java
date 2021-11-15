package sg.therecursiveshepherd.crud.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeQuerySpecDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.entities.Employee.FieldName;
import sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.utils.RangeQuery;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Profile("read")
@Slf4j
public class EmployeeReadService {

  private final EmployeeReadRepository employeeReadRepository;
  private final EmployeeMapper employeeMapper;
  private final Sort.Order defaultSortOrder;
  private final Sort defaultSort;

  public EmployeeReadService(
    EmployeeReadRepository employeeReadRepository,
    EmployeeMapper employeeMapper,
    @Qualifier("defaultSortOrderForEmployees") Sort.Order defaultSortOrder) {
    this.employeeReadRepository = employeeReadRepository;
    this.employeeMapper = employeeMapper;
    this.defaultSortOrder = defaultSortOrder;
    this.defaultSort = Sort.by(defaultSortOrder);
  }

  public List<EmployeeDto> findAllEmployees(EmployeeQuerySpecDto querySpecDto, int offset, int limit, FieldName sortBy, Direction sortDir) {
    var queryIndex = getQueryIndex(querySpecDto.getLogin(), querySpecDto.getName());
    var actualSort = getSortRequest(sortBy, sortDir);
    var rangeQuery = RangeQuery.of(offset, 0, limit, actualSort);
    var employeeList = makeQuery(queryIndex,querySpecDto, rangeQuery);
    return employeeList.stream()
      .map(employeeMapper::toDto)
      .collect(Collectors.toList());
  }

  public Optional<EmployeeDto> findEmployeeById(String id) {
    return employeeReadRepository.findById(id)
      .map(employeeMapper::toDto);
  }

  private Sort getSortRequest(FieldName sortBy, Direction sortDir) {
    var actualField = Objects.requireNonNullElse(sortBy, Employee.FieldName.ID);
    var actualDir = Objects.requireNonNullElse(sortDir, Direction.ASC);
    if (actualField == Employee.FieldName.ID) {
      if (actualDir.isAscending()) {
        return defaultSort;
      }
      return Sort.by(actualDir, actualField.getDbColumnName());
    }
    return Sort.by(new Sort.Order(actualDir, actualField.getDbColumnName()), defaultSortOrder);
  }

  private List<Employee> makeQuery(int queryIndex, EmployeeQuerySpecDto querySpecDto, Pageable pageable) {
    var minSalary = querySpecDto.getSalaryRangeQuery().getMinSalary();
    var maxSalary = querySpecDto.getSalaryRangeQuery().getMaxSalary();
    var minStartDate = querySpecDto.getDateRangeQuery().getMinStartDate();
    var maxStartDate = querySpecDto.getDateRangeQuery().getMaxStartDate();
    var login = querySpecDto.getLogin();
    var name = querySpecDto.getName();
    switch(queryIndex) {
      case 12:
        return employeeReadRepository.findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(minSalary, maxSalary, minStartDate, maxStartDate, pageable);
      case 13:
        return employeeReadRepository.findByLoginAndSalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(login, minSalary, maxSalary, minStartDate, maxStartDate, pageable);
      case 14:
        return employeeReadRepository.findByNameContainingAndSalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(name, minSalary, maxSalary, minStartDate, maxStartDate, pageable);
      case 15:
        return employeeReadRepository.findByLoginAndNameContainingAndSalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(login, name, minSalary, maxSalary, minStartDate, maxStartDate, pageable);
      default:
        throw new IllegalArgumentException(String.format("Invalid query index %d from query parameters", queryIndex));
    }
  }

  private int getQueryIndex(String login, String name) {
    var result = 0;
    if (StringUtils.hasLength(login)) {
      result += 1;
    }
    if (StringUtils.hasLength(name)) {
      result += 2;
    }
    result += 12;
    return result;
  }
}
