package sg.therecursiveshepherd.crud.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@Profile("write")
@EnableTransactionManagement
@EnableJpaRepositories(
  entityManagerFactoryRef = "writeEntityManagerFactory",
  transactionManagerRef = "writeTransactionManager",
  basePackages = {"sg.therecursiveshepherd.crud.repositories.employees.write"}
)
public class EmployeeWriteDbConfig {

  @Bean(name = "writeDataSource")
  @ConfigurationProperties(prefix = "write.datasource")
  public DataSource writeDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean(name = "writeEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
    EntityManagerFactoryBuilder builder,
    @Qualifier("writeDataSource") DataSource dataSource) {
    return builder.dataSource(dataSource)
      .packages("sg.therecursiveshepherd.crud.entities")
      .persistenceUnit("employeeWrite")
      .build();
  }

  @Bean(name = "writeTransactionManager")
  public PlatformTransactionManager transactionManager(@Qualifier("writeEntityManagerFactory") EntityManagerFactory factory) {
    return new JpaTransactionManager(factory);
  }
}
