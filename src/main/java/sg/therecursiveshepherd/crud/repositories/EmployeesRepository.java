package sg.therecursiveshepherd.crud.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sg.therecursiveshepherd.crud.entities.Employee;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, String> {

  @Transactional(readOnly = true, timeout = 10)
  List<Employee> findBySalaryGreaterThanEqualAndSalaryLessThan(BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "15000")})
  @Transactional
  List<Employee> findByIdIn(Iterable<String> ids);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "15000")})
  @Transactional
  List<Employee> findByIdOrLogin(String id, String login);

}
