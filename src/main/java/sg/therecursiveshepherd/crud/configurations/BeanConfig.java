package sg.therecursiveshepherd.crud.configurations;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;


@Configuration
public class BeanConfig {

  @Bean("employeeCsvMapper")
  public ObjectReader employeeCsvMapper() {
    var schema = CsvSchema.emptySchema()
      .withComments()
      .withHeader();
    var csvMapper = new CsvMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new ParameterNamesModule())
      .registerModule(new JavaTimeModule());
    return csvMapper.readerFor(EmployeeDto.class)
      .with(schema);
  }

  @Bean
  public MappedInterceptor mappedInterceptor(HandlerInterceptor requestTimingInterceptor) {
    var pathsToTime = new String[]{"/**"};
    return new MappedInterceptor(pathsToTime, requestTimingInterceptor);
  }

}
