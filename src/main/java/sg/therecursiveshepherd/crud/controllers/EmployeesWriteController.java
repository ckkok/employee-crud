package sg.therecursiveshepherd.crud.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.markers.OnCreateRequest;
import sg.therecursiveshepherd.crud.markers.OnPatchRequest;
import sg.therecursiveshepherd.crud.services.EmployeeWriteService;

import javax.validation.Valid;
import java.io.IOException;

@Profile("write")
@RestController
@Slf4j
@RequestMapping(value = "/users", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class EmployeesWriteController {

  private static final String CSV_FILENAME_FIELD = "file";

  private final EmployeeWriteService employeeWriteService;

  @PostMapping()
  @Validated(OnCreateRequest.class)
  public ResponseEntity<ApiResponseDto<String>> createEmployee(@RequestBody @Valid EmployeeDto dto) {
    log.info("Creating employee: {}", dto);
    employeeWriteService.createEmployee(dto);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_CREATED, HttpStatus.CREATED);
  }

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponseDto<String>> handleUpload(@RequestParam(CSV_FILENAME_FIELD) MultipartFile file) throws IOException {
    long numRecordsCreated = employeeWriteService.handleFileUpload(file);
    var status = numRecordsCreated > 0L ? HttpStatus.CREATED : HttpStatus.OK;
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_CSV_FILE_PROCESSED, status);
  }

  @PutMapping("/{id}")
  @Validated(OnCreateRequest.class)
  public ResponseEntity<ApiResponseDto<String>> replaceEmployeeById(@PathVariable("id") String id, @RequestBody @Valid EmployeeDto dto) {
    log.info("Replace employee id {} with {}", id, dto);
    employeeWriteService.replaceEmployeeById(id, dto);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_UPDATED, HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  @Validated(OnPatchRequest.class)
  public ResponseEntity<ApiResponseDto<String>> updateEmployeeById(@PathVariable("id") String id, @RequestBody @Valid EmployeeDto dto) {
    log.info("Update employee id {} with {}", id, dto);
    employeeWriteService.updateEmployeeById(id, dto);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_UPDATED, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponseDto<String>> deleteEmployeeById(@PathVariable("id") String id) {
    log.info("Delete employee id {}", id);
    employeeWriteService.deleteEmployeeById(id);
    return new ResponseEntity<>(Content.RESPONSE_EMPLOYEE_DELETED, HttpStatus.OK);
  }
}
