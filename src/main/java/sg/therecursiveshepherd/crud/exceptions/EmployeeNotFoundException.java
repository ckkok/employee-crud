package sg.therecursiveshepherd.crud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sg.therecursiveshepherd.crud.configurations.Content;

public class EmployeeNotFoundException extends ResponseStatusException {

  public EmployeeNotFoundException() {
    super(HttpStatus.BAD_REQUEST, Content.ERROR_EMPLOYEE_NOT_FOUND);
  }

  public EmployeeNotFoundException(Throwable cause) {
    super(HttpStatus.BAD_REQUEST, Content.ERROR_EMPLOYEE_NOT_FOUND, cause);
  }

}
