package sg.therecursiveshepherd.crud.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "employee", uniqueConstraints = {
  @UniqueConstraint(name = Employee.CONSTRAINT_LOGIN, columnNames = {Employee.COLUMN_LOGIN})
})
public class Employee {

  public static final String FIELD_ID = "id";
  public static final String FIELD_LOGIN = "login";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_SALARY = "salary";
  public static final String FIELD_START_DATE = "startDate";

  public static final String CONSTRAINT_LOGIN = "uniquelogin";

  public static final String COLUMN_ID = "id";
  public static final String COLUMN_LOGIN = "login";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_SALARY = "salary";
  public static final String COLUMN_START_DATE = "start_date";
  public static final String COLUMN_VERSION = "version";
  public static final String COLUMN_CREATED_DATETIME = "created_datetime";
  public static final String COLUMN_LAST_UPDATED_DATETIME = "last_updated_datetime";

  @AllArgsConstructor
  @Getter
  public enum FieldName {
    ID(FIELD_ID, COLUMN_ID),
    LOGIN(FIELD_LOGIN, COLUMN_LOGIN),
    NAME(FIELD_NAME, COLUMN_NAME),
    SALARY(FIELD_SALARY, COLUMN_SALARY),
    STARTDATE(FIELD_START_DATE, COLUMN_START_DATE);

    private final String dtoFieldName;
    private final String dbColumnName;

    private static final String ERROR_TEMPLATE;

    static {
      ERROR_TEMPLATE = "Invalid value '%s' for employee field name given. Has to be one of " +
        Arrays.stream(FieldName.values()).map(f -> f.name().toLowerCase(Locale.US)).collect(Collectors.joining(", "));
    }

    public static FieldName fromString(String value) {
      try {
        return FieldName.valueOf(value.toUpperCase(Locale.US));
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format(ERROR_TEMPLATE, value), e);
      }
    }
  }

  @Version
  @Column(nullable = false, name = COLUMN_VERSION)
  protected Integer version;

  @Id
  @Column(name = COLUMN_ID)
  private String id;

  @Column(nullable = false, name = COLUMN_LOGIN)
  private String login;

  @Column(nullable = false, name = COLUMN_NAME)
  private String name;

  @Column(nullable = false, name = COLUMN_SALARY, precision = 20, scale = 4)
  private BigDecimal salary;

  @Column(nullable = false, name = COLUMN_START_DATE)
  private LocalDate startDate;

  @CreatedDate
  @Column(name = COLUMN_CREATED_DATETIME, nullable = false, updatable = false)
  private Instant createdDate;

  @LastModifiedDate
  @Column(name = COLUMN_LAST_UPDATED_DATETIME, nullable = false)
  private Instant lastUpdatedDate;

  @PrePersist
  public void prePersist() {
    var instantNow = Instant.now();
    setCreatedDate(instantNow);
    setLastUpdatedDate(instantNow);
  }

  @PreUpdate
  public void preUpdate() {
    setLastUpdatedDate(Instant.now());
  }

}
