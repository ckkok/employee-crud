package sg.therecursiveshepherd.crud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sg.therecursiveshepherd.crud.configurations.Content;

public class EmployeeIdAlreadyExistsException extends ResponseStatusException {

  private static final long serialVersionUID = 1252425729001543523L;

  public EmployeeIdAlreadyExistsException() {
    super(HttpStatus.BAD_REQUEST, Content.ERROR_EMPLOYEE_ID_ALREADY_EXISTS);
  }

}
