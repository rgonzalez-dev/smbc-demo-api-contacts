package rgonzalez.smbc.contacts.service;

import java.util.List;
import java.util.Optional;

import rgonzalez.smbc.contacts.model.Contact;

public interface ContactService {

    /**
     * Create a new contact
     * 
     * @param contact the contact to create
     * @return the created contact
     */
    Contact createContact(Contact contact);

    /**
     * Retrieve a contact by ID
     * 
     * @param id the contact ID
     * @return an Optional containing the contact if found
     */
    Optional<Contact> getContactById(Long id);

    /**
     * Retrieve all contacts
     * 
     * @return a list of all contacts
     */
    List<Contact> getAllContacts();

    /**
     * Update an existing contact
     * 
     * @param id      the contact ID
     * @param contact the updated contact data
     * @return the updated contact
     */
    Contact updateContact(Long id, Contact contact);

    /**
     * Delete a contact by ID
     * 
     * @param id the contact ID
     */
    void deleteContact(Long id);

    /**
     * Check if a contact exists by ID
     * 
     * @param id the contact ID
     * @return true if contact exists, false otherwise
     */
    boolean contactExists(Long id);
}
