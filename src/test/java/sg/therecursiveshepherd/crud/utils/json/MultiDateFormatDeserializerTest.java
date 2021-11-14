package sg.therecursiveshepherd.crud.utils.json;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MultiDateFormatDeserializerTest {

  @ParameterizedTest(name = "{index}: Parses {0} into local date with year {1}, month {2}, day {3}")
  @CsvSource({
    "16-Nov-01,2001,11,16",
    "2003-06-21,2003,6,21",
  })
  @DisplayName("MultiDateFormatDeserializer parses dd-MMM-yy and yyyy-MM-dd formats")
  void multiDateFormatDeserializerParsesSupportedFormats(String testDateString, int year, int month, int dayOfMonth) throws IOException {
    var deserializer = new MultiDateFormatDeserializer();
    var mockJsonParser = getMockJsonParser(testDateString);
    var deserializedDate = deserializer.deserialize(mockJsonParser, null);
    assertEquals(LocalDate.of(year, month, dayOfMonth), deserializedDate);
  }

  @ParameterizedTest(name = "{index}: Returns null for date: {0}")
  @CsvSource({
    "''",
    "2003-6-21",
  })
  @DisplayName("MultiDateFormatDeserializer parses dd-MMM-yy and yyyy-MM-dd formats")
  void multiDateFormatDeserializerReturnsNullForUnsupportedDateFormats(String testDateString) throws IOException {
    var deserializer = new MultiDateFormatDeserializer();
    var mockJsonParser = getMockJsonParser(testDateString);
    var deserializedDate = deserializer.deserialize(mockJsonParser, null);
    assertNull(deserializedDate);
  }

  private JsonParser getMockJsonParser(String testDateString) throws IOException {
    var mockJsonParser = mock(JsonParser.class);
    var mockCodec = mock(ObjectCodec.class);
    var testJsonNode = mock(JsonNode.class);
    when(mockJsonParser.getCodec()).thenReturn(mockCodec);
    when(mockCodec.readTree(mockJsonParser)).thenReturn(testJsonNode);
    when(testJsonNode.textValue()).thenReturn(testDateString);
    return mockJsonParser;
  }
}
