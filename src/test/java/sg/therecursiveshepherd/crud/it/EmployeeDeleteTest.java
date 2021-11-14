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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.helpers.TestHelpers;
import sg.therecursiveshepherd.crud.repositories.employees.write.EmployeeWriteRepository;
import sg.therecursiveshepherd.crud.services.EmployeeWriteService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles({"write", "test"})
class EmployeeDeleteTest {

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
  @DisplayName("/users/e0001: Responds to DELETE requests with status 200 given valid id")
  void deleteUserByIdReturnsStatus200ForValidId() throws Exception {
    var response = mockMvc.perform(delete("/users/e0001"))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Successfully deleted", dto.getMessage());
    var employee = employeeWriteRepository.findById("e0001");
    assertTrue(employee.isEmpty());
  }

  @Test
  @DisplayName("/users/e9999: Responds to DELETE requests with status 400 given non-existent id")
  void deleteUserByIdReturnsStatus400ForNonExistentId() throws Exception {
    var response = mockMvc.perform(delete("/users/e9999"))
      .andExpect(status().isBadRequest())
      .andReturn()
      .getResponse();
    var dto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("No such employee", dto.getMessage());
  }
}
