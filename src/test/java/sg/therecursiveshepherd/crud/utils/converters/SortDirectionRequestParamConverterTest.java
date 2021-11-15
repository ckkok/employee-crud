package sg.therecursiveshepherd.crud.utils.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SortDirectionRequestParamConverterTest {

  private SortDirectionRequestParamConverter converter;
  private Map<String, Sort.Direction> enumValueMap;

  @BeforeEach
  void setup() {
    converter = new SortDirectionRequestParamConverter();
    enumValueMap = new HashMap<>();
    enumValueMap.put("asc", Sort.Direction.ASC);
    enumValueMap.put("desc", Sort.Direction.DESC);
  }

  @ParameterizedTest(name = "{index}: {0}")
  @DisplayName("Converts params to respective field enums irrespective of case")
  @CsvSource({
    "asc", "desc",
    "ASC", "DESC"
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
