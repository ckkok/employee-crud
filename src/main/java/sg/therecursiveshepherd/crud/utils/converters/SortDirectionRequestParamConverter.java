package sg.therecursiveshepherd.crud.utils.converters;

import lombok.NoArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort.Direction;

@NoArgsConstructor
public class SortDirectionRequestParamConverter implements Converter<String, Direction> {
  @Override
  public Direction convert(String param) {
    return Direction.fromString(param);
  }
}
