package sg.therecursiveshepherd.crud.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sg.therecursiveshepherd.crud.dtos.ApiResponseAllEmployeesDto;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;
import sg.therecursiveshepherd.crud.helpers.TestHelpers;
import sg.therecursiveshepherd.crud.repositories.EmployeesRepository;
import sg.therecursiveshepherd.crud.services.EmployeesService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CrudApplicationTest {

  private static final String UPLOAD_FILE_FIELD_NAME = "file";
  private static final String UPLOAD_FILE_ENDPOINT = "/users/upload";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeesRepository employeesRepository;

  @Autowired
  private EmployeesService employeesService;

  @BeforeEach
  void setup() throws Exception {
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, "sample_data.csv");
    mockMvc.perform(
      MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
        .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
        .characterEncoding(StandardCharsets.UTF_8)
    ).andExpect(status().isCreated());
  }

  @AfterEach
  void teardown() {
    employeesRepository.deleteAll();
  }

  @ParameterizedTest(name = "{index}: {0}")
  @DisplayName("/users: Fetches all users with given filters")
  @CsvSource({
    "/users,5",
    "/users?minSalary=3999.999,1",
    "/users?minSalary=3999.999&maxSalary=4000.004,2"
  })
  void fetchAllUsersWithGivenFilters(String url, int numResults) throws Exception {
    var response = mockMvc.perform(get(url))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseAllEmployeesDto.class);
    assertEquals(numResults, dto.getResults().size());
  }

  @Test
  @DisplayName("/users/e0001: Fetches user with id e0001")
  void fetchUserById() throws Exception {
    var response = mockMvc.perform(get("/users/e0001"))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), EmployeeDto.class);
    assertEquals("e0001", dto.getId().get());
  }

  @Test
  @DisplayName("/users/e9999: Responds to GET request with status 400 given non-existent id")
  void fetchUserByIdReturnsStatus400ForNonExistentId() throws Exception {
    var response = mockMvc.perform(get("/users/e9999"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("No such employee", dto.getMessage());
  }

  @Test
  @DisplayName("/users/e9999: Responds to DELETE requests with status 400 given non-existent id")
  void deleteUserByIdReturnsStatus400ForNonExistentId() throws Exception {
    var response = mockMvc.perform(delete("/users/e9999"))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("No such employee", dto.getMessage());
  }

  @Test
  @DisplayName("/users/e9999: Responds to DELETE requests with status 200 given valid id")
  void deleteUserByIdReturnsStatus200ForValidId() throws Exception {
    var response = mockMvc.perform(delete("/users/e0001"))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully deleted", dto.getMessage());
    var employee = employeesRepository.findById("e0001");
    assertTrue(employee.isEmpty());
  }

  @Test
  @DisplayName("/users: Responds to POST requests with status 201 given valid request body")
  void createUserReturnsStatus201ForValidRequestBody() throws Exception {
    var dto = EmployeeDto.builder()
      .id(Optional.of("e9000"))
      .login(Optional.of("testuser"))
      .name(Optional.of("testname"))
      .salary(Optional.of(BigDecimal.ZERO))
      .startDate(Optional.of(LocalDate.now()))
      .build();
    var json = objectMapper.writeValueAsString(dto);
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andDo(print())
      .andExpect(status().isCreated())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully created", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users: Responds to POST requests with status 400 given duplicate id")
  void createUserReturnsStatus400GivenExistingId() throws Exception {
    var dto = EmployeeDto.builder()
      .id(Optional.of("e0001"))
      .login(Optional.of("testuser"))
      .name(Optional.of("testname"))
      .salary(Optional.of(BigDecimal.ZERO))
      .startDate(Optional.of(LocalDate.now()))
      .build();
    var json = objectMapper.writeValueAsString(dto);
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Employee ID already exists", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users: Responds to POST requests with status 400 given duplicate login")
  void createUserReturnsStatus400GivenExistingLogin() throws Exception {
    var dto = EmployeeDto.builder()
      .id(Optional.of("e0099"))
      .login(Optional.of("hpotter"))
      .name(Optional.of("testname"))
      .salary(Optional.of(BigDecimal.ZERO))
      .startDate(Optional.of(LocalDate.now()))
      .build();
    var json = objectMapper.writeValueAsString(dto);
    var response = mockMvc.perform(
        post("/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andDo(print())
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
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid input", responseDto.getMessage());
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
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employee = employeesRepository.findById("e0001").orElseThrow(NullPointerException::new);
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
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid id", responseDto.getMessage());
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
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
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid input", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PATCH requests with status 200 given valid partial DTO")
  void updateUserReturnsStatus200GivenValidPartialDTO() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": \"testuser\"" +
      "}";
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andDo(print())
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully updated", responseDto.getMessage());
    var employee = employeesRepository.findById("e0001").get();
    assertEquals("testuser", employee.getLogin());
  }

  @Test
  @DisplayName("/users/{id}: Responds to PATCH requests with status 400 given partial DTO with numeric login")
  void updateUserReturnsStatus400GivenValidPartialDTO() throws Exception {
    var json = "{\n" +
      "  \"id\": \"e0001\",\n" +
      "  \"login\": 123" +
      "}";
    var response = mockMvc.perform(
        patch("/users/e0001")
          .contentType(MediaType.APPLICATION_JSON)
          .content(json))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Invalid login", responseDto.getMessage());
    var employee = employeesRepository.findById("e0001").get();
    assertEquals("hpotter", employee.getLogin());
  }

  @Test
  @DisplayName("Generic server error message is shown for uncaught exceptions")
  void genericServerErrorIsShownForUncaughtExceptions() throws Exception {
    var mockRepository = Mockito.mock(EmployeesRepository.class);
    ReflectionTestUtils.setField(employeesService, "employeesRepository", mockRepository);
    Mockito.when(mockRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
    var response = mockMvc.perform(get("/users"))
      .andDo(print())
      .andExpect(status().isInternalServerError())
      .andReturn()
      .getResponse();
    ReflectionTestUtils.setField(employeesService, "employeesRepository", employeesRepository);
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Internal server error", responseDto.getMessage());
  }

  @Test
  @DisplayName("/users/upload: Repeated uploads return status 200")
  void uploadingOfSameFileReturnsStatus200() throws Exception {
    var currentCount = employeesRepository.count();
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, "sample_data.csv");
    var response = mockMvc.perform(
        MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
          .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
          .characterEncoding(StandardCharsets.UTF_8)
      ).andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var afterCount = employeesRepository.count();
    assertEquals(currentCount, afterCount);
  }

  @ParameterizedTest(name = "{index}: {0}")
  @DisplayName("/users/upload: Uploading csv files with invalid rows returns status 400")
  @CsvSource({
    "sample_data_duplicate_ids.csv,Duplicate ids in file: e0001",
    "sample_data_duplicate_logins.csv,Duplicate logins in file: hpotter",
    "sample_data_invalid_date_format.csv,Invalid date",
    "sample_data_missing_data_in_row.csv,Row 1: Invalid name",
    "sample_data_negative_salary.csv,Row 1: Invalid salary"})
  void uploadingCsvWithInvalidRowsReturnsStatus400(String fileName, String responseMessage) throws Exception {
    var employeesBefore = employeesRepository.findAll();
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, fileName);
    var response = mockMvc.perform(
        MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
          .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
          .characterEncoding(StandardCharsets.UTF_8)
      ).andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals(responseMessage, responseDto.getMessage());
    var employeesAfter = employeesRepository.findAll();
    assertEquals(employeesBefore, employeesAfter);
  }

  @Test
  @DisplayName("/users/upload: Row prefixed with # is not processed in csv file")
  void uploadingCsvWithCommentedOutRowResultsInRowNotProcessed() throws Exception {
    employeesRepository.deleteAll();
    var employeesBefore = employeesRepository.findAll();
    assertEquals(0, employeesBefore.size());
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, "sample_data_with_comments.csv");
    var response = mockMvc.perform(
        MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
          .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
          .characterEncoding(StandardCharsets.UTF_8)
      ).andExpect(status().isCreated())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var employeesAfter = employeesRepository.findAll();
    assertEquals(4, employeesAfter.size());
  }

  @Test
  @DisplayName("/users/upload: UTF-8 characters are preserved in the processed data")
  void uploadingCsvWithUtf8CharactersPreservesCharacters() throws Exception {
    employeesRepository.deleteAll();
    var employeesBefore = employeesRepository.findAll();
    assertEquals(0, employeesBefore.size());
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, "sample_data_with_non_english_characters.csv");
    var response = mockMvc.perform(
        MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
          .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
          .characterEncoding(StandardCharsets.UTF_8)
      ).andExpect(status().isCreated())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var employee1 = employeesRepository.findById("e0001").get();
    var employee2 = employeesRepository.findById("e0002").get();
    assertEquals("安納", employee1.getName());
    assertEquals("彩红", employee2.getLogin());
  }

  @Test
  @DisplayName("/users/upload: Commas in quoted fields are preserved")
  void uploadingCsvWithCommasInFieldsPreservesFieldsAndCommas() throws Exception {
    employeesRepository.deleteAll();
    var employeesBefore = employeesRepository.findAll();
    assertEquals(0, employeesBefore.size());
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, "sample_data_with_comma_name.csv");
    var response = mockMvc.perform(
        MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
          .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
          .characterEncoding(StandardCharsets.UTF_8)
      ).andExpect(status().isCreated())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var employee1 = employeesRepository.findById("e0001").get();
    assertEquals("Ah Kow, Tan", employee1.getName());
  }

  private MockHttpServletResponse uploadFile(String fileName) throws Exception {
    var mockUploadedFile = TestHelpers.getFileForUpload(UPLOAD_FILE_FIELD_NAME, fileName);
    return mockMvc.perform(
        MockMvcRequestBuilders.multipart(UPLOAD_FILE_ENDPOINT)
          .file(UPLOAD_FILE_FIELD_NAME, mockUploadedFile.getBytes())
          .characterEncoding(StandardCharsets.UTF_8)
      ).andReturn()
      .getResponse();
  }

}
