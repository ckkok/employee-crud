package sg.therecursiveshepherd.crud.utils;

import com.fasterxml.jackson.databind.MappingIterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;
import sg.therecursiveshepherd.crud.configurations.ApplicationConfiguration;
import sg.therecursiveshepherd.crud.dtos.EmployeeDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CsvMapperTest {

  @Test
  @DisplayName("CsvMapper trims leading and trailing spaces from columns")
  void employeeCsvMapper() throws IOException {
    var csvMapper = new ApplicationConfiguration().employeeCsvMapper();
    try (var inputStream = new ByteArrayInputStream(getTestInput().getBytes(StandardCharsets.UTF_8))) {
      MappingIterator<EmployeeDto> it = csvMapper.readValues(inputStream.readAllBytes());
      var dtoList = it.readAll();
      assertFalse(StringUtils.hasText(dtoList.get(0).getLogin()));
      assertEquals("rwesley", dtoList.get(1).getLogin());
    }
  }

  private String getTestInput() {
    return "id,login,name,salary,startDate\n" +
      "e0011, ,Harry Potter,1234.00,16-Nov-01\n" +
      "e0012,rwesley ,Ron Weasley,19234.50,2001-11-16";
  }
}
