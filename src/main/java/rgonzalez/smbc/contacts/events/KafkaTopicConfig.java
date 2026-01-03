package rgonzalez.smbc.contacts.events;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaTopicConfig {

    public static final String CONTACTS_TOPIC = "contacts";
    public static final String BUSINESS_EVENTS_TOPIC = "business-events";
    public static final String CUSTOMER_SSN_VERIFIED_TOPIC = "customer-ssn-verified";
    public static final int PARTITIONS = 3;
    public static final short REPLICATION_FACTOR = 1;

    /**
     * Create the Contacts topic with 3 partitions
     * Only creates if kafka.auto-create-topics is enabled
     */
    @Bean
    @ConditionalOnProperty(name = "kafka.auto-create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic contactsTopic() {
        return new NewTopic(CONTACTS_TOPIC, PARTITIONS, REPLICATION_FACTOR);
    }

    /**
     * Create the Business Events topic with 3 partitions
     * Only creates if kafka.auto-create-topics is enabled
     */
    @Bean
    @ConditionalOnProperty(name = "kafka.auto-create-topics", havingValue = "true", matchIfMissing = false)
    public NewTopic businessEventsTopic() {
        return new NewTopic(BUSINESS_EVENTS_TOPIC, PARTITIONS, REPLICATION_FACTOR);
    }
}
