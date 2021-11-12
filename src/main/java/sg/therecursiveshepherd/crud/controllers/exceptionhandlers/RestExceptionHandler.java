package sg.therecursiveshepherd.crud.controllers.exceptionhandlers;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sg.therecursiveshepherd.crud.configurations.Content;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.exceptions.DataValidationException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeIdAlreadyExistsException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeLoginNonUniqueException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class})
  ResponseEntity<ApiResponseDto> employeeDtoValidationExceptionHandler(MethodArgumentNotValidException exception) {
    var errors = exception.getBindingResult().getAllErrors();
    if (errors.isEmpty()) {
      return new ResponseEntity<>(Content.RESPONSE_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Report only the first
    var error = errors.get(0).getDefaultMessage();
    var response = new ApiResponseDto(error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({DataValidationException.class})
  ResponseEntity<ApiResponseDto> dataValidationExceptionHandler(DataValidationException exception) {
    var response = new ApiResponseDto(exception.getReason());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({EmployeeNotFoundException.class})
  ResponseEntity<ApiResponseDto> employeeNotFoundHandler(EmployeeNotFoundException exception) {
    var response = new ApiResponseDto(exception.getReason());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({EmployeeIdAlreadyExistsException.class})
  ResponseEntity<ApiResponseDto> employeeIdAlreadyExistsHandler(EmployeeIdAlreadyExistsException exception) {
    var response = new ApiResponseDto(exception.getReason());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({EmployeeLoginNonUniqueException.class})
  ResponseEntity<ApiResponseDto> employeeLoginNonUniqueHandler(EmployeeLoginNonUniqueException exception) {
    var response = new ApiResponseDto(exception.getReason());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  ResponseEntity<ApiResponseDto> invalidFormatHandler(HttpMessageNotReadableException exception) {
    var cause = exception.getCause();
    if (cause instanceof MismatchedInputException) {
      var mismatchedInputException = (MismatchedInputException) cause;
      var fieldName = getJsonFieldName(mismatchedInputException);
      var response = new ApiResponseDto(String.format(Content.ERROR_INVALID_FIELD_TEMPLATE, fieldName));
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(Content.RESPONSE_INVALID_INPUT, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({Exception.class})
  ResponseEntity<ApiResponseDto> catchAllExceptionHandler(Exception exception) {
    log.error("Server error", exception);
    return new ResponseEntity<>(Content.RESPONSE_INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String getJsonFieldName(MismatchedInputException e) {
    return e.getPath().stream()
      .filter(ref -> ref.getFieldName() != null)
      .findFirst()
      .map(JsonMappingException.Reference::getFieldName)
      .orElse(null);
  }
}
