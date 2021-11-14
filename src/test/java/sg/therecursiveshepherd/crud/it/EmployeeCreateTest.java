package sg.therecursiveshepherd.crud.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles({"write", "test"})
class EmployeeCreateTest {

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
  @DisplayName("/users: Responds to POST requests with status 201 given valid request body")
  void createUserReturnsStatus201ForValidRequestBody() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e9000\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 0,\n" +
      "  \"startDate\": \"12-Nov-21\"\n" +
      "}";
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isCreated())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully created", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users: Responds to POST requests with status 400 given duplicate id")
  void createUserReturnsStatus400GivenExistingId() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 0,\n" +
      "  \"startDate\": \"12-Nov-21\"\n" +
      "}";
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Employee ID already exists", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users: Responds to POST requests with status 400 given duplicate login")
  void createUserReturnsStatus400GivenExistingLogin() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0099\",\n" +
      "  \"login\": \"hpotter\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 0,\n" +
      "  \"startDate\": \"12-Nov-21\"\n" +
      "}";
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Employee login not unique", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users: Responds to POST requests with status 400 given invalid json")
  void createUserReturnsStatus400GivenInvalidJson() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\",\n" +
      "  \"name\": \"testname\",\n" +
      "  \"salary\": 100.00,\n" +
      "  \"startDate\": \"11-Nov-11\"\n";
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid input", responseDto.getMessage());
  }
}
