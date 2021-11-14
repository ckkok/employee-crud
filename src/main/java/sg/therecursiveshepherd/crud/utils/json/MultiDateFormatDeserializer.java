package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.util.StringUtils;
import sg.therecursiveshepherd.crud.configurations.Content;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class MultiDateFormatDeserializer extends StdDeserializer<LocalDate> {

  private static final long serialVersionUID = -7842262609402911573L;

  public MultiDateFormatDeserializer() {
    this(null);
  }

  protected MultiDateFormatDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode node = jsonParser.getCodec().readTree(jsonParser);
    var dateString = node.textValue();
    if (!StringUtils.hasText(dateString)) {
      return null;
    }
    for (var formatter : Content.EMPLOYEE_START_DATE_FORMATTERS) {
      try {
        return LocalDate.parse(dateString, formatter);
      } catch (DateTimeParseException e) {
        // Fail silently here and try the next one. Throw only if all fails
      }
    }
    return null;
  }
}
