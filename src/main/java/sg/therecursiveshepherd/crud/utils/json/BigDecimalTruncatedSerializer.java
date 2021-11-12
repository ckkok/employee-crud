package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import sg.therecursiveshepherd.crud.configurations.Content;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class BigDecimalTruncatedSerializer extends JsonSerializer<Optional<BigDecimal>> {
  @Override
  public void serialize(Optional<BigDecimal> bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    var value = bigDecimal.map(b -> b.setScale(Content.SALARY_SCALE, RoundingMode.HALF_EVEN)).orElse(null);
    jsonGenerator.writeNumber(value);
  }
}
