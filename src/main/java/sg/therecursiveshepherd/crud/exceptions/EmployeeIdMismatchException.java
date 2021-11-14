package sg.therecursiveshepherd.crud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import sg.therecursiveshepherd.crud.configurations.Content;

public class EmployeeIdMismatchException extends ResponseStatusException {

  private static final long serialVersionUID = 2439930154543287514L;

  public EmployeeIdMismatchException() {
    super(HttpStatus.BAD_REQUEST, Content.ERROR_EMPLOYEE_ID_MISMATCH);
  }

}
