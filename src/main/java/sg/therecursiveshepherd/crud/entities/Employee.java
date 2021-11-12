package sg.therecursiveshepherd.crud.entities;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Employee {

  public static final String FIELD_ID = "id";
  public static final String FIELD_LOGIN = "login";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_SALARY = "salary";
  public static final String FIELD_START_DATE = "startDate";

  public static final String COLUMN_ID = "id";
  public static final String COLUMN_LOGIN = "login";
  public static final String COLUMN_NAME = "name";
  public static final String COLUMN_SALARY = "salary";
  public static final String COLUMN_START_DATE = "start_date";
  public static final String COLUMN_VERSION = "version";

  @Id
  @Column(name = COLUMN_ID)
  private String id;

  @Column(nullable = false, unique = true, name = COLUMN_LOGIN)
  private String login;

  @Column(nullable = false, name = COLUMN_NAME)
  private String name;

  @Column(nullable = false, name = COLUMN_SALARY, precision = 20, scale = 3)
  private BigDecimal salary;

  @Column(nullable = false, name = COLUMN_START_DATE)
  private LocalDate startDate;

  @Version
  @Column(nullable = false, name = COLUMN_VERSION)
  protected Integer version;

}
