package sg.therecursiveshepherd.crud.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sg.therecursiveshepherd.crud.utils.json.BigDecimalTruncatedSerializer;
import sg.therecursiveshepherd.crud.utils.json.MultiDateFormatDeserializer;
import sg.therecursiveshepherd.crud.utils.json.StringOnlyDeserializer;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {

  public static final String FIELD_ID = "id";
  public static final String FIELD_LOGIN = "login";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_SALARY = "salary";
  public static final String FIELD_START_DATE = "startDate";

  private static final String ERROR_INVALID_ID = "Invalid id";
  private static final String ERROR_INVALID_LOGIN = "Invalid login";
  private static final String ERROR_INVALID_NAME = "Invalid name";
  private static final String ERROR_INVALID_SALARY = "Invalid salary";
  private static final String ERROR_INVALID_DATE = "Invalid date";

  private static final String MINIMUM_SALARY_ALLOWED = "0.0";

  @NotNull(message = ERROR_INVALID_ID)
  @JsonDeserialize(using = StringOnlyDeserializer.class)
  private Optional<@NotBlank(message = ERROR_INVALID_ID) String> id;

  @NotNull(message = ERROR_INVALID_LOGIN)
  @JsonDeserialize(using = StringOnlyDeserializer.class)
  private Optional<@NotBlank(message = ERROR_INVALID_LOGIN) String> login;

  @NotNull(message = ERROR_INVALID_NAME)
  @JsonDeserialize(using = StringOnlyDeserializer.class)
  private Optional<@NotBlank(message = ERROR_INVALID_NAME) String> name;

  @NotNull(message = ERROR_INVALID_SALARY)
  @JsonSerialize(using = BigDecimalTruncatedSerializer.class)
  private Optional<@DecimalMin(value = MINIMUM_SALARY_ALLOWED, message = ERROR_INVALID_SALARY) BigDecimal> salary;

  @NotNull(message = ERROR_INVALID_DATE)
  @JsonDeserialize(using = MultiDateFormatDeserializer.class)
  private Optional<LocalDate> startDate;

}
