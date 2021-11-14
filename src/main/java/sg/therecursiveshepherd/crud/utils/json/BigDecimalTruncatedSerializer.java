package sg.therecursiveshepherd.crud.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import sg.therecursiveshepherd.crud.configurations.Content;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalTruncatedSerializer extends JsonSerializer<BigDecimal> {
  @Override
  public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    var value = bigDecimal.setScale(Content.SALARY_SCALE, RoundingMode.HALF_UP);
    jsonGenerator.writeNumber(value);
  }
}
