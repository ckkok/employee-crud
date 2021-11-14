package sg.therecursiveshepherd.crud.services.validators;

import org.springframework.beans.BeanWrapperImpl;
import sg.therecursiveshepherd.crud.annotations.NotAllNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotAllNullConstraintValidator implements ConstraintValidator<NotAllNull, Object> {

  private String[] fieldNames;

  @Override
  public void initialize(NotAllNull annotation) {
    this.fieldNames = annotation.fieldNames();
  }

  @Override
  public boolean isValid(Object object, ConstraintValidatorContext context) {
    if (object == null) {
      return false;
    }
    BeanWrapperImpl beanWrapper = new BeanWrapperImpl(object);
    try {
      for (String fieldName : fieldNames) {
        var property = beanWrapper.getPropertyValue(fieldName);
        if (property != null) {
          return true;
        }
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }
}
