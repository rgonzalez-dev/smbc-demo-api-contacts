package rgonzalez.smbc.contacts.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "contacts", schema = "contacts")
@EntityListeners(AuditingEntityListener.class)
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTimestamp;

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String updatedBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedTimestamp;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Phone> phones = new HashSet<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Email> emails = new HashSet<>();

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Address> addresses = new HashSet<>();

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
        this.createdBy = createdBy;
        this.createdTimestamp = createdTimestamp;
        this.updatedBy = updatedBy;
        this.updatedTimestamp = updatedTimestamp;
    }

    public Contact(String name, String ssn, String firstName, String lastName, String middleInitial) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.ssn = Objects.requireNonNull(ssn, "ssn cannot be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName cannot be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName cannot be null");
        this.middleInitial = middleInitial;
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
        this.createdBy = other.createdBy;
        this.createdTimestamp = other.createdTimestamp;
        this.updatedBy = other.updatedBy;
        this.updatedTimestamp = other.updatedTimestamp;
        // Defensive copy of collections
        this.phones = new HashSet<>(other.phones);
        this.emails = new HashSet<>(other.emails);
        this.addresses = new HashSet<>(other.addresses);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public Set<Phone> getPhones() {
        return Collections.unmodifiableSet(phones);
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
    }

    public Set<Email> getEmails() {
        return Collections.unmodifiableSet(emails);
    }

    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public Set<Address> getAddresses() {
        return Collections.unmodifiableSet(addresses);
    }

    public void setAddresses(Set<Address> addresses) {
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
        private String createdBy;
        private LocalDateTime createdTimestamp;
        private String updatedBy;
        private LocalDateTime updatedTimestamp;
        private Set<Phone> phones = new HashSet<>();
        private Set<Email> emails = new HashSet<>();
        private Set<Address> addresses = new HashSet<>();

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
            this.createdBy = contact.createdBy;
            this.createdTimestamp = contact.createdTimestamp;
            this.updatedBy = contact.updatedBy;
            this.updatedTimestamp = contact.updatedTimestamp;
            this.phones = new HashSet<>(contact.phones);
            this.emails = new HashSet<>(contact.emails);
            this.addresses = new HashSet<>(contact.addresses);
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
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdTimestamp(LocalDateTime createdTimestamp) {
            this.createdTimestamp = createdTimestamp;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder updatedTimestamp(LocalDateTime updatedTimestamp) {
            this.updatedTimestamp = updatedTimestamp;
            return this;
        }

        public Builder phones(Set<Phone> phones) {
            this.phones = phones;
            return this;
        }

        public Builder emails(Set<Email> emails) {
            this.emails = emails;
            return this;
        }

        public Builder addresses(Set<Address> addresses) {
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
            contact.createdBy = this.createdBy;
            contact.createdTimestamp = this.createdTimestamp;
            contact.updatedBy = this.updatedBy;
            contact.updatedTimestamp = this.updatedTimestamp;
            contact.phones = new HashSet<>(this.phones);
            contact.emails = new HashSet<>(this.emails);
            contact.addresses = new HashSet<>(this.addresses);

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
