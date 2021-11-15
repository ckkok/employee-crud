package sg.therecursiveshepherd.crud.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import sg.therecursiveshepherd.crud.dtos.EmployeeQuerySpecDto;
import sg.therecursiveshepherd.crud.entities.Employee;
import sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository;
import sg.therecursiveshepherd.crud.services.mappers.EmployeeMapper;
import sg.therecursiveshepherd.crud.utils.RangeQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeReadServiceTest {

  private EmployeeReadService employeeReadService;

  @Mock
  private EmployeeReadRepository employeeReadRepository;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    var employeeMapper = new EmployeeMapper();
    var defaultSortOrderForEmployees = Sort.Order.asc(Employee.FieldName.ID.getDbColumnName());
    employeeReadService = new EmployeeReadService(employeeReadRepository, employeeMapper, defaultSortOrderForEmployees);
  }

  @Test
  @DisplayName("Given offset and limit, findAllEmployees invokes the repository method with the same offset and limit and with page 0")
  void givenOffsetAndLimitFindAllEmployeesCallsRepositoryMethodWithThem() {
    var querySpecDto = EmployeeQuerySpecDto.builder()
      .login(null).name(null)
      .dateRangeQuery(new EmployeeQuerySpecDto.DateRangeQuery(null, null))
      .salaryRangeQuery(new EmployeeQuerySpecDto.SalaryRangeQuery(null, null))
      .build();
    var argCaptor = ArgumentCaptor.forClass(RangeQuery.class);
    when(employeeReadRepository.findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(any(BigDecimal.class), any(BigDecimal.class), any(LocalDate.class), any(LocalDate.class), any()))
      .thenReturn(Collections.emptyList());
    employeeReadService.findAllEmployees(querySpecDto, 1, 2, null, null);
    verify(employeeReadRepository).findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(nullable(BigDecimal.class), nullable(BigDecimal.class), nullable(LocalDate.class), nullable(LocalDate.class), argCaptor.capture());
    var rangeQuery = argCaptor.getValue();
    assertEquals(1, rangeQuery.getOffset());
    assertEquals(0, rangeQuery.getPageNumber());
    assertEquals(2, rangeQuery.getPageSize());
  }

  @Test
  @DisplayName("Given no sort parameter, findAllEmployeesBySalary sorts by id ascending")
  void givenNoSortParameterFindAllEmployeesSortsByIdAscending() {
    var querySpecDto = EmployeeQuerySpecDto.builder()
      .login(null).name(null)
      .dateRangeQuery(new EmployeeQuerySpecDto.DateRangeQuery(null, null))
      .salaryRangeQuery(new EmployeeQuerySpecDto.SalaryRangeQuery(null, null))
      .build();
    var argCaptor = ArgumentCaptor.forClass(RangeQuery.class);
    when(employeeReadRepository.findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(any(BigDecimal.class), any(BigDecimal.class), any(LocalDate.class), any(LocalDate.class), any()))
      .thenReturn(Collections.emptyList());
    employeeReadService.findAllEmployees(querySpecDto, 0, 0, null, null);
    verify(employeeReadRepository).findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(nullable(BigDecimal.class), nullable(BigDecimal.class), nullable(LocalDate.class), nullable(LocalDate.class), argCaptor.capture());
    var sortOrders = argCaptor.getValue().getSort().toList();
    assertEquals(1, sortOrders.size());
    assertEquals(Sort.Direction.ASC, sortOrders.get(0).getDirection());
    assertEquals(Employee.FIELD_ID, sortOrders.get(0).getProperty());
  }

  @Test
  @DisplayName("Given an id, findEmployeeById returns an optional DTO if the employee record exists")
  void givenValidIdFindEmployeeByIdReturnsOptionalDto() {
    var entity = Employee.builder()
      .id("testId")
      .login("testLogin")
      .name("testName")
      .salary(BigDecimal.ZERO)
      .startDate(LocalDate.of(2001, 11, 16))
      .build();
    when(employeeReadRepository.findById(any(String.class))).thenReturn(Optional.of(entity));
    var dtoOptional = employeeReadService.findEmployeeById("testId");
    assertTrue(dtoOptional.isPresent());
  }

  @Test
  @DisplayName("Given a non-existent id, findEmployeeById returns an empty optional")
  void givenNonExistentIdFindEmployeeByIdReturnsOptionalDto() {
    when(employeeReadRepository.findById(any(String.class))).thenReturn(Optional.empty());
    var dtoOptional = employeeReadService.findEmployeeById("testId");
    assertFalse(dtoOptional.isPresent());
  }

}
