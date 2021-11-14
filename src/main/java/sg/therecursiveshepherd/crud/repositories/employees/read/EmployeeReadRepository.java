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
import java.util.List;

@Repository
@Profile("read")
public interface EmployeeReadRepository extends JpaRepository<Employee, String> {

  @Transactional(readOnly = true, timeout = 10)
  @Retryable(value = {TransientDataAccessException.class}, maxAttempts = 2, backoff = @Backoff(delay = 100L, maxDelay = 250L))
  List<Employee> findBySalaryGreaterThanEqualAndSalaryLessThan(BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);

}
