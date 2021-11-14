package sg.therecursiveshepherd.crud.repositories.employees.write;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sg.therecursiveshepherd.crud.entities.Employee;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("write")
public interface EmployeeWriteRepository extends JpaRepository<Employee, String> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "15000")})
  @Transactional
  List<Employee> findByIdIn(Iterable<String> ids);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "15000")})
  @Transactional
  List<Employee> findByIdOrLogin(String id, String login);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "15000")})
  @Transactional
  Optional<Employee> findByIdNotAndLogin(String id, String login);

}
