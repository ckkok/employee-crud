package sg.therecursiveshepherd.crud.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sg.therecursiveshepherd.crud.dtos.ApiResponseAllEmployeesDto;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.helpers.TestHelpers;
import sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository;
import sg.therecursiveshepherd.crud.services.EmployeeReadService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles({"read", "test"})
class EmployeeFindAndFilterTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeReadRepository employeeReadRepository;

  @Autowired
  private EmployeeReadService employeeReadService;

  @BeforeEach
  void setup() throws Exception {
    employeeReadRepository.saveAll(TestHelpers.getBaselineData());
  }

  @AfterEach
  void teardown() {
    employeeReadRepository.deleteAll();
  }

  @ParameterizedTest(name = "{index}: {0}")
  @DisplayName("/users: Fetches all users with given filters")
  @CsvSource({
    "/users,6",
    "/users?minSalary=3999.999,1",
    "/users?minSalary=3999.999&maxSalary=4000.004,2"
  })
  void fetchAllUsersWithGivenFilters(String url, int numResults) throws Exception {
    var response = mockMvc.perform(get(url))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals(numResults, dto.getResults().size());
  }

  @Test
  @DisplayName("/users?login=adumbledore: Fetches only employee with login as adumbledore")
  void fetchAllUsersGivenValidLoginFilterReturnsEmployeesWithMatchingLogin() throws Exception {
    var response = mockMvc.perform(get("/users?login=adumbledore"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals(1, dto.getResults().size());
    var employee = dto.getResults().get(0);
    assertEquals("adumbledore", employee.getLogin());
  }

  @Test
  @DisplayName("/users?name=Rubeus: Fetches employees with name containing Rubeus")
  void fetchAllUsersGivenValidNameFilterReturnsEmployeesWithNameLikeGivenValue() throws Exception {
    var response = mockMvc.perform(get("/users?name=Rubeus"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals(1, dto.getResults().size());
    var employee = dto.getResults().get(0);
    assertEquals("Rubeus Hagrid", employee.getName());
  }

  @Test
  @DisplayName("/users?offset=1&limit=1: Fetches only second user with salary between 0 and 4000 (exclusive)")
  void fetchAllUsersWithGivenOffsetAndLimit() throws Exception {
    var response = mockMvc.perform(get("/users?offset=1&limit=1"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals(1, dto.getResults().size());
    assertEquals("e0004", dto.getResults().get(0).getId());
  }

  @Test
  @DisplayName("/users?sortDir=desc: Fetches users sorted by id (default) descending when sortDir is specified as desc")
  void fetchAllUsersSortedByIdDesc() throws Exception {
    var response = mockMvc.perform(get("/users?sortDir=desc"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals("e0010", dto.getResults().get(0).getId());
  }

  @Test
  @DisplayName("/users?sortBy=name&sortDir=desc: Fetches users sorted by name descending when specified")
  void fetchAllUsersSortedByNameDesc() throws Exception {
    var response = mockMvc.perform(get("/users?sortBy=name&sortDir=desc"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals("Rubeus Hagrid", dto.getResults().get(0).getName());
  }

  @Test
  @DisplayName("/users?login=hpotter&name=Potter: Fetches users by login and name when both are specified")
  void fetchAllUsersByLoginAndName() throws Exception {
    var response = mockMvc.perform(get("/users?login=hpotter&name=Potter"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals(1, dto.getResults().size());
    assertEquals("Harry Potter", dto.getResults().get(0).getName());
  }

  @Test
  @DisplayName("/users?minStartDate=2001-11-17: Fetches users by login and name when both are specified")
  void fetchAllUsersWithStartDateAfter() throws Exception {
    var testDate = LocalDate.of(2001, 11, 17);
    var response = mockMvc.perform(get("/users?minStartDate=2001-11-17"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    for (var employee : dto.getResults()) {
      assertTrue(employee.getStartDate().isAfter(testDate) || employee.getStartDate().isEqual(testDate));
    }
  }

  @Test
  @DisplayName("/users?maxStartDate=2001-11-18: Fetches users by login and name when both are specified")
  void fetchAllUsersWithStartDateBefore() throws Exception {
    var testDate = LocalDate.of(2001, 11, 18);
    var response = mockMvc.perform(get("/users?maxStartDate=2001-11-18"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    for (var employee : dto.getResults()) {
      assertTrue(employee.getStartDate().isBefore(testDate));
    }
  }

  @Test
  @DisplayName("/users?sortDir=asdf: Responds to GET request with status 400 given invalid parameter value")
  void fetchAllUsersReturnsStatus400GivenInvalidSortDirValue() throws Exception {
    var response = mockMvc.perform(get("/users?sortDir=asdf"))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid sortDir", dto.getMessage());
  }

  @Test
  @DisplayName("/users/e0001: Fetches user with id e0001")
  void fetchUserById() throws Exception {
    var response = mockMvc.perform(get("/users/e0001"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), EmployeeDto.class);
    assertEquals("e0001", dto.getId());
  }

  @Test
  @DisplayName("/users/e9999: Responds to GET request with status 400 given non-existent id")
  void fetchUserByIdReturnsStatus400ForNonExistentId() throws Exception {
    var response = mockMvc.perform(get("/users/e9999"))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("No such employee", dto.getMessage());
  }
}
