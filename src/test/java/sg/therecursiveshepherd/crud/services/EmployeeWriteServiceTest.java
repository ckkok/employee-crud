package sg.therecursiveshepherd.crud.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import sg.therecursiveshepherd.crud.configurations.ApplicationConfiguration;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.exceptions.EmployeeIdAlreadyExistsException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeIdMismatchException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeLoginNonUniqueException;
import sg.therecursiveshepherd.crud.exceptions.EmployeeNotFoundException;
import sg.therecursiveshepherd.crud.helpers.TestHelpers;
import sg.therecursiveshepherd.crud.repositories.employees.write.EmployeeWriteRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.services.validators.EmployeeCustomValidator;

import javax.validation.Validation;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeWriteServiceTest {

  private EmployeeWriteService employeeWriteService;

  @Mock
  private EmployeeWriteRepository employeeWriteRepository;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    var employeeMapper = new EmployeeMapper();
    var validator = Validation.buildDefaultValidatorFactory().getValidator();
    var employeeCustomValidator = new EmployeeCustomValidator(validator);
    var employeeCsvMapper = new ApplicationConfiguration().employeeCsvMapper();
    employeeWriteService = new EmployeeWriteService(employeeCsvMapper, employeeCustomValidator, employeeWriteRepository, employeeMapper);
  }

  @Test
  @DisplayName("Given a valid dto, createEmployee maps it to an entity and saves it")
  void givenValidDtoCreateEmployeeMapsItToEntityAndSavesIt() {
    var dto = EmployeeDto.builder()
      .id("testId")
      .login("testLogin")
      .name("testName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2021, 11, 16))
      .build();
    var entityCaptor = ArgumentCaptor.forClass(Employee.class);
    employeeWriteService.createEmployee(dto);
    verify(employeeWriteRepository).save(entityCaptor.capture());
    var entity = entityCaptor.getValue();
    assertEquals(dto.getId(), entity.getId());
    assertEquals(dto.getLogin(), entity.getLogin());
    assertEquals(dto.getName(), entity.getName());
    assertEquals(0, entity.getSalary().compareTo(dto.getSalary()));
    assertEquals(dto.getStartDate(), entity.getStartDate());
  }

  @Test
  @DisplayName("Given a dto with id in database, createEmployee throws EmployeeIdAlreadyExistsException")
  void givenDtoWithIdInDatabaseCreateEmployeeThrowsEmployeeIdAlreadyExistsException() {
    var dto = EmployeeDto.builder()
      .id("testId")
      .login("testLogin")
      .name("testName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2021, 11, 16))
      .build();
    var existingEntity = Employee.builder()
      .id("testId")
      .login("differentLogin")
      .name("differentName")
      .salary(BigDecimal.ONE)
      .startDate(LocalDate.of(2021, 1, 1))
      .build();
    when(employeeWriteRepository.findByIdOrLogin("testId", "testLogin")).thenReturn(List.of(existingEntity));
    assertThrows(EmployeeIdAlreadyExistsException.class, () -> employeeWriteService.createEmployee(dto));
  }

  @Test
  @DisplayName("Given a dto with login in database, createEmployee throws EmployeeLoginNonUniqueException")
  void givenDtoWithLoginInDatabaseCreateEmployeeThrowsEmployeeLoginNonUniqueException() {
    var dto = EmployeeDto.builder()
      .id("testId")
      .login("testLogin")
      .name("testName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2021, 11, 16))
      .build();
    var existingEntity = Employee.builder()
      .id("differentId")
      .login("testLogin")
      .name("differentName")
      .salary(BigDecimal.ONE)
      .startDate(LocalDate.of(2021, 1, 1))
      .build();
    when(employeeWriteRepository.findByIdOrLogin("testId", "testLogin")).thenReturn(List.of(existingEntity));
    assertThrows(EmployeeLoginNonUniqueException.class, () -> employeeWriteService.createEmployee(dto));
  }

  @Test
  @DisplayName("Given a valid dto and valid id, replaceEmployeeById replaces entity with data from dto")
  void givenValidDtoAndIdReplaceEmployeeByIdReplacesEntityFieldsWithDtoData() {
    var testId = "testId";
    var employeeCaptor = ArgumentCaptor.forClass(Employee.class);
    var dto = getTestDto();
    var originalEmployee = Employee.builder()
      .id(testId)
      .login("testEntityLogin")
      .name("testEntityName")
      .salary(BigDecimal.ONE)
      .startDate(LocalDate.of(2001, 1, 1))
      .build();
    when(employeeWriteRepository.findById(testId)).thenReturn(Optional.of(originalEmployee));
    employeeWriteService.replaceEmployeeById(testId, dto);
    verify(employeeWriteRepository).save(employeeCaptor.capture());
    var savedEmployee = employeeCaptor.getValue();
    assertEquals("testLogin", savedEmployee.getLogin());
    assertEquals("testName", savedEmployee.getName());
    assertEquals(0, BigDecimal.ZERO.compareTo(savedEmployee.getSalary()));
    assertEquals(LocalDate.of(2021, 11, 16), savedEmployee.getStartDate());
  }

  @Test
  @DisplayName("Given dto with id not matching path id, replaceEmployeeById throws EmployeeIdMismatchException")
  void givenDtoWithIdNotMatchingPathIdReplaceEmployeeByIdThrowsEmployeeIdMismatchException() {
    var dto = getTestDto();
    assertThrows(EmployeeIdMismatchException.class, () -> employeeWriteService.replaceEmployeeById("newId", dto));
  }

  @Test
  @DisplayName("Given path id that does not exist in database, replaceEmployeeById returns true")
  void givenPathIdNotExistingInDatabaseReplaceEmployeeByIdReturnsTrue() {
    var testId = "testId";
    var dto = getTestDto();
    when(employeeWriteRepository.findById(testId)).thenReturn(Optional.empty());
    assertTrue(employeeWriteService.replaceEmployeeById(testId, dto));
  }

  @Test
  @DisplayName("Given path id not matching patch dto id, updateEmployeeById throws EmployeeIdMismatchException")
  void givenPathIdNotMatchingDtoIdUpdateEmployeeByIdThrowsEmployeeIdMismatchException() {
    var dto = getTestDto();
    assertThrows(EmployeeIdMismatchException.class, () -> employeeWriteService.updateEmployeeById("newId", dto));
  }

  @Test
  @DisplayName("Given dto with all null fields, updateEmployeeById does not call the database")
  void givenDtoWithNullFieldsUpdateEmployeeByIdDoesNotCallTheDatabase() {
    var dto = new EmployeeDto();
    employeeWriteService.updateEmployeeById("testId", dto);
    verifyNoInteractions(employeeWriteRepository);
  }

  @Test
  @DisplayName("Given id not in database, updateEmployeeById throws EmployeeNotFoundException")
  void givenIdNotInDatabaseUpdateEmployeeByIdThrowsEmployeeNotFoundException() {
    var testId = "testId";
    var dto = EmployeeDto.builder().name("newName").build();
    when(employeeWriteRepository.findById(testId)).thenReturn(Optional.empty());
    assertThrows(EmployeeNotFoundException.class, () -> employeeWriteService.updateEmployeeById(testId, dto));
  }

  @Test
  @DisplayName("Given login belonging to another id in database, updateEmployeeById throws EmployeeLoginNonUniqueException")
  void givenLoginBelongingToAnotherIdUpdateEmployeeByIdThrowsEmployeeLoginNonUniqueException() {
    var testId = "testId";
    var testLogin = "testLogin";
    var dto = EmployeeDto.builder().login(testLogin).build();
    var existingEmployee = getTestEntity();
    var otherEmployee = Employee.builder()
      .id("otherId")
      .login(testLogin)
      .name("testName")
      .salary(BigDecimal.TEN)
      .startDate(LocalDate.of(2020, 1, 1))
      .build();
    when(employeeWriteRepository.findById(testId)).thenReturn(Optional.of(existingEmployee));
    when(employeeWriteRepository.findByIdNotAndLogin(testId, testLogin)).thenReturn(Optional.of(otherEmployee));
    assertThrows(EmployeeLoginNonUniqueException.class, () -> employeeWriteService.updateEmployeeById(testId, dto));
  }

  @Test
  @DisplayName("Given valid dto and id, updateEmployeeId patches only non-null employee field from dto to entity with given id")
  void givenValidDtoAndIdUpdateEmployeeIdPatchesOnlyNonNullEmployeeFieldsFromDtoToEntityWithGivenId() {
    var testId = "testId";
    var dto = EmployeeDto.builder().name("newName").build();
    var testEntity = getTestEntity();
    assertNotEquals("newName", testEntity.getName());
    var employeeCaptor = ArgumentCaptor.forClass(Employee.class);
    when(employeeWriteRepository.findById(testId)).thenReturn(Optional.of(testEntity));
    employeeWriteService.updateEmployeeById(testId, dto);
    verify(employeeWriteRepository).save(employeeCaptor.capture());
    var savedEmployee = employeeCaptor.getValue();
    assertEquals("newName", savedEmployee.getName());
  }

  @Test
  @DisplayName("Given id, deleteEmployeeById attempts to delete record in database with id")
  void givenIdDeleteEmployeeByIdAttemptsToDeleteRecordInDatabaseWithId() {
    var testId = "testId";
    var idCaptor = ArgumentCaptor.forClass(String.class);
    employeeWriteService.deleteEmployeeById(testId);
    verify(employeeWriteRepository).deleteById(idCaptor.capture());
    assertEquals(testId, idCaptor.getValue());
  }

  @Test
  @DisplayName("Given id not in database, deleteEmployeeById throws EmployeeNotFoundException")
  void givenIdNotInDatabaseDeleteEmployeeByIdThrowsEmployeeNotFoundException() {
    var testId = "testId";
    doThrow(EmptyResultDataAccessException.class).when(employeeWriteRepository).deleteById(testId);
    assertThrows(EmployeeNotFoundException.class, () -> employeeWriteService.deleteEmployeeById(testId));
  }


  @Test
  @DisplayName("Given the employee CSV sample file, 8 records are processed")
  void handleFileUpload() throws IOException {
    var mockUploadedFile = TestHelpers.getFileForUpload("file", "sample_data.csv");
    when(employeeWriteRepository.findByIdIn(anyIterable())).thenReturn(Collections.emptyList());
    long numRecordsUpdated = employeeWriteService.handleFileUpload(mockUploadedFile);
    verify(employeeWriteRepository).findByIdIn(anyIterable());
    verify(employeeWriteRepository).saveAllAndFlush(anyIterable());
    assertEquals(8, numRecordsUpdated);
  }

  private EmployeeDto getTestDto() {
    return EmployeeDto.builder()
      .id("testId")
      .login("testLogin")
      .name("testName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2021, 11, 16))
      .build();
  }

  private Employee getTestEntity() {
    return Employee.builder()
      .id("testId")
      .login("testEntityLogin")
      .name("testEntityName")
      .salary(BigDecimal.ONE)
      .startDate(LocalDate.of(2001, 1, 1))
      .build();
  }

}
