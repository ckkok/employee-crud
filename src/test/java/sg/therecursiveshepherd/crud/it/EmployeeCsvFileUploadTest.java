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
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.helpers.TestHelpers;
import sg.therecursiveshepherd.crud.repositories.employees.write.EmployeeWriteRepository;
import sg.therecursiveshepherd.crud.services.EmployeeWriteService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles({"write", "test"})
class EmployeeCsvFileUploadTest {

  private static final String UPLOAD_FILE_FIELD_NAME = "file";
  private static final String UPLOAD_FILE_ENDPOINT = "/users/upload";

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
    uploadFile("sample_data.csv");
  }

  @AfterEach
  void teardown() {
    employeeWriteRepository.deleteAll();
  }

  @Test
  @DisplayName("/users/upload: Repeated uploads return status 200")
  void uploadingOfSameFileReturnsStatus200() throws Exception {
    var currentCount = employeeWriteRepository.count();
    var response = uploadFile("sample_data.csv");
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var afterCount = employeeWriteRepository.count();
    assertEquals(currentCount, afterCount);
  }

  @Test
  @DisplayName("/users/upload: Uploading file with different row returns status 201")
  void uploadingOfFileWithDifferentRowUpdatesThatRow() throws Exception {
    var currentCount = employeeWriteRepository.count();
    var response = uploadFile("sample_data_updated.csv");
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var afterCount = employeeWriteRepository.count();
    assertEquals(currentCount, afterCount);
    var updatedEmployeeLogin = employeeWriteRepository.findById("e0001").get().getLogin();
    assertEquals("hpotter1", updatedEmployeeLogin);
    var updatedEmployeeName = employeeWriteRepository.findById("e0002").get().getName();
    assertEquals("Randy Weasley", updatedEmployeeName);
    var updatedEmployeeSalary = employeeWriteRepository.findById("e0003").get().getSalary();
    assertEquals(0, updatedEmployeeSalary.compareTo(new BigDecimal("9000.0")));
    var updatedEmployeeStartDate = employeeWriteRepository.findById("e0004").get().getStartDate();
    assertEquals(LocalDate.of(2002, 11, 16), updatedEmployeeStartDate);
  }

  @ParameterizedTest(name = "{index}: {0}")
  @DisplayName("/users/upload: Uploading csv files with invalid rows returns status 400")
  @CsvSource({
    "sample_data_duplicate_ids.csv,Duplicate ids in file: e0001",
    "sample_data_duplicate_logins.csv,Duplicate logins in file: hpotter",
    "sample_data_duplicate_login_in_db.csv,Employee login not unique",
    "sample_data_invalid_date_format.csv,Row 1: Invalid date",
    "sample_data_missing_data_in_row.csv,Row 1: Invalid name",
    "sample_data_negative_salary.csv,Row 1: Invalid salary"
  })
  void uploadingCsvWithInvalidRowsReturnsStatus400(String fileName, String responseMessage) throws Exception {
    var employeesBefore = employeeWriteRepository.findAll();
    var response = uploadFile(fileName);
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    ApiResponseDto<List<String>> responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals(responseMessage, responseDto.getMessage().get(0));
    var employeesAfter = employeeWriteRepository.findAll();
    assertEquals(employeesBefore, employeesAfter);
  }

  @Test
  @DisplayName("/users/upload: Row prefixed with # is not processed in csv file")
  void uploadingCsvWithCommentedOutRowResultsInRowNotProcessed() throws Exception {
    employeeWriteRepository.deleteAll();
    var employeesBefore = employeeWriteRepository.findAll();
    assertEquals(0, employeesBefore.size());
    var response = uploadFile("sample_data_with_comments.csv");
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var employeesAfter = employeeWriteRepository.findAll();
    assertEquals(4, employeesAfter.size());
  }

  @Test
  @DisplayName("/users/upload: UTF-8 characters are preserved in the processed data")
  void uploadingCsvWithUtf8CharactersPreservesCharacters() throws Exception {
    employeeWriteRepository.deleteAll();
    var employeesBefore = employeeWriteRepository.findAll();
    assertEquals(0, employeesBefore.size());
    var response = uploadFile("sample_data_with_non_english_characters.csv");
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var employee1 = employeeWriteRepository.findById("e0001").get();
    var employee2 = employeeWriteRepository.findById("e0002").get();
    assertEquals("安納", employee1.getName());
    assertEquals("彩红", employee2.getLogin());
  }

  @Test
  @DisplayName("/users/upload: Commas in quoted fields are preserved")
  void uploadingCsvWithCommasInFieldsPreservesFieldsAndCommas() throws Exception {
    employeeWriteRepository.deleteAll();
    var employeesBefore = employeeWriteRepository.findAll();
    assertEquals(0, employeesBefore.size());
    var response = uploadFile("sample_data_with_comma_name.csv");
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully processed", responseDto.getMessage());
    var employee1 = employeeWriteRepository.findById("e0001").get();
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
