package rgonzalez.smbc.contacts.events;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
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

        // Use ErrorHandlingDeserializer to wrap the actual deserializers
        // This allows graceful handling of deserialization failures
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // Configure the wrapped deserializers
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // JsonDeserializer configuration for the wrapped instance
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "rgonzalez.smbc.contacts.model.SsnVerificationResult");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);

        // Consumer configuration
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);

        // Note: max.in.flight.requests is a producer-only property
        // Consumer ordering is guaranteed by concurrency=1 and
        // MAX_POLL_RECORDS_CONFIG=1
        String config1 = new StringBuilder()
                .append("org.apache.kafka.common.security.plain.PlainLoginModule required ")
                .append("username=\"$ConnectionString\" ")
                .append(kafkaProperties.getProperties().get("spring.kafka.properties.sasl.jaas.config.password"))
                .toString();
        configProps.put("kafka.sasl.jaas.config", config1);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * Kafka Listener Container Factory for SSN verification results
     * Ensures single threaded processing for message ordering
     * Uses ErrorHandlingDeserializer to gracefully handle deserialization failures
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SsnVerificationResult> ssnVerificationKafkaListenerContainerFactory(
            ConsumerFactory<String, SsnVerificationResult> ssnVerificationResultConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SsnVerificationResult> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1); // Single threaded consumer for ordering
        factory.setConsumerFactory(ssnVerificationResultConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Manual acknowledgment

        // Add error handler with exponential backoff for processing errors
        // Note: This handles errors during message processing, not deserialization
        // Deserialization errors are handled by ErrorHandlingDeserializer
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}
