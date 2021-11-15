package sg.therecursiveshepherd.crud.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sg.therecursiveshepherd.crud.dtos.ApiResponseAllEmployeesDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeQuerySpecDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;
import sg.therecursiveshepherd.crud.services.EmployeeReadService;

import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Profile("read")
@RestController
@Slf4j
@RequestMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class EmployeesReadController {

  private static final String ERROR_INVALID_OFFSET = "{controller.employees.invalidOffset}";
  private static final String ERROR_INVALID_LIMIT = "{controller.employees.invalidLimit}";

  private static final String PARAM_MIN_SALARY = "minSalary";
  private static final String PARAM_MAX_SALARY = "maxSalary";
  private static final String PARAM_NAME = "name";
  private static final String PARAM_LOGIN = "login";
  private static final String PARAM_MIN_STARTDATE = "minStartDate";
  private static final String PARAM_MAX_STARTDATE = "maxStartDate";
  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_LIMIT = "limit";
  private static final String PARAM_SORT_BY = "sortBy";
  private static final String PARAM_SORT_DIR = "sortDir";

  private static final String DEFAULT_MIN_SALARY = "0";
  private static final String DEFAULT_MAX_SALARY = "4000.00";
  private static final String DEFAULT_MIN_STARTDATE = "1900-01-01";
  private static final String DEFAULT_MAX_STARTDATE = "3000-12-31";
  private static final String DEFAULT_OFFSET = "0";
  private static final String DEFAULT_LIMIT = "0";
  private static final String DEFAULT_SORT_FIELD = "id";
  private static final String DEFAULT_SORT_ORDER = "asc";

  private final EmployeeReadService employeeReadService;

  @GetMapping("")
  public ResponseEntity<ApiResponseAllEmployeesDto> findAllEmployees(
    @RequestParam(value = PARAM_LOGIN, required = false) String login,
    @RequestParam(value = PARAM_NAME, required = false) String name,
    @RequestParam(value = PARAM_MIN_STARTDATE, required = false, defaultValue = DEFAULT_MIN_STARTDATE) LocalDate minStartDate,
    @RequestParam(value = PARAM_MAX_STARTDATE, required = false, defaultValue = DEFAULT_MAX_STARTDATE) LocalDate maxStartDate,
    @RequestParam(value = PARAM_MIN_SALARY, required = false, defaultValue = DEFAULT_MIN_SALARY) BigDecimal minSalary,
    @RequestParam(value = PARAM_MAX_SALARY, required = false, defaultValue = DEFAULT_MAX_SALARY) BigDecimal maxSalary,
    @RequestParam(value = PARAM_OFFSET, required = false, defaultValue = DEFAULT_OFFSET) @PositiveOrZero(message = ERROR_INVALID_OFFSET) int offset,
    @RequestParam(value = PARAM_LIMIT, required = false, defaultValue = DEFAULT_LIMIT) @PositiveOrZero(message = ERROR_INVALID_LIMIT) int limit,
    @RequestParam(value = PARAM_SORT_BY, required = false, defaultValue = DEFAULT_SORT_FIELD) Employee.FieldName sortBy,
    @RequestParam(value = PARAM_SORT_DIR, required = false, defaultValue = DEFAULT_SORT_ORDER) Sort.Direction sortDir
  ) {
    var querySpec = EmployeeQuerySpecDto.builder()
      .login(login).name(name)
      .dateRangeQuery(new EmployeeQuerySpecDto.DateRangeQuery(minStartDate, maxStartDate))
      .salaryRangeQuery(new EmployeeQuerySpecDto.SalaryRangeQuery(minSalary, maxSalary))
      .build();
    log.info("Query: {}, Offset: {}, Limit: {}, SortBy: {}, SortDir: {}", querySpec, offset, limit, sortBy, sortDir);
    var employeeDtoList = employeeReadService.findAllEmployees(querySpec, offset, limit, sortBy, sortDir);
    var responseBody = new ApiResponseAllEmployeesDto(employeeDtoList);
    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable("id") String id) {
    var employeeDto = employeeReadService.findEmployeeById(id)
      .orElseThrow(EmployeeNotFoundException::new);
    return new ResponseEntity<>(employeeDto, HttpStatus.OK);
  }

}
