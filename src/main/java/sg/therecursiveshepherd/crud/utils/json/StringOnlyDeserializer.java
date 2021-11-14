package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class StringOnlyDeserializer extends StdDeserializer<String> {

  private static final long serialVersionUID = -8308645837880778327L;

  public StringOnlyDeserializer() {
    super(String.class);
  }

  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    var token = p.currentToken();
    if (token.isBoolean() || token.isNumeric() || !token.toString().equalsIgnoreCase("value_string")) {
      ctxt.reportWrongTokenException(String.class, JsonToken.VALUE_STRING, "Received " + token.asString());
      return null;
    }
    return StringDeserializer.instance.deserialize(p, ctxt);
  }
}
