package sg.therecursiveshepherd.crud.repositories.employees.read;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sg.therecursiveshepherd.crud.entities.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository.READ_ONLY;
import static sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository.TIME_OUT;
import static sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository.MIN_BACKOFF;
import static sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository.MAX_BACKOFF;
import static sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository.MAX_ATTEMPTS;

@Repository
@Transactional(readOnly = READ_ONLY, timeout = TIME_OUT)
@Retryable(value = {TransientDataAccessException.class}, maxAttempts = MAX_ATTEMPTS, backoff = @Backoff(delay = MIN_BACKOFF, maxDelay = MAX_BACKOFF))
@Profile("read")
public interface EmployeeReadRepository extends JpaRepository<Employee, String> {

  boolean READ_ONLY = true;
  int TIME_OUT = 10;
  long MIN_BACKOFF = 100L;
  long MAX_BACKOFF = 250L;
  int MAX_ATTEMPTS = 2;

  List<Employee> findByStartDateGreaterThanEqual(LocalDate startDate);

  List<Employee> findByStartDateGreaterThanEqualAndStartDateLessThan(LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findBySalaryGreaterThanEqualAndSalaryLessThan(BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);
  List<Employee> findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(BigDecimal minSalary, BigDecimal maxSalary, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findByNameContaining(String name, Pageable pageable);
  List<Employee> findByNameContainingAndStartDateGreaterThanEqualAndStartDateLessThan(String name, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findByNameContainingAndSalaryGreaterThanEqualAndSalaryLessThan(String name, BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);
  List<Employee> findByNameContainingAndSalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(String name, BigDecimal minSalary, BigDecimal maxSalary, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findByLogin(String login, Pageable pageable);
  List<Employee> findByLoginAndStartDateGreaterThanEqualAndStartDateLessThan(String login, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findByLoginAndSalaryGreaterThanEqualAndSalaryLessThan(String login, BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);
  List<Employee> findByLoginAndSalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(String login, BigDecimal minSalary, BigDecimal maxSalary, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findByLoginAndNameContaining(String login, String name, Pageable pageable);
  List<Employee> findByLoginAndNameContainingAndStartDateGreaterThanEqualAndStartDateLessThan(String login, String name, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);
  List<Employee> findByLoginAndNameContainingAndSalaryGreaterThanEqualAndSalaryLessThan(String login, String name, BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);
  List<Employee> findByLoginAndNameContainingAndSalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(String login, String name, BigDecimal minSalary, BigDecimal maxSalary, LocalDate minStartDate, LocalDate maxStartDate, Pageable pageable);

}
