package rgonzalez.smbc.contacts.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rgonzalez.smbc.contacts.model.BusinessEvent;

@Repository
public interface BusinessEventRepository extends JpaRepository<BusinessEvent, Long> {
}
