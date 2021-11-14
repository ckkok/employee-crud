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
  public static final String ERROR_EMPLOYEE_ID_MISMATCH;
  public static final String ERROR_EMPLOYEE_LOGIN_NOT_UNIQUE;
  public static final String ERROR_EMPLOYEE_NOT_FOUND;
  public static final String ERROR_INVALID_FIELD_TEMPLATE;
  public static final int SALARY_SCALE;

  public static final ApiResponseDto<String> RESPONSE_EMPLOYEE_CREATED;
  public static final ApiResponseDto<String> RESPONSE_EMPLOYEE_CSV_FILE_PROCESSED;
  public static final ApiResponseDto<String> RESPONSE_EMPLOYEE_UPDATED;
  public static final ApiResponseDto<String> RESPONSE_EMPLOYEE_DELETED;
  public static final ApiResponseDto<String> RESPONSE_METHOD_NOT_ALLOWED;
  public static final ApiResponseDto<String> RESPONSE_INTERNAL_SERVER_ERROR;
  public static final ApiResponseDto<String> RESPONSE_INVALID_INPUT;

  static {
    YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
    yamlMapFactoryBean.setResources(new ClassPathResource("content.yml"));
    Properties properties = Objects.requireNonNull(yamlMapFactoryBean.getObject(), "Content file not found");

    EMPLOYEE_START_DATE_FORMATTERS = List.of(
      DateTimeFormatter.ofPattern(properties.getProperty("content.employeeStartDateFormat[0]")),
      DateTimeFormatter.ofPattern(properties.getProperty("content.employeeStartDateFormat[1]"))
    );

    SALARY_SCALE = Integer.parseInt(properties.getProperty("content.salaryScale"), 10);

    RESPONSE_EMPLOYEE_DELETED = new ApiResponseDto<>(properties.getProperty("content.success.employeeDeleted"));
    RESPONSE_EMPLOYEE_UPDATED = new ApiResponseDto<>(properties.getProperty("content.success.employeeUpdated"));
    RESPONSE_EMPLOYEE_CREATED = new ApiResponseDto<>(properties.getProperty("content.success.employeeCreated"));
    RESPONSE_EMPLOYEE_CSV_FILE_PROCESSED = new ApiResponseDto<>(properties.getProperty("content.success.employeeFileUploaded"));
    RESPONSE_INTERNAL_SERVER_ERROR = new ApiResponseDto<>(properties.getProperty("content.errors.serverError"));
    RESPONSE_METHOD_NOT_ALLOWED = new ApiResponseDto<>(properties.getProperty("content.errors.methodNotAllowed"));
    RESPONSE_INVALID_INPUT = new ApiResponseDto<>(properties.getProperty("content.errors.genericInputInvalid"));

    ERROR_EMPLOYEE_LOGIN_NOT_UNIQUE = properties.getProperty("content.errors.employeeLoginNotUnique");
    ERROR_INVALID_FIELD_TEMPLATE = properties.getProperty("content.errors.genericInputFieldInvalidTemplate");
    ERROR_EMPLOYEE_NOT_FOUND = properties.getProperty("content.errors.employeeNotFound");
    ERROR_EMPLOYEE_ID_ALREADY_EXISTS = properties.getProperty("content.errors.employeeIdAlreadyExists");
    ERROR_EMPLOYEE_ID_MISMATCH = properties.getProperty("content.errors.employeeIdMismatch");
  }

}
