package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;
import java.util.Optional;

public class StringOnlyDeserializer extends StdDeserializer<Optional<String>> {

  private static final long serialVersionUID = -8308645837880778327L;

  public StringOnlyDeserializer() {
    super(Optional.class);
  }

  @Override
  public Optional<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    var token = p.currentToken();
    if (token.isBoolean() || token.isNumeric() || !token.toString().equalsIgnoreCase("value_string")) {
      ctxt.reportWrongTokenException(String.class, token, "Not a string");
      return Optional.empty();
    }
    return Optional.of(StringDeserializer.instance.deserialize(p, ctxt));
  }
}
