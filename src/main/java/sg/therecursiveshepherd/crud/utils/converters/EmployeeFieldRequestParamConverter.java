package sg.therecursiveshepherd.crud.utils.converters;

import lombok.NoArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import sg.therecursiveshepherd.crud.entities.Employee;

@NoArgsConstructor
public class EmployeeFieldRequestParamConverter implements Converter<String, Employee.FieldName> {

  @Override
  public Employee.FieldName convert(String param) {
    return Employee.FieldName.fromString(param);
  }

}
