package sg.therecursiveshepherd.crud.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.utils.RangeQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Profile("read")
@Slf4j
public class EmployeeReadService {

  private static final BigDecimal MAX_SALARY_CAP = BigDecimal.valueOf(Double.MAX_VALUE);

  private final EmployeeReadRepository employeeReadRepository;
  private final EmployeeMapper employeeMapper;

  public EmployeeReadService(
    EmployeeReadRepository employeeReadRepository,
    EmployeeMapper employeeMapper) {
    this.employeeReadRepository = employeeReadRepository;
    this.employeeMapper = employeeMapper;
  }

  public List<EmployeeDto> findAllEmployees(BigDecimal minSalary, BigDecimal maxSalary, int offset, int limit) {
    var actualMinSalary = Objects.requireNonNullElse(minSalary, BigDecimal.ZERO);
    var actualMaxSalary = Objects.requireNonNullElse(maxSalary, MAX_SALARY_CAP);
    return employeeReadRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(
        actualMinSalary, actualMaxSalary,
        RangeQuery.of(offset, 0, limit, Sort.by(Direction.ASC, Employee.FIELD_ID)))
      .stream()
      .map(employeeMapper::toDto)
      .collect(Collectors.toList());
  }

  public Optional<EmployeeDto> findEmployeeById(String id) {
    return employeeReadRepository.findById(id)
      .map(employeeMapper::toDto);
  }

}
