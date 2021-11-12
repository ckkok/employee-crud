package sg.therecursiveshepherd.crud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public class DataValidationException extends ResponseStatusException {

  private static final long serialVersionUID = 2457734570983281399L;

  public DataValidationException(@NonNull String reason) {
    super(HttpStatus.BAD_REQUEST, reason);
    Objects.requireNonNull(reason, "Reason is required");
  }

}
