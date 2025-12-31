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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@Profile({ "mix2" })
@EnableJpaRepositories(basePackages = "rgonzalez.smbc.contacts.repository", entityManagerFactoryRef = "primaryEntityManagerFactory", transactionManagerRef = "primaryTransactionManager")
public class JpaConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(JpaConfiguration.class);

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
		HikariDataSource dataSource = new HikariDataSource(config);
		logger.info("Initialized primaryDataSource: {}", dataSource.getClass().getSimpleName());
		return dataSource;
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
		logger.info("Initialized primaryEntityManagerFactory: {} with persistence unit: primary",
				emf.getClass().getSimpleName());
		return emf;
	}

	@Bean
	@Primary
	public PlatformTransactionManager primaryTransactionManager(
			LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(primaryEntityManagerFactory.getObject());
		logger.info("Initialized primaryTransactionManager: {}", tm.getClass().getSimpleName());
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
		HikariDataSource dataSource = new HikariDataSource(config);
		logger.info("Initialized secondaryDataSource: {}", dataSource.getClass().getSimpleName());
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(DataSource secondaryDataSource) {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(secondaryDataSource);
		emf.setPackagesToScan("rgonzalez.smbc.contacts.model");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setJpaProperties(jpaProperties());
		emf.setPersistenceUnitName("secondary");
		logger.info("Initialized secondaryEntityManagerFactory: {} with persistence unit: secondary",
				emf.getClass().getSimpleName());
		return emf;
	}

	@Bean
	public PlatformTransactionManager secondaryTransactionManager(
			LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(secondaryEntityManagerFactory.getObject());
		logger.info("Initialized secondaryTransactionManager: {}", tm.getClass().getSimpleName());
		return tm;
	}

	// SHARED JPA PROPERTIES

	private java.util.Properties jpaProperties() {
		java.util.Properties props = new java.util.Properties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		props.setProperty("hibernate.hbm2ddl.auto", "drop-and-create");
		props.setProperty("hibernate.show_sql", "false");
		return props;
	}
}
