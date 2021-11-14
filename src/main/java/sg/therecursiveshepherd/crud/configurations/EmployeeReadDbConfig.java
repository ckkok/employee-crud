package sg.therecursiveshepherd.crud.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Profile("read")
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "readEntityManagerFactory",
  transactionManagerRef = "readTransactionManager",
  basePackages = {"sg.therecursiveshepherd.crud.repositories.employees.read"}
)
public class EmployeeReadDbConfig {

  @Autowired
  private Environment env;

  @Primary
  @Bean(name = "readDataSource")
  @ConfigurationProperties(prefix = "read.datasource")
  public DataSource readDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Primary
  @Bean(name = "readEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean readEntityManagerFactory(
    EntityManagerFactoryBuilder builder,
    @Qualifier("readDataSource") DataSource dataSource) {
    return builder.dataSource(dataSource)
      .packages("sg.therecursiveshepherd.crud.entities")
      .persistenceUnit("employeeRead")
      .build();
  }

  @Primary
  @Bean(name = "readTransactionManager")
  public PlatformTransactionManager transactionManager(@Qualifier("readEntityManagerFactory") EntityManagerFactory factory) {
    return new JpaTransactionManager(factory);
  }
}
