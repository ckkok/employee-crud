package sg.therecursiveshepherd.crud.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import sg.therecursiveshepherd.crud.configurations.BeanConfig;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.repositories.EmployeesRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.services.validators.EmployeeCustomValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmployeesServiceTest {

  private EmployeesService employeesService;

  @Mock
  private EmployeesRepository employeesRepository;

  @Mock
  private Validator validator;

  @BeforeEach
  void setup() throws IOException {
    MockitoAnnotations.openMocks(this);
    var beanConfig = new BeanConfig();
    var employeeCsvMapper = beanConfig.employeeCsvMapper();
    var csvFileValidator = new EmployeeCustomValidator(validator);
    var employeeMapper = new EmployeeMapper(validator);
    employeesService = new EmployeesService(employeeCsvMapper, csvFileValidator, employeesRepository, employeeMapper);
  }

  @Test
  void handleFileUpload() {
    var factory = Validation.buildDefaultValidatorFactory();
    var validator = factory.getValidator();
    var dto = new EmployeeDto();
    dto.setId(Optional.of("abc"));
    Set<ConstraintViolation<EmployeeDto>> violations = validator.validateProperty(dto, "id");
    System.out.println(violations);
  }

  @Test
  @DisplayName("Given null minSalary, findAllEmployees defaults to 0.0 minSalary")
  void givenNullMinSalaryUsesDefaultOfZero() {
    var captor = ArgumentCaptor.forClass(BigDecimal.class);
    when(employeesRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(any(BigDecimal.class), any(BigDecimal.class), any()))
      .thenReturn(Collections.emptyList());
    employeesService.findAllEmployees(null, null, 0, 0);
    verify(employeesRepository).findBySalaryGreaterThanEqualAndSalaryLessThan(captor.capture(), any(BigDecimal.class), any());
    assertEquals(BigDecimal.ZERO, captor.getValue());
  }

  @Test
  @DisplayName("Given null maxSalary, findAllEmployees defaults to Double.MAX_VALUE maxSalary")
  void givenNullMaxSalaryUsesDefaultOfMaxDouble() {
    var captor = ArgumentCaptor.forClass(BigDecimal.class);
    when(employeesRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(any(BigDecimal.class), any(BigDecimal.class), any()))
      .thenReturn(Collections.emptyList());
    employeesService.findAllEmployees(null, null, 0, 0);
    verify(employeesRepository).findBySalaryGreaterThanEqualAndSalaryLessThan(any(BigDecimal.class), captor.capture(), any());
    assertEquals(BigDecimal.valueOf(Double.MAX_VALUE), captor.getValue());
  }

//    @Test
//    @DisplayName("Given the employee CSV sample file, 8 records are processed")
//    void handleFileUpload() throws IOException {
//        var mockUploadedFile = TestHelpers.getFileForUpload("file", "sample_data.csv");
//        ArgumentCaptor<List<Employee>> captor = ArgumentCaptor.forClass(List.class);
//        Mockito.when(employeesRepository.saveAll(Mockito.anyList())).thenReturn(Collections.emptyList());
//        employeesService.handleFileUpload(mockUploadedFile);
//        Mockito.verify(employeesRepository).saveAll(captor.capture());
//        var entityList = captor.getValue();
//        assertEquals(8, entityList.size());
//    }


}
