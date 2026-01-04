package rgonzalez.smbc.contacts.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "contacts", schema = "contacts", indexes = {
        @Index(name = "idx_ssn", columnList = "ssn", unique = true),
        @Index(name = "idx_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 255)
    private final String name;

    @Column(nullable = false, length = 11)
    private final String ssn;

    @Column(nullable = false, length = 100)
    private final String firstName;

    @Column(nullable = false, length = 100)
    private final String lastName;

    @Column(length = 1)
    private final String middleInitial;

    @JsonIgnore
    @Column(nullable = false, length = 20)
    private String ssnVerificationStatus = "not-verified";

    @Embedded
    @JsonIgnore(false)
    private Traceable traceable = new Traceable();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Phone> phones = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Email> emails = new ArrayList<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Address> addresses = new ArrayList<>();

    // Constructors
    public Contact() {
        this.name = null;
        this.ssn = null;
        this.firstName = null;
        this.lastName = null;
        this.middleInitial = null;
    }

    @JsonCreator
    public Contact(
            @JsonProperty("name") String name,
            @JsonProperty("ssn") String ssn,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("middleInitial") String middleInitial,
            @JsonProperty("createdBy") String createdBy,
            @JsonProperty("createdTimestamp") LocalDateTime createdTimestamp,
            @JsonProperty("updatedBy") String updatedBy,
            @JsonProperty("updatedTimestamp") LocalDateTime updatedTimestamp) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.ssn = Objects.requireNonNull(ssn, "ssn cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName cannot be null");
        this.middleInitial = middleInitial;
        this.traceable = new Traceable(createdBy, createdTimestamp, updatedBy, updatedTimestamp);
    }

    public Contact(String name, String ssn, String firstName, String lastName, String middleInitial) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.ssn = Objects.requireNonNull(ssn, "ssn cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName cannot be null");
        this.middleInitial = middleInitial;
        this.traceable = new Traceable();
    }

    /**
     * Copy constructor for creating an immutable copy of this Contact
     */
    public Contact(Contact other) {
        this.id = other.id;
        this.name = other.name;
        this.ssn = other.ssn;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.middleInitial = other.middleInitial;
        this.ssnVerificationStatus = other.ssnVerificationStatus;
        this.traceable = new Traceable(other.traceable);
        // Defensive copy of collections
        this.phones = new ArrayList<>(other.phones);
        this.emails = new ArrayList<>(other.emails);
        this.addresses = new ArrayList<>(other.addresses);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getSsn() {
        return ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public String getSsnVerificationStatus() {
        return ssnVerificationStatus;
    }

    public void setSsnVerificationStatus(String ssnVerificationStatus) {
        this.ssnVerificationStatus = ssnVerificationStatus;
    }

    public Traceable getTraceable() {
        return traceable;
    }

    public void setTraceable(Traceable traceable) {
        this.traceable = traceable != null ? traceable : new Traceable();
    }

    public List<Phone> getPhones() {
        return Collections.unmodifiableList(phones);
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public List<Email> getEmails() {
        return Collections.unmodifiableList(emails);
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    // Helper methods for relationships
    public void addPhone(Phone phone) {
        phones.add(phone);
        phone.setContact(this);
    }

    public void removePhone(Phone phone) {
        phones.remove(phone);
        phone.setContact(null);
    }

    public void addEmail(Email email) {
        emails.add(email);
        email.setContact(this);
    }

    public void removeEmail(Email email) {
        emails.remove(email);
        email.setContact(null);
    }

    public void addAddress(Address address) {
        addresses.add(address);
        address.setContact(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setContact(null);
    }

    /**
     * Creates an immutable copy of this Contact instance
     */
    public Contact toImmutableCopy() {
        return new Contact(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Contact contact = (Contact) o;
        return Objects.equals(id, contact.id) &&
                Objects.equals(ssn, contact.ssn) &&
                Objects.equals(name, contact.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ssn, name);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ssn='" + ssn + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", ssnVerificationStatus='" + ssnVerificationStatus + '\'' +
                '}';
    }

    /**
     * Static builder class for constructing Contact instances using the builder
     * pattern
     */
    public static class Builder {
        private Long id;
        private String name;
        private String ssn;
        private String firstName;
        private String lastName;
        private String middleInitial;
        private String ssnVerificationStatus = "not-verified";
        private Traceable traceable = new Traceable();
        private List<Phone> phones = new ArrayList<>();
        private List<Email> emails = new ArrayList<>();
        private List<Address> addresses = new ArrayList<>();

        public Builder() {
        }

        /**
         * Copy constructor for builder - initializes from existing Contact
         */
        public Builder(Contact contact) {
            this.id = contact.id;
            this.name = contact.name;
            this.ssn = contact.ssn;
            this.firstName = contact.firstName;
            this.lastName = contact.lastName;
            this.middleInitial = contact.middleInitial;
            this.ssnVerificationStatus = contact.ssnVerificationStatus;
            this.traceable = new Traceable(contact.traceable);
            this.phones = new ArrayList<>(contact.phones);
            this.emails = new ArrayList<>(contact.emails);
            this.addresses = new ArrayList<>(contact.addresses);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder ssn(String ssn) {
            this.ssn = ssn;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder middleInitial(String middleInitial) {
            this.middleInitial = middleInitial;
            return this;
        }

        public Builder ssnVerificationStatus(String ssnVerificationStatus) {
            this.ssnVerificationStatus = ssnVerificationStatus;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.traceable.setCreatedBy(createdBy);
            return this;
        }

        public Builder createdTimestamp(LocalDateTime createdTimestamp) {
            this.traceable.setCreatedTimestamp(createdTimestamp);
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.traceable.setUpdatedBy(updatedBy);
            return this;
        }

        public Builder updatedTimestamp(LocalDateTime updatedTimestamp) {
            this.traceable.setUpdatedTimestamp(updatedTimestamp);
            return this;
        }

        public Builder traceable(Traceable traceable) {
            this.traceable = traceable != null ? traceable : new Traceable();
            return this;
        }

        public Builder phones(List<Phone> phones) {
            this.phones = phones;
            return this;
        }

        public Builder emails(List<Email> emails) {
            this.emails = emails;
            return this;
        }

        public Builder addresses(List<Address> addresses) {
            this.addresses = addresses;
            return this;
        }

        /**
         * Builds and returns a new Contact instance
         * 
         * @return a new Contact with the configured builder properties
         * @throws IllegalStateException if required fields are not set
         */
        public Contact build() {
            Contact contact = new Contact(
                    Objects.requireNonNull(name, "name is required"),
                    Objects.requireNonNull(ssn, "ssn is required"),
                    Objects.requireNonNull(firstName, "firstName is required"),
                    Objects.requireNonNull(lastName, "lastName is required"),
                    middleInitial);

            contact.id = this.id;
            contact.ssnVerificationStatus = this.ssnVerificationStatus;
            contact.traceable = new Traceable(this.traceable);
            contact.phones = new ArrayList<>(this.phones);
            contact.emails = new ArrayList<>(this.emails);
            contact.addresses = new ArrayList<>(this.addresses);

            return contact;
        }
    }

    /**
     * Creates a new builder for constructing Contact instances
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a builder initialized with values from this Contact
     */
    public Builder toBuilder() {
        return new Builder(this);
    }
}
