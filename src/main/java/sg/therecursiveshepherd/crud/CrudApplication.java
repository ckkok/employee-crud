package sg.therecursiveshepherd.crud;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class CrudApplication {

  public static void main(String[] args) {
    new SpringApplicationBuilder()
      .sources(CrudApplication.class)
      .bannerMode(Banner.Mode.OFF)
      .run(args);
  }

}
