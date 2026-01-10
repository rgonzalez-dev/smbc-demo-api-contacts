package rgonzalez.smbc.contacts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rgonzalez.smbc.contacts.api.ContactApi;
import rgonzalez.smbc.contacts.model.Contact;
import rgonzalez.smbc.contacts.service.ContactService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController implements ContactApi {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        Contact createdContact = contactService.createContact(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }

    @Override
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        return contact.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<List<Contact>> getAllContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    @Override
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        try {
            Contact updatedContact = contactService.updateContact(id, contact);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<Boolean> contactExists(@PathVariable Long id) {
        boolean exists = contactService.contactExists(id);
        return ResponseEntity.ok(exists);
    }
}
