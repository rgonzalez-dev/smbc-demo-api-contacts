package rgonzalez.smbc.contacts.events;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Kafka Configuration composite that imports all Kafka related configurations.
 * This class provides access to all Kafka configuration properties and beans.
 * 
 * Segregated into separate configuration classes:
 * - KafkaTopicConfig: Handles topic creation
 * - KafkaProducerConfig: Handles producer configuration
 * - KafkaConsumerConfig: Handles consumer configuration
 */
@Configuration
@Import({
        KafkaTopicConfig.class,
        KafkaProducerConfig.class,
        KafkaConsumerConfig.class
})
public class KafkaConfig {

    // Topic constants are available through KafkaTopicConfig
    // public static final String CUSTOMER_SSN_VERIFIED_TOPIC =
    // KafkaTopicConfig.CUSTOMER_SSN_VERIFIED_TOPIC;
}
