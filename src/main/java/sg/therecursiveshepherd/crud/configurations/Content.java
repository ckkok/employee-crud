package sg.therecursiveshepherd.crud.configurations;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Content {

  public static final List<DateTimeFormatter> EMPLOYEE_START_DATE_FORMATTERS;
  public static final String ERROR_EMPLOYEE_ID_ALREADY_EXISTS;
  public static final String ERROR_EMPLOYEE_LOGIN_NOT_UNIQUE;
  public static final String ERROR_EMPLOYEE_NOT_FOUND;
  public static final String ERROR_INVALID_FIELD_TEMPLATE;
  public static final String ERROR_INVALID_ID;
  public static final String ERROR_INVALID_LOGIN;
  public static final String ERROR_INVALID_NAME;
  public static final String ERROR_INVALID_SALARY;
  public static final String ERROR_INVALID_DATE;
  public static final int SALARY_SCALE;

  public static final ApiResponseDto RESPONSE_EMPLOYEE_CREATED;
  public static final ApiResponseDto RESPONSE_EMPLOYEE_CSV_FILE_PROCESSED;
  public static final ApiResponseDto RESPONSE_EMPLOYEE_UPDATED;
  public static final ApiResponseDto RESPONSE_EMPLOYEE_DELETED;
  public static final ApiResponseDto RESPONSE_INTERNAL_SERVER_ERROR;
  public static final ApiResponseDto RESPONSE_INVALID_INPUT;

  static {
    YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
    yamlMapFactoryBean.setResources(new ClassPathResource("content.yml"));
    Properties properties = Objects.requireNonNull(yamlMapFactoryBean.getObject(), "Content file not found");

    EMPLOYEE_START_DATE_FORMATTERS = List.of(
      DateTimeFormatter.ofPattern(properties.getProperty("content.employeeStartDateFormat[0]")),
      DateTimeFormatter.ofPattern(properties.getProperty("content.employeeStartDateFormat[1]"))
    );

    SALARY_SCALE = Integer.parseInt(properties.getProperty("content.salaryScale"), 10);

    RESPONSE_EMPLOYEE_DELETED = new ApiResponseDto(properties.getProperty("content.success.employeeDeleted"));
    RESPONSE_EMPLOYEE_UPDATED = new ApiResponseDto(properties.getProperty("content.success.employeeUpdated"));
    RESPONSE_EMPLOYEE_CREATED = new ApiResponseDto(properties.getProperty("content.success.employeeCreated"));
    RESPONSE_EMPLOYEE_CSV_FILE_PROCESSED = new ApiResponseDto(properties.getProperty("content.success.employeeFileUploaded"));
    RESPONSE_INTERNAL_SERVER_ERROR = new ApiResponseDto(properties.getProperty("content.errors.serverError"));
    RESPONSE_INVALID_INPUT = new ApiResponseDto(properties.getProperty("content.errors.genericInputInvalid"));

    ERROR_EMPLOYEE_LOGIN_NOT_UNIQUE = properties.getProperty("content.errors.employeeLoginNotUnique");
    ERROR_INVALID_FIELD_TEMPLATE = properties.getProperty("content.errors.genericInputFieldInvalid");
    ERROR_EMPLOYEE_NOT_FOUND = properties.getProperty("content.errors.employeeNotFound");
    ERROR_EMPLOYEE_ID_ALREADY_EXISTS = properties.getProperty("content.errors.employeeIdAlreadyExists");
    ERROR_INVALID_ID = properties.getProperty("content.errors.invalidId");
    ERROR_INVALID_LOGIN = properties.getProperty("content.errors.invalidLogin");
    ERROR_INVALID_NAME = properties.getProperty("content.errors.invalidName");
    ERROR_INVALID_SALARY = properties.getProperty("content.errors.invalidSalary");
    ERROR_INVALID_DATE = properties.getProperty("content.errors.invalidDate");
  }

}
