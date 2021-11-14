package sg.therecursiveshepherd.crud.configurations;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableRetry
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

  @Bean
  @Profile("dev")
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.basePackage("sg.therecursiveshepherd.crud.controllers"))
      .paths(PathSelectors.any())
      .build();
  }

}
