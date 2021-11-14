package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BigDecimalTruncatedSerializerTest {

  @ParameterizedTest(name = "{index}: BigDecimalTruncatedSerializer rounds {0} to {1}")
  @CsvSource({
    "1234.323,1234.32",
    "1234.325,1234.33"
  })
  @DisplayName("BigDecimalTruncatedSerializer serializes numbers rounded off to 2 decimal places")
  void bigDecimalTruncatedSerializerRoundsOffTo2DecimalPlaces(String value, String expectedValue) throws IOException {
    var serializer = new BigDecimalTruncatedSerializer();
    var mockJsonGenerator = Mockito.mock(JsonGenerator.class);
    var valueToSerialize = new BigDecimal(value);
    serializer.serialize(valueToSerialize, mockJsonGenerator, null);
    var serializedValueCaptor = ArgumentCaptor.forClass(BigDecimal.class);
    Mockito.verify(mockJsonGenerator, Mockito.times(1)).writeNumber(serializedValueCaptor.capture());
    assertEquals(0, serializedValueCaptor.getValue().compareTo(new BigDecimal(expectedValue)));
  }
}
