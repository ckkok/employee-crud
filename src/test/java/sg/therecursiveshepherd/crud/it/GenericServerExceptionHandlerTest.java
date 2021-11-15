package sg.therecursiveshepherd.crud.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import sg.therecursiveshepherd.crud.dtos.ApiResponseDto;
import sg.therecursiveshepherd.crud.repositories.employees.read.EmployeeReadRepository;
import sg.therecursiveshepherd.crud.services.EmployeeReadService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles({"read", "test"})
class GenericServerExceptionHandlerTest {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeReadRepository employeeReadRepository;

  @Autowired
  private EmployeeReadService employeeReadService;

  @Test
  @DisplayName("Generic server error message is shown for uncaught exceptions")
  void genericServerErrorIsShownForUncaughtExceptions() throws Exception {
    var mockRepository = Mockito.mock(EmployeeReadRepository.class);
    ReflectionTestUtils.setField(employeeReadService, "employeeReadRepository", mockRepository);
    Mockito.when(mockRepository.findBySalaryGreaterThanEqualAndSalaryLessThan(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
    Mockito.when(mockRepository.findBySalaryGreaterThanEqualAndSalaryLessThanAndStartDateGreaterThanEqualAndStartDateLessThan(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(RuntimeException.class);
    var response = mockMvc.perform(get("/users"))
      .andExpect(status().isInternalServerError())
      .andReturn()
      .getResponse();
    ReflectionTestUtils.setField(employeeReadService, "employeeReadRepository", employeeReadRepository);
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Internal server error", responseDto.getMessage());
  }

  @Test
  @DisplayName("Unsupported operation message is shown for calling endpoints with unsupported operation")
  void unsupportedOperationMessageShownForCallingUnsupportedActionOnEndpoints() throws Exception {
    var response = mockMvc.perform(post("/users"))
      .andExpect(status().isMethodNotAllowed())
      .andReturn()
      .getResponse();
    var responseDto = objectMapper.readValue(response.getContentAsString(), ApiResponseDto.class);
    assertEquals("Unsupported operation", responseDto.getMessage());
  }

}
