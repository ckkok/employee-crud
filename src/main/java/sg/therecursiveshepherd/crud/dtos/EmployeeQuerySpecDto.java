package sg.therecursiveshepherd.crud.dtos;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class EmployeeQuerySpecDto {

  String login;
  String name;
  DateRangeQuery dateRangeQuery;
  SalaryRangeQuery salaryRangeQuery;

  @Value
  public static class DateRangeQuery {
    LocalDate minStartDate;
    LocalDate maxStartDate;
  }

  @Value
  public static class SalaryRangeQuery {
    BigDecimal minSalary;
    BigDecimal maxSalary;
  }
}
