package sg.therecursiveshepherd.crud.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import sg.therecursiveshepherd.crud.annotations.NotAllNull;
import sg.therecursiveshepherd.crud.markers.OnCreateRequest;
import sg.therecursiveshepherd.crud.markers.OnPatchRequest;
import sg.therecursiveshepherd.crud.markers.PrePatch;
import sg.therecursiveshepherd.crud.utils.json.BigDecimalTruncatedSerializer;
import sg.therecursiveshepherd.crud.utils.json.MultiDateFormatDeserializer;
import sg.therecursiveshepherd.crud.utils.json.StringOnlyDeserializer;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import static sg.therecursiveshepherd.crud.dtos.EmployeeDto.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
@NotAllNull(fieldNames = {FIELD_ID, FIELD_LOGIN, FIELD_NAME, FIELD_SALARY, FIELD_START_DATE}, groups = PrePatch.class)
public class EmployeeDto implements Serializable {

  private static final long serialVersionUID = 2501140544036862911L;

  public static final transient String FIELD_ID = "id";
  public static final transient String FIELD_LOGIN = "login";
  public static final transient String FIELD_NAME = "name";
  public static final transient String FIELD_SALARY = "salary";
  public static final transient String FIELD_START_DATE = "startDate";

  private static final transient String ERROR_INVALID_ID = "{dto.employee.invalidId}";
  private static final transient String ERROR_INVALID_LOGIN = "{dto.employee.invalidLogin}";
  private static final transient String ERROR_INVALID_NAME = "{dto.employee.invalidName}";
  private static final transient String ERROR_INVALID_SALARY = "{dto.employee.invalidSalary}";
  private static final transient String ERROR_INVALID_DATE = "{dto.employee.invalidStartDate}";

  private static final transient String MINIMUM_SALARY_ALLOWED = "0.0";

  @NotBlank(message = ERROR_INVALID_ID, groups = OnCreateRequest.class)
  @Length(min = 1, groups = OnPatchRequest.class)
  @JsonDeserialize(using = StringOnlyDeserializer.class)
  private String id;

  @NotBlank(message = ERROR_INVALID_LOGIN, groups = OnCreateRequest.class)
  @Length(min = 1, groups = OnPatchRequest.class)
  @JsonDeserialize(using = StringOnlyDeserializer.class)
  private String login;

  @NotBlank(message = ERROR_INVALID_NAME, groups = OnCreateRequest.class)
  @Length(min = 1, groups = OnPatchRequest.class)
  @JsonDeserialize(using = StringOnlyDeserializer.class)
  private String name;

  @NotNull(message = ERROR_INVALID_SALARY, groups = OnCreateRequest.class)
  @DecimalMin(value = MINIMUM_SALARY_ALLOWED, message = ERROR_INVALID_SALARY, groups = {OnCreateRequest.class, OnPatchRequest.class})
  @JsonSerialize(using = BigDecimalTruncatedSerializer.class)
  private BigDecimal salary;

  @NotNull(message = ERROR_INVALID_DATE, groups = OnCreateRequest.class)
  @JsonDeserialize(using = MultiDateFormatDeserializer.class)
  private LocalDate startDate;

}
