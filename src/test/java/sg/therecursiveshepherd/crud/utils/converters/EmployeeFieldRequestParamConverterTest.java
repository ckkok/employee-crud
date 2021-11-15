package sg.therecursiveshepherd.crud.utils.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import sg.therecursiveshepherd.crud.entities.Employee;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeFieldRequestParamConverterTest {

  private EmployeeFieldRequestParamConverter converter;
  private Map<String, Employee.FieldName> enumValueMap;

  @BeforeEach
  void setup() {
    converter = new EmployeeFieldRequestParamConverter();
    enumValueMap = new HashMap<>();
    enumValueMap.put("id", Employee.FieldName.ID);
    enumValueMap.put("login", Employee.FieldName.LOGIN);
    enumValueMap.put("name", Employee.FieldName.NAME);
    enumValueMap.put("salary", Employee.FieldName.SALARY);
    enumValueMap.put("startdate", Employee.FieldName.STARTDATE);
  }

  @ParameterizedTest(name = "{index}: {0}")
  @DisplayName("Converts params to respective field enums irrespective of case")
  @CsvSource({
    "id", "login", "name", "salary", "startdate",
    "ID", "LOGIN", "NAME", "SALARY", "STARTDATE"
  })
  void convert(String value) {
    var result = converter.convert(value);
    var expectedResult = enumValueMap.get(value.toLowerCase(Locale.ROOT));
    assertEquals(expectedResult, result);
  }

  @Test
  @DisplayName("Given invalid field name, throws IllegalArgumentException")
  void givenInvalidFieldNameConvertThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> converter.convert("asdf"));
  }
}
