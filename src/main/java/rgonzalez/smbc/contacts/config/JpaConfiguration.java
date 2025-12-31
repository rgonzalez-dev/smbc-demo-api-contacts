package rgonzalez.smbc.contacts.config;

import javax.sql.DataSource;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile({ "mix" })
@EnableJpaRepositories(basePackages = "rgonzalez.smbc.contacts.repository", entityManagerFactoryRef = "primaryEntityManagerFactory", transactionManagerRef = "primaryTransactionManager")
public class JpaConfiguration {

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${spring.datasource.username}")
	private String datasourceUsername;

	@Value("${spring.datasource.password}")
	private String datasourcePassword;

	@Value("${spring.datasource.secondary.url}")
	private String secondaryDatasourceUrl;

	@Value("${spring.datasource.secondary.username}")
	private String secondaryDatasourceUsername;

	@Value("${spring.datasource.secondary.password}")
	private String secondaryDatasourcePassword;

	// PRIMARY DATASOURCE

	@Bean
	@Primary
	public DataSource primaryDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(datasourceUrl);
		config.setUsername(datasourceUsername);
		config.setPassword(datasourcePassword);
		config.setMaximumPoolSize(10);
		config.setMinimumIdle(3);
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);
		return new HikariDataSource(config);
	}

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(DataSource primaryDataSource) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(primaryDataSource);
		emf.setPackagesToScan("rgonzalez.smbc.contacts.model");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setJpaProperties(jpaProperties());
		emf.setPersistenceUnitName("primary");
		return emf;
	}

	@Bean
	@Primary
	public PlatformTransactionManager primaryTransactionManager(
			LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(primaryEntityManagerFactory.getObject());
		return tm;
	}

	// SECONDARY DATASOURCE

	@Bean
	public DataSource secondaryDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(secondaryDatasourceUrl);
		config.setUsername(secondaryDatasourceUsername);
		config.setPassword(secondaryDatasourcePassword);
		config.setMaximumPoolSize(8);
		config.setMinimumIdle(2);
		config.setConnectionTimeout(30000);
		config.setIdleTimeout(600000);
		config.setMaxLifetime(1800000);
		return new HikariDataSource(config);
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(DataSource secondaryDataSource) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(secondaryDataSource);
		emf.setPackagesToScan("rgonzalez.smbc.contacts.model");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setJpaProperties(jpaProperties());
		emf.setPersistenceUnitName("secondary");
		return emf;
	}

	@Bean
	public PlatformTransactionManager secondaryTransactionManager(
			LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(secondaryEntityManagerFactory.getObject());
		return tm;
	}

	// SHARED JPA PROPERTIES

	private java.util.Properties jpaProperties() {
		java.util.Properties props = new java.util.Properties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		props.setProperty("hibernate.hbm2ddl.auto", "update");
		props.setProperty("hibernate.show_sql", "false");
		return props;
	}
}
