package rgonzalez.smbc.contacts.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rgonzalez.smbc.contacts.config.KafkaConfig;
import rgonzalez.smbc.contacts.model.Contact;
import rgonzalez.smbc.contacts.model.SsnVerificationResult;
import rgonzalez.smbc.contacts.repository.ContactRepository;
import rgonzalez.smbc.contacts.repository.SsnVerificationResultRepository;

/**
 * Kafka event handler for SSN verification results.
 * Listens to the customer-ssn-verified topic and processes verification
 * outcomes
 * from the integration-api.
 */
@Service
public class SsnVerificationEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(SsnVerificationEventHandler.class);
    private final SsnVerificationResultRepository ssnVerificationResultRepository;
    private final ContactRepository contactRepository;

    public SsnVerificationEventHandler(SsnVerificationResultRepository ssnVerificationResultRepository,
            ContactRepository contactRepository) {
        this.ssnVerificationResultRepository = ssnVerificationResultRepository;
        this.contactRepository = contactRepository;
    }

    /**
     * Listens to the customer-ssn-verified topic and processes incoming
     * verification results
     * Persists verification outcomes and processes business logic based on
     * verification status
     *
     * @param verificationResult The SsnVerificationResult from the integration-api
     * @param contactId          The message key (contact/aggregate id)
     * @param partition          The partition this message came from
     * @param offset             The offset of this message
     * @param acknowledgment     Manual acknowledgment handler
     */
    @KafkaListener(topics = KafkaConfig.CUSTOMER_SSN_VERIFIED_TOPIC, containerFactory = "ssnVerificationKafkaListenerContainerFactory", groupId = "contacts-service")
    @Transactional
    public void handleSsnVerificationEvent(
            @Payload SsnVerificationResult verificationResult,
            @Header(KafkaHeaders.RECEIVED_KEY) String contactId,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {

        try {
            logger.info("Processing SSN verification event from partition [{}] with offset [{}]. " +
                    "ContactId: {}, Status: {}, Matching: {}",
                    partition, offset, contactId, verificationResult.getStatus(),
                    verificationResult.isMatching());

            // Persist the verification result to database
            SsnVerificationResult persistedResult = ssnVerificationResultRepository.save(verificationResult);
            logger.debug("SSN verification result persisted to database with id [{}]", persistedResult.getId());

            // Process the verification result
            processVerificationResult(verificationResult);

            // Manually acknowledge the message after successful processing
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                logger.debug("Message acknowledged for contact [{}] in partition [{}]",
                        contactId, partition);
            }

        } catch (Exception e) {
            logger.error("Error processing SSN verification event for contact [{}] from partition [{}]: {}",
                    contactId, partition, e.getMessage(), e);
            // Do not acknowledge on error - message will be retried
            throw new RuntimeException("Failed to process SSN verification event", e);
        }
    }

    /**
     * Process the SSN verification result
     * Implements business logic based on verification outcome
     *
     * @param verificationResult The verification result to process
     */
    private void processVerificationResult(SsnVerificationResult verificationResult) {
        logger.info("Processing SSN verification result - ContactId: {}, Status: {}, Matching: {}",
                verificationResult.getContactId(), verificationResult.getStatus(),
                verificationResult.isMatching());

        if (verificationResult.isMatching()) {
            handleSuccessfulVerification(verificationResult);
        } else {
            handleFailedVerification(verificationResult);
        }
    }

    /**
     * Handle successful SSN verification
     * Perform business logic when SSN verification succeeds
     *
     * @param verificationResult The successful verification result
     */
    private void handleSuccessfulVerification(SsnVerificationResult verificationResult) {
        logger.info("SSN verification successful for contact [{}]",
                verificationResult.getContactId());

        // Update contact verification status
        contactRepository.findById(Long.parseLong(verificationResult.getContactId())).ifPresent(contact -> {
            contact.setSsnVerificationStatus("verified");
            contactRepository.save(contact);
            logger.info("Contact [{}] marked as verified", verificationResult.getContactId());
        });

        // TODO: Implement additional business logic for successful verification
        // Examples:
        // - Send notification to user
        // - Trigger downstream processes
        // - Update audit logs
    }

    /**
     * Handle failed SSN verification
     * Perform business logic when SSN verification fails
     *
     * @param verificationResult The failed verification result
     */
    private void handleFailedVerification(SsnVerificationResult verificationResult) {
        logger.warn("SSN verification failed for contact [{}]: Status={}, Message={}",
                verificationResult.getContactId(),
                verificationResult.getStatus(),
                verificationResult.getMessage());

        // Update contact verification status
        contactRepository.findById(Long.parseLong(verificationResult.getContactId())).ifPresent(contact -> {
            contact.setSsnVerificationStatus("failed-verification");
            contactRepository.save(contact);
            logger.info("Contact [{}] marked as failed verification", verificationResult.getContactId());
        });

        // TODO: Implement additional business logic for failed verification
        // Examples:
        // - Flag contact for manual review
        // - Send alert notification
        // - Log verification failure reason
    }
}
