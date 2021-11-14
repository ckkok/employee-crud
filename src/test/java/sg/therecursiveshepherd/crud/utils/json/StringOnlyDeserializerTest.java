package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

    class StringOnlyDeserializerTest {

      @Test
      @DisplayName("StringOnlyDeserializer returns null for boolean tokens")
      void stringOnlyDeserializerReturnsNullForBooleanTokens() throws IOException {
        var deserializer = new StringOnlyDeserializer();
        var mockDeserializationContext = mock(DeserializationContext.class);
        var mockJsonParser = getMockJsonParser(JsonToken.VALUE_TRUE, null);
        var result = deserializer.deserialize(mockJsonParser, mockDeserializationContext);
        assertNull(result);
        verify(mockDeserializationContext)
          .reportWrongTokenException(any(Class.class), eq(JsonToken.VALUE_STRING), anyString());
  }

  @Test
  @DisplayName("StringOnlyDeserializer returns null for numeric tokens")
  void stringOnlyDeserializerReturnsNullForNumericTokens() throws IOException {
    var deserializer = new StringOnlyDeserializer();
    var mockDeserializationContext = mock(DeserializationContext.class);
    var mockJsonParser = getMockJsonParser(JsonToken.VALUE_NUMBER_FLOAT, null);
    var result = deserializer.deserialize(mockJsonParser, mockDeserializationContext);
    assertNull(result);
    verify(mockDeserializationContext)
      .reportWrongTokenException(any(Class.class), eq(JsonToken.VALUE_STRING), anyString());
  }

  @Test
  @DisplayName("StringOnlyDeserializer returns null for array tokens")
  void stringOnlyDeserializerReturnsNullForArrayTokens() throws IOException {
    var deserializer = new StringOnlyDeserializer();
    var mockDeserializationContext = mock(DeserializationContext.class);
    var mockJsonParser = getMockJsonParser(JsonToken.START_ARRAY, null);
    var result = deserializer.deserialize(mockJsonParser, mockDeserializationContext);
    assertNull(result);
    verify(mockDeserializationContext)
      .reportWrongTokenException(any(Class.class), eq(JsonToken.VALUE_STRING), anyString());
  }

  @Test
  @DisplayName("StringOnlyDeserializer delegates to string deserializer for string tokens")
  void stringOnlyDeserializerDelegatesToStringDeserializerForStringTokens() throws IOException {
    var deserializer = new StringOnlyDeserializer();
    var mockDeserializationContext = mock(DeserializationContext.class);
    var mockJsonParser = getMockJsonParser(JsonToken.VALUE_STRING, null);
    var result = deserializer.deserialize(mockJsonParser, mockDeserializationContext);
    assertNull(result);
    verify(mockJsonParser).hasToken(JsonToken.VALUE_STRING);
    verify(mockJsonParser).getText();
  }

  private JsonParser getMockJsonParser(JsonToken currentToken, String optionalStringValue) throws IOException {
    var mockJsonParser = mock(JsonParser.class);
    when(mockJsonParser.currentToken()).thenReturn(currentToken);
    if (currentToken == JsonToken.VALUE_STRING) {
      when(mockJsonParser.hasToken(JsonToken.VALUE_STRING)).thenReturn(true);
      when(mockJsonParser.getText()).thenReturn(optionalStringValue);
    }
    return mockJsonParser;
  }
}
