package rgonzalez.smbc.contacts.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import rgonzalez.smbc.contacts.model.BusinessEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

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
        // Don't include type headers for BusinessEvent since integration-api expects
        // its own type
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

}
