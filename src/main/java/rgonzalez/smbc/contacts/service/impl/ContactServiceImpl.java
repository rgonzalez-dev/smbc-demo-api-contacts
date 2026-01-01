package rgonzalez.smbc.contacts.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rgonzalez.smbc.contacts.config.KafkaTopicConfig;
import rgonzalez.smbc.contacts.model.BusinessEvent;
import rgonzalez.smbc.contacts.model.Contact;

import rgonzalez.smbc.contacts.repository.BusinessEventRepository;
import rgonzalez.smbc.contacts.repository.ContactRepository;
import rgonzalez.smbc.contacts.service.ContactService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final BusinessEventRepository businessEventRepository;
    private final KafkaTemplate<String, BusinessEvent> businessEventKafkaTemplate;
    private final ObjectMapper objectMapper;

    public ContactServiceImpl(ContactRepository contactRepository, BusinessEventRepository businessEventRepository,
            KafkaTemplate<String, BusinessEvent> businessEventKafkaTemplate) {
        this.contactRepository = contactRepository;
        this.businessEventRepository = businessEventRepository;
        this.businessEventKafkaTemplate = businessEventKafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    @Transactional
    public Contact createContact(Contact contact) {
        // Save the contact
        Contact savedContact = contactRepository.save(contact);

        // Create a BusinessEvent for this contact creation
        try {
            String eventPayload = objectMapper.writeValueAsString(savedContact);
            BusinessEvent businessEvent = new BusinessEvent(
                    UUID.randomUUID().toString(),
                    savedContact.getId().toString(),
                    "Contact",
                    "ContactCreated",
                    eventPayload,
                    "contact-created-v1", null,
                    BusinessEvent.EventDirection.OUTBOUND);
            businessEvent.setCreatedBy("system"); // Set createdBy if needed
            businessEvent.setUpdatedBy("system"); // Set updatedBy if needed
            businessEvent.setUpdatedTimestamp(LocalDateTime.now());
            // Persist the event to database
            businessEventRepository.save(businessEvent);

            // Send the event to Kafka with aggregate id as the message key
            businessEventKafkaTemplate.send(KafkaTopicConfig.CONTACTS_TOPIC,
                    businessEvent.getAggregateId(), businessEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create business event for contact", e);
        }

        return savedContact;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public Contact updateContact(Long id, Contact contact) {
        return contactRepository.findById(id)
                .map(existingContact -> {
                    // Since Contact core fields are now immutable, we need to delete and recreate
                    // or use a builder pattern. For now, we'll update the mutable audit fields
                    Contact updatedContact = existingContact.toBuilder()
                            .name(contact.getName())
                            .emails(contact.getEmails())
                            .phones(contact.getPhones())
                            .build();
                    updatedContact.setUpdatedBy("system");
                    updatedContact.setUpdatedTimestamp(LocalDateTime.now());
                    return contactRepository.save(updatedContact);
                })
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
    }

    @Override
    public void deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new RuntimeException("Contact not found with id: " + id);
        }
        contactRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean contactExists(Long id) {
        return contactRepository.existsById(id);
    }
}
