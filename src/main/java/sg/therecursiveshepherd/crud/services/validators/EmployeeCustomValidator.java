package sg.therecursiveshepherd.crud.services.validators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.markers.OnCreateRequest;
import sg.therecursiveshepherd.crud.markers.PrePatch;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeCustomValidator {

  private final Validator validator;

  @NonNull
  public List<String> validateDtoList(List<EmployeeDto> dtos) {
    var idsSeen = new HashSet<String>();
    var loginsSeen = new HashSet<String>();
    var duplicateIds = new HashSet<String>();
    var duplicateLogins = new HashSet<String>();
    List<String> errors = null;
    for (int i = 0; i < dtos.size(); i++) {
      var dto = dtos.get(i);
      Set<ConstraintViolation<EmployeeDto>> violations = validator.validate(dto, OnCreateRequest.class);
      if (!CollectionUtils.isEmpty(violations)) {
        errors = ensureList(errors);
        var errorMessage = "Row " + (i + 1) + ": " + violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        errors.add(errorMessage);
      }
      var id = dto.getId();
      var login = dto.getLogin();
      if (!idsSeen.add(id)) {
        duplicateIds.add(id);
      }
      if (!loginsSeen.add(login)) {
        duplicateLogins.add(login);
      }
    }
    if (!CollectionUtils.isEmpty(duplicateIds)) {
      errors = ensureList(errors);
      errors.add(String.format("Duplicate ids in file: %s", String.join(",", duplicateIds)));
    }
    if (!CollectionUtils.isEmpty(duplicateLogins)) {
      errors = ensureList(errors);
      errors.add(String.format("Duplicate logins in file: %s", String.join(",", duplicateLogins)));
    }
    return errors == null ? Collections.emptyList() : errors;
  }

  public Set<ConstraintViolation<EmployeeDto>> validateNotAllNull(EmployeeDto dto) {
    return validator.validate(dto, PrePatch.class);
  }

  private List<String> ensureList(List<String> errors) {
    if (errors == null) {
      return new ArrayList<>();
    }
    return errors;
  }
}
