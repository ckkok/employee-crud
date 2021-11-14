package sg.therecursiveshepherd.crud.services.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

  private EmployeeMapper employeeMapper;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    employeeMapper = new EmployeeMapper();
  }

  @Test
  @DisplayName("Given null dto, toEntity returns null entity")
  void givenNullDtoReturnsNullEntity() {
    var entity = employeeMapper.toEntity(null);
    assertNull(entity);
  }

  @Test
  @DisplayName("Given a dto, toEntity builds an entity with all the dto's fields")
  void givenDtoReturnsEntityWithAllOfDtoFieldValues() {
    var dto = getTestDto();
    var entity = employeeMapper.toEntity(dto);
    verifyAllEntityFieldsMatchDtoFields(entity, dto);
  }

  @Test
  @DisplayName("Given null dto and non-null entity, toEntity returns entity")
  void givenNullDtoAndNonNullEntityReturnsEntity() {
    var entity = new Employee();
    var returnedEntity = employeeMapper.toEntity(null, entity);
    assertEquals(entity, returnedEntity);
  }

  @Test
  @DisplayName("Given a dto and null entity, toEntity returns a new entity with all of the dto's fields")
  void givenDtoAndNullEntityReturnsNewEntity() {
    var dto = getTestDto();
    var returnedEntity = employeeMapper.toEntity(dto, null);
    verifyAllEntityFieldsMatchDtoFields(returnedEntity, dto);
  }

  @Test
  @DisplayName("Given a dto and entity, toEntity copies dto's fields to entity")
  void givenDtoAndEntityReturnsEntityWithDtoFieldsCopiedOver() {
    var dto = getTestDto();
    var entity = Employee.builder()
      .id("entityId")
      .login("entityLogin")
      .name("entityName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2021, 11, 16))
      .build();
    var returnedEntity = employeeMapper.toEntity(dto, entity);
    assertSame(entity, returnedEntity);
    verifyAllEntityFieldsMatchDtoFields(returnedEntity, dto);
  }

  @Test
  @DisplayName("Given a null dto and non-null entity, patchEntity returns entity")
  void givenNullDtoPatchEntityReturnsEntity() {
    var entity = new Employee();
    var returnedEntity = employeeMapper.patchEntity(null, entity);
    assertSame(entity, returnedEntity);
  }

  @Test
  @DisplayName("Given a null entity, patchEntity throws EmployeeNotFoundException")
  void givenNullEntityPatchEntityThrowsNullPointerException() {
    var dto = new EmployeeDto();
    assertThrows(EmployeeNotFoundException.class, () -> employeeMapper.patchEntity(dto, null));
  }

  @Test
  @DisplayName("Given a dto and entity, patchEntity copies over only non-null dto fields (id) to entity")
  void givenDtoAndEntityPatchEntityCopiesOverDtoIdFieldToEntity() {
    var entity = getTestEntity();
    var dto = EmployeeDto.builder().id("testDtoId").build();
    var patchedEntity = employeeMapper.patchEntity(dto, entity);
    assertSame(entity, patchedEntity);
    assertEquals("testDtoId", patchedEntity.getId());
    assertEquals("testEntityLogin", patchedEntity.getLogin());
    assertEquals("testEntityName", patchedEntity.getName());
    assertEquals(0, patchedEntity.getSalary().compareTo(BigDecimal.ZERO));
    assertEquals(LocalDate.of(2021, 11, 16), patchedEntity.getStartDate());
  }

  @Test
  @DisplayName("Given a dto and entity, patchEntity copies over only non-null dto fields (login) to entity")
  void givenDtoAndEntityPatchEntityCopiesOverDtoLoginFieldToEntity() {
    var entity = getTestEntity();
    var dto = EmployeeDto.builder().login("testDtoLogin").build();
    var patchedEntity = employeeMapper.patchEntity(dto, entity);
    assertSame(entity, patchedEntity);
    assertEquals("testEntityId", patchedEntity.getId());
    assertEquals("testDtoLogin", patchedEntity.getLogin());
    assertEquals("testEntityName", patchedEntity.getName());
    assertEquals(0, patchedEntity.getSalary().compareTo(BigDecimal.ZERO));
    assertEquals(LocalDate.of(2021, 11, 16), patchedEntity.getStartDate());
  }

  @Test
  @DisplayName("Given a dto and entity, patchEntity copies over only non-null dto fields (name) to entity")
  void givenDtoAndEntityPatchEntityCopiesOverDtoNameFieldToEntity() {
    var entity = getTestEntity();
    var dto = EmployeeDto.builder().name("testDtoName").build();
    var patchedEntity = employeeMapper.patchEntity(dto, entity);
    assertSame(entity, patchedEntity);
    assertEquals("testEntityId", patchedEntity.getId());
    assertEquals("testEntityLogin", patchedEntity.getLogin());
    assertEquals("testDtoName", patchedEntity.getName());
    assertEquals(0, patchedEntity.getSalary().compareTo(BigDecimal.ZERO));
    assertEquals(LocalDate.of(2021, 11, 16), patchedEntity.getStartDate());
  }

  @Test
  @DisplayName("Given a dto and entity, patchEntity copies over only non-null dto fields (salary) to entity")
  void givenDtoAndEntityPatchEntityCopiesOverDtoSalaryFieldToEntity() {
    var entity = getTestEntity();
    var dto = EmployeeDto.builder().salary(BigDecimal.TEN).build();
    var patchedEntity = employeeMapper.patchEntity(dto, entity);
    assertSame(entity, patchedEntity);
    assertEquals("testEntityId", patchedEntity.getId());
    assertEquals("testEntityLogin", patchedEntity.getLogin());
    assertEquals("testEntityName", patchedEntity.getName());
    assertEquals(0, patchedEntity.getSalary().compareTo(BigDecimal.TEN));
    assertEquals(LocalDate.of(2021, 11, 16), patchedEntity.getStartDate());
  }

  @Test
  @DisplayName("Given a dto and entity, patchEntity copies over only non-null dto fields (startDate) to entity")
  void givenDtoAndEntityPatchEntityCopiesOverDtoStartDateFieldToEntity() {
    var entity = getTestEntity();
    var dto = EmployeeDto.builder().startDate(LocalDate.of(2001, 1, 1)).build();
    var patchedEntity = employeeMapper.patchEntity(dto, entity);
    assertSame(entity, patchedEntity);
    assertEquals("testEntityId", patchedEntity.getId());
    assertEquals("testEntityLogin", patchedEntity.getLogin());
    assertEquals("testEntityName", patchedEntity.getName());
    assertEquals(0, patchedEntity.getSalary().compareTo(BigDecimal.ZERO));
    assertEquals(LocalDate.of(2001, 1, 1), patchedEntity.getStartDate());
  }

  @Test
  @DisplayName("Given null entity, toDto returns null dto")
  void givenNullEntityToDtoReturnsNullDto() {
    var dto = employeeMapper.toDto(null);
    assertNull(dto);
  }

  @Test
  @DisplayName("Given entity, toDto returns dto with all fields from entity")
  void givenEntityToDtoReturnsDtoWithAllFieldsFromEntity() {
    var entity = getTestEntity();
    var dto = employeeMapper.toDto(entity);
    assertEquals(entity.getId(), dto.getId());
    assertEquals(entity.getLogin(), dto.getLogin());
    assertEquals(entity.getName(), dto.getName());
    assertEquals(0, entity.getSalary().compareTo(dto.getSalary()));
    assertEquals(entity.getStartDate(), dto.getStartDate());
  }

  private void verifyAllEntityFieldsMatchDtoFields(Employee entity, EmployeeDto dto) {
    assertEquals(dto.getId(), entity.getId());
    assertEquals(dto.getLogin(), entity.getLogin());
    assertEquals(dto.getName(), entity.getName());
    assertEquals(0, dto.getSalary().compareTo(entity.getSalary()));
    assertEquals(dto.getStartDate(), entity.getStartDate());
  }

  private EmployeeDto getTestDto() {
    return EmployeeDto.builder()
      .id("test id")
      .login("test login")
      .name("test name")
      .salary(BigDecimal.valueOf(1000.00))
      .startDate(LocalDate.of(2001, 11, 16))
      .build();
  }

  private Employee getTestEntity() {
    return Employee.builder()
      .id("testEntityId")
      .login("testEntityLogin")
      .name("testEntityName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2021, 11, 16))
      .build();
  }
}
