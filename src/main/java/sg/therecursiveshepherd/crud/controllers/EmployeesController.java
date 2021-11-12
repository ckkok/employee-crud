package sg.therecursiveshepherd.crud.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.ApiResponseAllEmployeesDto;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;
import sg.therecursiveshepherd.crud.services.EmployeesService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.math.BigDecimal;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class EmployeesController {

  private final EmployeesService employeesService;

  @GetMapping()
  public ResponseEntity<ApiResponseAllEmployeesDto> findAllEmployees(
    @RequestParam(value = "minSalary", required = false, defaultValue = "0") BigDecimal minSalary,
    @RequestParam(value = "maxSalary", required = false, defaultValue = "4000.00") BigDecimal maxSalary,
    @RequestParam(value = "offset", required = false, defaultValue = "0") @PositiveOrZero int offset,
    @RequestParam(value = "limit", required = false, defaultValue = "0") @PositiveOrZero int limit
  ) {
    log.info("Min salary: {}, Max salary: {}, Offset: {}, Limit: {}", minSalary, maxSalary, offset, limit);
    var employeeDtoList = employeesService.findAllEmployees(minSalary, maxSalary, offset, limit);
    var responseBody = new ApiResponseAllEmployeesDto(employeeDtoList);
    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<ApiResponseDto> createEmployee(@RequestBody @Valid EmployeeDto dto) {
    log.info("Employee: {}", dto);
    employeesService.createEmployee(dto);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_CREATED, HttpStatus.CREATED);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponseDto> handleUpload(@RequestParam("file") MultipartFile file) throws IOException {
    long numRecordsCreated = employeesService.handleFileUpload(file);
    var status = numRecordsCreated > 0L ? HttpStatus.CREATED : HttpStatus.OK;
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_CSV_FILE_PROCESSED, status);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable("id") String id) {
    var employeeDto = employeesService.findEmployeeById(id)
      .orElseThrow(EmployeeNotFoundException::new);
    return new ResponseEntity<>(employeeDto, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponseDto> replaceEmployeeById(@PathVariable("id") String id, @RequestBody @Valid EmployeeDto dto) {
    employeesService.replaceEmployeeById(id, dto);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_UPDATED, HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponseDto> updateEmployeeById(@PathVariable("id") String id, @RequestBody EmployeeDto dto) {
    employeesService.updateEmployeeById(id, dto);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_UPDATED, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponseDto> deleteEmployeeById(@PathVariable("id") String id) {
    employeesService.deleteEmployeeById(id);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_DELETED, HttpStatus.OK);
  }
}
