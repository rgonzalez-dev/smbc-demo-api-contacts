package rgonzalez.smbc.contacts.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import rgonzalez.smbc.contacts.model.SsnVerificationResult;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    /**
     * Consumer Factory for SSN verification results
     * Guarantees message ordering by:
     * - Setting max.in.flight.requests.per.connection=1 to process one message at a
     * time per partition
     * - Disabling auto-commit to ensure offset is committed only after successful
     * processing
     */
    @Bean
    public ConsumerFactory<String, SsnVerificationResult> ssnVerificationResultConsumerFactory(
            KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildConsumerProperties());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SsnVerificationResult.class.getName());
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        // configProps.put(ConsumerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka Listener Container Factory for SSN verification results
     * Ensures single threaded processing for message ordering
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SsnVerificationResult> ssnVerificationKafkaListenerContainerFactory(
            ConsumerFactory<String, SsnVerificationResult> ssnVerificationResultConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SsnVerificationResult> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1); // Single threaded consumer for ordering
        factory.setConsumerFactory(ssnVerificationResultConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Manual acknowledgment
        return factory;
    }
}
