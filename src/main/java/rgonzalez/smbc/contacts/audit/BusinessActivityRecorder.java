package rgonzalez.smbc.contacts.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import rgonzalez.smbc.contacts.dao.BusinessEventRepository;
import rgonzalez.smbc.contacts.model.BusinessEvent;
import rgonzalez.smbc.contacts.model.system.BusinessActivity;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
public class BusinessActivityRecorder {

    private static final Logger logger = LoggerFactory.getLogger(BusinessActivityRecorder.class);
    private final BusinessEventRepository businessEventRepository;
    private final KafkaTemplate<String, BusinessEvent> businessEventKafkaTemplate;
    private final ObjectMapper objectMapper;

    public BusinessActivityRecorder(BusinessEventRepository businessEventRepository,
            KafkaTemplate<String, BusinessEvent> businessEventKafkaTemplate) {
        this.businessEventRepository = businessEventRepository;
        this.businessEventKafkaTemplate = businessEventKafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Pointcut that matches all public methods annotated with @BusinessActivity
     */
    @Pointcut("@annotation(rgonzalez.smbc.contacts.model.system.BusinessActivity)") // && execution(public * *(..))
    public void businessActivityMethods() {
    }

    /**
     * Around advice that records business activity for methods annotated
     * with @BusinessActivity
     * 
     * @param joinPoint        the join point
     * @param businessActivity the BusinessActivity annotation instance
     * @return the result of the method invocation
     * @throws Throwable if an exception occurs during method execution
     */
    @Around("businessActivityMethods() && @annotation(businessActivity)")
    public Object recordBusinessActivity(ProceedingJoinPoint joinPoint, BusinessActivity businessActivity)
            throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        try {
            logger.info("Recording Business Activity - Class: {}, Method: {}, Parameters: {}",
                    className, methodName, Arrays.toString(args));

            Object result = joinPoint.proceed();

            if (businessActivity.isAuditable()) {
                // Create and persist the business event
                createAndPublishBusinessEvent(result, businessActivity);
            }

            return result;
        } catch (Throwable throwable) {

            logger.error("Business Activity Failed - Class: {}, Method: {}, Error: {}",
                    className, methodName, throwable.getMessage(), throwable);
            throw throwable;
        }
    }

    /**
     * Creates and publishes a business event based on the activity result and
     * annotation
     * 
     * @param result           the result object from the business activity method
     * @param businessActivity the BusinessActivity annotation with event details
     */
    private void createAndPublishBusinessEvent(Object result, BusinessActivity businessActivity) {
        try {
            String eventPayload = objectMapper.writeValueAsString(result);

            // Extract aggregate ID from result if it has an id field, otherwise use a
            // default
            String aggregateId = extractAggregateId(result);
            String aggregateName = result.getClass().getSimpleName();
            String eventName = businessActivity.eventName();
            String schema = generateSchema(aggregateName, eventName);

            BusinessEvent businessEvent = new BusinessEvent(
                    UUID.randomUUID().toString(),
                    aggregateId,
                    aggregateName,
                    eventName,
                    eventPayload,
                    schema,
                    null,
                    BusinessEvent.EventDirection.OUTBOUND);

            // Persist the event to database
            businessEventRepository.save(businessEvent);

            // Send the event to Kafka with aggregate id as the message key
            businessEventKafkaTemplate.send("contacts", businessEvent.getAggregateId(), businessEvent);

            logger.info("Business Event published - Event: {}, Aggregate: {}, Schema: {}",
                    eventName, aggregateName, schema);
        } catch (Exception e) {
            logger.error("Failed to create and publish business event", e);
            // Don't throw exception to avoid affecting the main business logic
        }
    }

    /**
     * Extracts the aggregate ID from the result object using the getId() getter
     * method
     * 
     * @param result the result object
     * @return the aggregate ID as string, or UUID if no id field is found
     */
    private String extractAggregateId(Object result) {
        try {
            if (result != null) {
                // Try to invoke getId() method
                java.lang.reflect.Method getIdMethod = result.getClass().getMethod("getId");
                Object idValue = getIdMethod.invoke(result);
                return idValue != null ? idValue.toString() : UUID.randomUUID().toString();
            }
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            logger.debug("Could not extract id from result object using getId() method", e);
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a schema name from aggregate and event names
     * 
     * @param aggregateName the aggregate name
     * @param eventName     the event name
     * @return the generated schema name
     */
    private String generateSchema(String aggregateName, String eventName) {
        return (aggregateName.toLowerCase() + "-" + eventName.toLowerCase()).replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }

    /**
     * Retrieves the current authenticated username from Spring Security context
     * 
     * @return the username of the authenticated user, or "SYSTEM" if no user is
     *         authenticated
     */
    // private String getCurrentUsername() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();
    // if (authentication != null && authentication.isAuthenticated()) {
    // return authentication.getName();
    // }
    // return "SYSTEM";
    // }
}
