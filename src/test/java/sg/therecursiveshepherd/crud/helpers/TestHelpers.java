package sg.therecursiveshepherd.crud.helpers;

import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

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
}
