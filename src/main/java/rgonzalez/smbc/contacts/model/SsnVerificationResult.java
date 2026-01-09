package rgonzalez.smbc.contacts.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity for storing SSN verification results received from the
 * integration-api.
 * Persists verification outcomes for audit and tracking purposes.
 */
@Entity
@Table(name = "ssn_verification_results", schema = "contacts")
@EntityListeners(AuditingEntityListener.class)
public class SsnVerificationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String contactId;

    @Column(nullable = false, length = 11)
    private String ssn;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private boolean isMatching;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 50)
    private String verificationSource;

    @Column(nullable = false)
    private Long verificationTimestamp;

    @Embedded
    private Traceable traceable = new Traceable();

    // Constructors
    public SsnVerificationResult() {
    }

    public SsnVerificationResult(String contactId, String ssn, String firstName, String lastName,
            String status, boolean isMatching, String message,
            String verificationSource, Long verificationTimestamp) {
        this.contactId = contactId;
        this.ssn = ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.isMatching = isMatching;
        this.message = message;
        this.verificationSource = verificationSource;
        this.verificationTimestamp = verificationTimestamp;
        this.traceable = new Traceable();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isMatching() {
        return isMatching;
    }

    public void setMatching(boolean matching) {
        isMatching = matching;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVerificationSource() {
        return verificationSource;
    }

    public void setVerificationSource(String verificationSource) {
        this.verificationSource = verificationSource;
    }

    public Long getVerificationTimestamp() {
        return verificationTimestamp;
    }

    public void setVerificationTimestamp(Long verificationTimestamp) {
        this.verificationTimestamp = verificationTimestamp;
    }

    public Traceable getTraceable() {
        return traceable;
    }

    public void setTraceable(Traceable traceable) {
        this.traceable = traceable != null ? traceable : new Traceable();
    }

    @Override
    public String toString() {
        return "SsnVerificationResult{" +
                "id=" + id +
                ", contactId='" + contactId + '\'' +
                ", ssn='" + ssn + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status='" + status + '\'' +
                ", isMatching=" + isMatching +
                ", message='" + message + '\'' +
                ", verificationSource='" + verificationSource + '\'' +
                ", verificationTimestamp=" + verificationTimestamp +
                ", traceable=" + traceable +
                '}';
    }
}
