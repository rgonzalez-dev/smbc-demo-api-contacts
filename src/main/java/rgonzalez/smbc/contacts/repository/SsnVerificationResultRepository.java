package rgonzalez.smbc.contacts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rgonzalez.smbc.contacts.model.SsnVerificationResult;

import java.util.List;

/**
 * Repository for SSN verification results.
 * Provides database access for SSN verification records received from
 * integration-api.
 */
@Repository
public interface SsnVerificationResultRepository extends JpaRepository<SsnVerificationResult, Long> {

    /**
     * Find all verification results for a specific contact.
     * 
     * @param contactId the contact ID
     * @return list of verification results
     */
    List<SsnVerificationResult> findByContactId(String contactId);

    /**
     * Find the most recent verification result for a contact.
     * 
     * @param contactId the contact ID
     * @return the most recent verification result, or null if none found
     */
    SsnVerificationResult findFirstByContactIdOrderByCreatedTimestampDesc(String contactId);

    /**
     * Find verification results by status.
     * 
     * @param status the verification status
     * @return list of verification results
     */
    List<SsnVerificationResult> findByStatus(String status);

    /**
     * Find verification results by matching status.
     * 
     * @param isMatching the matching status
     * @return list of verification results
     */
    List<SsnVerificationResult> findByMatching(boolean isMatching);
}
