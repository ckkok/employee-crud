package sg.therecursiveshepherd.crud.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sg.therecursiveshepherd.crud.dtos.ApiResponseAllEmployeesDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;
import sg.therecursiveshepherd.crud.services.EmployeeReadService;

import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

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
  private static final String PARAM_OFFSET = "offset";
  private static final String PARAM_LIMIT = "limit";

  private static final String DEFAULT_MIN_SALARY = "0";
  private static final String DEFAULT_MAX_SALARY = "4000.00";
  private static final String DEFAULT_OFFSET = "0";
  private static final String DEFAULT_LIMIT = "0";

  private final EmployeeReadService employeeReadService;

  @GetMapping()
  public ResponseEntity<ApiResponseAllEmployeesDto> findAllEmployees(
    @RequestParam(value = PARAM_MIN_SALARY, required = false, defaultValue = DEFAULT_MIN_SALARY) BigDecimal minSalary,
    @RequestParam(value = PARAM_MAX_SALARY, required = false, defaultValue = DEFAULT_MAX_SALARY) BigDecimal maxSalary,
    @RequestParam(value = PARAM_OFFSET, required = false, defaultValue = DEFAULT_OFFSET) @PositiveOrZero(message = ERROR_INVALID_OFFSET) int offset,
    @RequestParam(value = PARAM_LIMIT, required = false, defaultValue = DEFAULT_LIMIT) @PositiveOrZero(message = ERROR_INVALID_LIMIT) int limit
  ) {
    log.info("Min salary: {}, Max salary: {}, Offset: {}, Limit: {}", minSalary, maxSalary, offset, limit);
    var employeeDtoList = employeeReadService.findAllEmployees(minSalary, maxSalary, offset, limit);
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
