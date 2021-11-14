package sg.therecursiveshepherd.crud.services.validators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sg.therecursiveshepherd.crud.annotations.NotAllNull;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.markers.PrePatch;

import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

class NotAllNullConstraintValidatorTest {

  @Test
  @DisplayName("Validates dto as false given null dto")
  void isValidReturnsFalseGivenNullDto() {
    var validator = new NotAllNullConstraintValidator();
    var fieldNames = new String[]{"id"};
    var groups = new Class<?>[]{PrePatch.class};
    var annotation = getMockAnnotation(fieldNames, groups);
    validator.initialize(annotation);
    var result = validator.isValid(null, null);
    assertFalse(result);
  }

  @Test
  @DisplayName("Validates dto as false if an exception is thrown")
  void isValidReturnsFalseIfExceptionIsThrown() {
    var validator = new NotAllNullConstraintValidator();
    var fieldNames = new String[]{"abc"};
    var groups = new Class<?>[]{PrePatch.class};
    var annotation = getMockAnnotation(fieldNames, groups);
    validator.initialize(annotation);
    var dto = new EmployeeDto();
    var result = validator.isValid(dto, null);
    assertFalse(result);
  }

  private NotAllNull getMockAnnotation(String[] fieldNames, Class<?>[] groups) {
    return new NotAllNull() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public String message() {
        return "Invalid input - at least one field must not be null";
      }

      @Override
      public Class<?>[] groups() {
        return groups;
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }

      @Override
      public String[] fieldNames() {
        return fieldNames;
      }
    };
  }
}
