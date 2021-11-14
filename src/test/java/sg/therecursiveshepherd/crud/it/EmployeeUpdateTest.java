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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.helpers.TestHelpers;
import sg.therecursiveshepherd.crud.repositories.employees.write.EmployeeWriteRepository;
import sg.therecursiveshepherd.crud.services.EmployeeWriteService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles({"write", "test"})
class EmployeeUpdateTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeWriteRepository employeeWriteRepository;

  @Autowired
  private EmployeeWriteService employeeWriteService;

  @BeforeEach
  void setup() throws Exception {
    employeeWriteRepository.saveAll(TestHelpers.getBaselineData());
  }

  @AfterEach
  void teardown() {
    employeeWriteRepository.deleteAll();
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 200 given valid request body")
  void updateUserReturnsStatus200OnSuccess() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employee = employeeWriteRepository.findById("e0001").orElseThrow(NullPointerException::new);
    assertEquals("e0001", employee.getId());
    assertEquals("testuser", employee.getLogin());
    assertEquals("testname", employee.getName());
    assertEquals(0, BigDecimal.valueOf(100.00).compareTo(employee.getSalary()));
    assertEquals(LocalDate.parse("11-Nov-11", DateTimeFormatter.ofPattern("dd-MMM-yy")), employee.getStartDate());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid id message given blank id")
  void updateUserReturnsStatus400ForBlankId() throws Exception {
    var json = "{\n" +
      "  \"id\": \"\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid id", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid id message given blank id")
  void updateUserReturnsStatus400ForMismatchedId() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e9999\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Employee ID does not match request", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid login message given blank login")
  void updateUserReturnsStatus400ForBlankLogin() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid login", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid login message given numeric login")
  void updateUserReturnsStatus400ForNumericLogin() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": 123,\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid login", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid login message given boolean login")
  void updateUserReturnsStatus400ForBooleanLogin() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": true,\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid login", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid login message given array login")
  void updateUserReturnsStatus400ForArrayLogin() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": [],\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid login", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid name message given blank name")
  void updateUserReturnsStatus400ForBlankName() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid name", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid salary message given non-numeric salary")
  void updateUserReturnsStatus400ForNonNumericSalary() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": \"abc\",\n" +
      "  \"startDate\": \"11-Nov-11\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid salary", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid date message given unsupported date format")
  void updateUserReturnsStatus400ForUnsupportedDateFormat() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-2011\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid date", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid date message given no date")
  void updateUserReturnsStatus400GivenNoDate() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid date", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid date message given empty date")
  void updateUserReturnsStatus400GivenEmptyDate() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"\"\n" +
      "}";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid date", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PUT requests with status 400 and invalid input message given invalid json")
  void updateUserReturnsStatus400GivenInvalidJson() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"\"\n";
    var response = mockMvc.perform(
        put("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid input", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PATCH requests with status 200 and updates login given valid partial DTO")
  void updateUserReturnsStatus200AndUpdatesLogin() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\"" +
      "}";
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employee = employeeWriteRepository.findById("e0001").get();
    assertEquals("testuser", employee.getLogin());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PATCH requests with status 200 and updates salary given valid partial DTO")
  void updateUserReturnsStatus200AndUpdatesSalary() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"salary\": 100.00" +
      "}";
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employee = employeeWriteRepository.findById("e0001").get();
    assertEquals(0, employee.getSalary().compareTo(BigDecimal.valueOf(100.00)));
  }

  @Test
  @DisplayName("/users/{id}: Responds to PATCH requests with status 200 given empty DTO")
  void updateUserReturnsStatus200GivenEmptyDTO() throws Exception {
    var json = "{}";
    var employeeBefore = employeeWriteRepository.findById("e0001").get();
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employeeAfter = employeeWriteRepository.findById("e0001").get();
    assertEquals(employeeBefore, employeeAfter);
  }

  @ParameterizedTest(name = "{index}: {1}")
  @DisplayName(("/users/{id}: Responds to PATCH requests with status 400 given data validation errors"))
  @CsvSource({
    "'{\"id\":\"e0001\",\"login\":123}',Invalid login",
    "'{\"id\": \"e9999\",\"login\": \"testlogin\"}',Employee ID does not match request",
    "'{\"id\": \"e0001\",\"login\": \"rweasley\"}',Employee login not unique"
  })
  void updateUserReturnsStatus400GivenDataValidationErrors(String json, String responseMessage) throws Exception {
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals(responseMessage, responseDto.getMessage());
    var employee = employeeWriteRepository.findById("e0001").get();
    assertEquals("hpotter", employee.getLogin());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PATCH requests with status 200 given existing login matching id")
  void updateUserReturnsStatus200GivenExistingLoginMatchingId() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"hpotter\"" +
      "}";
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employee = employeeWriteRepository.findById("e0001").get();
    assertEquals("hpotter", employee.getLogin());
  }
}
