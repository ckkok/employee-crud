package sg.therecursiveshepherd.crud.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

public class EmployeeCsvValidationException extends ResponseStatusException {

  private static final long serialVersionUID = 2457734570983281399L;

  @NonNull
  @Getter
  private final List<String> reasonList;

  public EmployeeCsvValidationException(@NonNull List<String> reasonList) {
    super(HttpStatus.BAD_REQUEST);
    Objects.requireNonNull(reasonList, "Reasons for validation failure needed");
    this.reasonList = reasonList;
  }

}
