package sg.therecursiveshepherd.crud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sg.therecursiveshepherd.crud.configurations.Content;

public class EmployeeLoginNonUniqueException extends ResponseStatusException {

  private static final long serialVersionUID = 9214626877010085636L;

  public EmployeeLoginNonUniqueException() {
    super(HttpStatus.BAD_REQUEST, Content.ERROR_EMPLOYEE_LOGIN_NOT_UNIQUE);
  }

}
