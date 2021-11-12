package sg.therecursiveshepherd.crud.services.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sg.therecursiveshepherd.crud.entities.Employee;

import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployeeMapperTest {

  private EmployeeMapper employeeMapper;

  @Mock
  private Validator validator;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    employeeMapper = new EmployeeMapper(validator);
  }

  @Test
  @DisplayName("Given null dto, returns null entity")
  void givenNullDtoReturnsNullEntity() {
    var entity = employeeMapper.toEntity(null);
    assertNull(entity);
  }

  @Test
  @DisplayName("Given null dto and non-null entity, returns entity")
  void givenNullDtoAndNonNullEntityReturnsEntity() {
    var entity = new Employee();
    var returnedEntity = employeeMapper.toEntity(null, entity);
    assertEquals(entity, returnedEntity);
  }

  @Test
  @DisplayName("Given null entity, returns null dto")
  void givenNullEntityReturnsNullDto() {
    var dto = employeeMapper.toDto(null);
    assertNull(dto);
  }
}
