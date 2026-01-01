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
import java.sql.Connection;
import java.sql.Statement;

@Configuration
@Profile({ "mix2" })
@EnableJpaRepositories(basePackages = "rgonzalez.smbc.contacts.repository", entityManagerFactoryRef = "primaryEntityManagerFactory", transactionManagerRef = "primaryTransactionManager")
public class JpaConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(JpaConfiguration.class);

	@Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
	private String driverClassName;

	@Value("${spring.jpa.database-platform:}")
	private String hibernateDialect;

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

		// Create schema immediately for H2 before Hibernate tries to use it
		if (driverClassName.contains("h2")) {
			createSchemaIfNotExists(dataSource, "CONTACTS");
		}

		return dataSource;
	}

	private void createSchemaIfNotExists(DataSource dataSource, String schemaName) {
		try (Connection conn = dataSource.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
			logger.info("Created schema {} if not exists", schemaName);
		} catch (Exception e) {
			logger.warn("Error creating schema {}: {}", schemaName, e.getMessage());
		}
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

	// @Bean
	// public DataSource secondaryDataSource() {
	// HikariConfig config = new HikariConfig();
	// config.setJdbcUrl(secondaryDatasourceUrl);
	// config.setUsername(secondaryDatasourceUsername);
	// config.setPassword(secondaryDatasourcePassword);
	// config.setMaximumPoolSize(8);
	// config.setMinimumIdle(2);
	// config.setConnectionTimeout(30000);
	// config.setIdleTimeout(600000);
	// config.setMaxLifetime(1800000);
	// HikariDataSource dataSource = new HikariDataSource(config);
	// logger.info("Initialized secondaryDataSource: {}",
	// dataSource.getClass().getSimpleName());
	// return dataSource;
	// }

	// @Bean
	// public LocalContainerEntityManagerFactoryBean
	// secondaryEntityManagerFactory(DataSource secondaryDataSource) {
	// LocalContainerEntityManagerFactoryBean emf = new
	// LocalContainerEntityManagerFactoryBean();
	// emf.setDataSource(secondaryDataSource);
	// emf.setPackagesToScan("rgonzalez.smbc.contacts.model");
	// emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
	// emf.setJpaProperties(jpaProperties());
	// emf.setPersistenceUnitName("secondary");
	// logger.info("Initialized secondaryEntityManagerFactory: {} with persistence
	// unit: secondary",
	// emf.getClass().getSimpleName());
	// return emf;
	// }

	// @Bean
	// public PlatformTransactionManager secondaryTransactionManager(
	// LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
	// JpaTransactionManager tm = new JpaTransactionManager();
	// tm.setEntityManagerFactory(secondaryEntityManagerFactory.getObject());
	// logger.info("Initialized secondaryTransactionManager: {}",
	// tm.getClass().getSimpleName());
	// return tm;
	// }

	// SHARED JPA PROPERTIES

	private java.util.Properties jpaProperties() {
		java.util.Properties props = new java.util.Properties();

		// Auto-detect dialect based on driver class name if not explicitly set
		String dialect = hibernateDialect;
		if (dialect == null || dialect.isEmpty()) {
			if (driverClassName.contains("h2")) {
				dialect = "org.hibernate.dialect.H2Dialect";
				logger.info("Auto-detected H2 dialect from driver class");
			} else if (driverClassName.contains("postgresql")) {
				dialect = "org.hibernate.dialect.PostgreSQLDialect";
				logger.info("Auto-detected PostgreSQL dialect from driver class");
			} else {
				dialect = "org.hibernate.dialect.PostgreSQLDialect";
				logger.warn("Unknown driver, defaulting to PostgreSQL dialect");
			}
		}

		// props.setProperty("hibernate.dialect", dialect);
		props.setProperty("hibernate.hbm2ddl.auto", "drop-and-create");
		props.setProperty("hibernate.hbm2ddl.create_namespaces", "true");
		props.setProperty("hibernate.show_sql", "true");
		logger.info("JPA Properties - dialect: {}, ddl-auto: drop-and-create, create_namespaces: true", dialect);
		return props;
	}
}
