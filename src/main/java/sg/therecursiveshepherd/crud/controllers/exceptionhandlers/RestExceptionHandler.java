package sg.therecursiveshepherd.crud.controllers.exceptionhandlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.exceptions.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  ResponseEntity<ApiResponseDto<String>> onMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
    var response = new ApiResponseDto<>(String.format(Content.ERROR_INVALID_FIELD_TEMPLATE, exception.getParameter().getParameterName()));
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  ResponseEntity<ApiResponseDto<String>> onConstraintViolationException(ConstraintViolationException exception) {
    var errors = exception.getConstraintViolations().stream()
      .findFirst() // Report only the first
      .map(ConstraintViolation::getMessage);
    var response = errors.map(ApiResponseDto::new).orElse(Content.RESPONSE_INVALID_INPUT);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({EmployeeCsvValidationException.class})
  ResponseEntity<ApiResponseDto<List<String>>> onEmployeeCsvDataValidationException(EmployeeCsvValidationException exception) {
    var response = new ApiResponseDto<>(exception.getReasonList());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({
    EmployeeNotFoundException.class, EmployeeIdAlreadyExistsException.class, EmployeeLoginNonUniqueException.class,
    EmployeeIdMismatchException.class})
  ResponseEntity<ApiResponseDto<String>> onEmployeeDataValidationException(ResponseStatusException exception) {
    var response = new ApiResponseDto<>(exception.getReason());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  ResponseEntity<ApiResponseDto<String>> onHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
    var cause = exception.getCause();
    if (cause instanceof JsonMappingException) {
      var mismatchedInputException = (JsonMappingException) cause;
      var fieldName = getJsonFieldName(mismatchedInputException);
      var response = new ApiResponseDto<>(String.format(Content.ERROR_INVALID_FIELD_TEMPLATE, fieldName));
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(Content.RESPONSE_INVALID_INPUT, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
  ResponseEntity<ApiResponseDto<String>> onHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    return new ResponseEntity<>(Content.RESPONSE_METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler({Exception.class})
  ResponseEntity<ApiResponseDto<String>> catchAllExceptionHandler(Exception exception) {
    log.error("Server error", exception);
    return new ResponseEntity<>(Content.RESPONSE_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getJsonFieldName(JsonMappingException e) {
    return e.getPath().stream()
      .filter(ref -> ref.getFieldName() != null)
      .findFirst()
      .map(JsonMappingException.Reference::getFieldName)
      .orElse(null);
  }
}
