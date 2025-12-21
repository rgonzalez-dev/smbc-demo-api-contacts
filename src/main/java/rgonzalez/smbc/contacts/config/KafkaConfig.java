package rgonzalez.smbc.contacts.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import rgonzalez.smbc.contacts.model.BusinessEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    public static final String CONTACTS_TOPIC = "contacts";
    public static final String BUSINESS_EVENTS_TOPIC = "business-events";
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

    /**
     * Producer Factory for BusinessEvent with String serialization for keys and
     * JSON for values
     */
    @Bean
    public ProducerFactory<String, BusinessEvent> businessEventProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * KafkaTemplate for sending BusinessEvent messages
     * The aggregate id will be used as the message key
     */
    @Bean
    public KafkaTemplate<String, BusinessEvent> businessEventKafkaTemplate(
            ProducerFactory<String, BusinessEvent> businessEventProducerFactory) {
        return new KafkaTemplate<>(businessEventProducerFactory);
    }

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
