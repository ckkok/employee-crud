package sg.therecursiveshepherd.crud.helpers;

import org.springframework.mock.web.MockMultipartFile;
import sg.therecursiveshepherd.crud.entities.Employee;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestHelpers {

  public static MockMultipartFile getFileForUpload(String fieldName, String resourceName) throws IOException {
    var fileBytes = readFileAsBytes(resourceName);
    return new MockMultipartFile(fieldName, resourceName, "multipart/form-data", fileBytes);
  }

  public static byte[] readFileAsBytes(String resourceName) throws IOException {
    var resource = TestHelpers.class.getClassLoader().getResourceAsStream(resourceName);
    if (resource == null) {
      throw new IOException("Resource not found: " + resourceName);
    }
    return resource.readAllBytes();
  }

  public static List<Employee> getBaselineData() {
    List<Employee> employees = new ArrayList<>();
    employees.add(Employee.builder()
      .id("e0001")
      .login("hpotter")
      .name("Harry Potter")
      .salary(BigDecimal.valueOf(1234.00))
      .startDate(LocalDate.of(2001, 11, 16))
      .build());
    employees.add(Employee.builder()
      .id("e0002")
      .login("rweasley")
      .name("Ron Weasley")
      .salary(BigDecimal.valueOf(19234.50))
      .startDate(LocalDate.of(2001, 11, 16))
      .build());
    employees.add(Employee.builder()
      .id("e0003")
      .login("ssnape")
      .name("Severus Snape")
      .salary(BigDecimal.valueOf(4000.0))
      .startDate(LocalDate.of(2001, 11, 16))
      .build());
    employees.add(Employee.builder()
      .id("e0004")
      .login("rhagrid")
      .name("Rubeus Hagrid")
      .salary(BigDecimal.valueOf(3999.999))
      .startDate(LocalDate.of(2001, 11, 16))
      .build());
    employees.add(Employee.builder()
      .id("e0005")
      .login("voldemort")
      .name("Lord Voldemort")
      .salary(BigDecimal.valueOf(523.4))
      .startDate(LocalDate.of(2001, 11, 17))
      .build());
    employees.add(Employee.builder()
      .id("e0006")
      .login("gwesley")
      .name("Ginny Weasley")
      .salary(BigDecimal.valueOf(4000.004))
      .startDate(LocalDate.of(2001, 11, 18))
      .build());
    employees.add(Employee.builder()
      .id("e0007")
      .login("hgranger")
      .name("Hermione Granger")
      .salary(BigDecimal.valueOf(0.0))
      .startDate(LocalDate.of(2001, 11, 18))
      .build());
    employees.add(Employee.builder()
      .id("e0008")
      .login("adumbledore")
      .name("Albus Dumbledore")
      .salary(BigDecimal.valueOf(34.23))
      .startDate(LocalDate.of(2001, 11, 19))
      .build());
    employees.add(Employee.builder()
      .id("e0009")
      .login("dmalfoy")
      .name("Draco Malfoy")
      .salary(BigDecimal.valueOf(34234.5))
      .startDate(LocalDate.of(2001, 11, 20))
      .build());
    employees.add(Employee.builder()
      .id("e0010")
      .login("basilisk")
      .name("Basilisk")
      .salary(BigDecimal.valueOf(23.43))
      .startDate(LocalDate.of(2001, 11, 21))
      .build());
    return employees;
  }

}
