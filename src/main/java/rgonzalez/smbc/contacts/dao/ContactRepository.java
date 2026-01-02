package rgonzalez.smbc.contacts.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rgonzalez.smbc.contacts.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
