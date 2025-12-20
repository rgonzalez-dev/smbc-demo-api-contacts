package rgonzalez.smbc.contacts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rgonzalez.smbc.contacts.model.Contact;
import rgonzalez.smbc.contacts.repository.ContactRepository;
import rgonzalez.smbc.contacts.service.ContactService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
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
                    existingContact.setName(contact.getName());
                    // Update other fields as needed
                    return contactRepository.save(existingContact);
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
