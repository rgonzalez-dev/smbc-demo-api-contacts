package rgonzalez.smbc.contacts.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Debug component that logs configuration properties at startup.
 * Helps verify that environment variables and properties are being resolved
 * correctly,
 * especially for Azure deployments with APPSETTING_ prefix.
 */
@Component
public class ConfigurationPropertiesLogger {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationPropertiesLogger.class);

    @Autowired
    private Environment environment;

    @EventListener(ApplicationStartedEvent.class)
    public void logConfigurationProperties() {
        logger.info("\n" + "=".repeat(80));
        logger.info("CONFIGURATION PROPERTIES AT STARTUP");
        logger.info("=".repeat(80));

        // Active profiles
        String[] activeProfiles = environment.getActiveProfiles();
        logger.info("Active Profiles: {}", String.join(", ", activeProfiles));

        // Primary datasource
        logger.info("\n--- Primary DataSource ---");
        logProperty("spring.datasource.url");
        logProperty("spring.datasource.username");
        logProperty("spring.datasource.password");

        // Secondary datasource
        logger.info("\n--- Secondary DataSource ---");
        logProperty("spring.datasource.secondary.url");
        logProperty("spring.datasource.secondary.username");
        logProperty("spring.datasource.secondary.password");

        // Kafka settings
        logger.info("\n--- Kafka Configuration ---");
        logProperty("spring.kafka.bootstrap-servers");
        logProperty("spring.kafka.properties.sasl.mechanism");
        logProperty("spring.kafka.properties.sasl.jaas.config");

        logger.info("\n" + "=".repeat(80));
    }

    private void logProperty(String propertyName) {
        String value = environment.getProperty(propertyName);
        if (value == null) {
            logger.info("  {}: NOT_SET", propertyName);
        } else if (propertyName.contains("password") || propertyName.contains("jaas")) {
            // Mask sensitive data
            logger.info("  {}: *** (masked)", propertyName);
        } else {
            logger.info("  {}: {}", propertyName, value);
        }
    }
}
