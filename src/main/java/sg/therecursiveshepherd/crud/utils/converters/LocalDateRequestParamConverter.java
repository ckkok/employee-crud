package sg.therecursiveshepherd.crud.utils.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateRequestParamConverter implements Converter<String, LocalDate> {

  private final DateTimeFormatter dateTimeFormatter;

  public LocalDateRequestParamConverter(String format) {
    this.dateTimeFormatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
  }

  @Override
  public LocalDate convert(String param) {
    return LocalDate.parse(param, this.dateTimeFormatter);
  }

}
