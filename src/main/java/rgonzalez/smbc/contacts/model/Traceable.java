package rgonzalez.smbc.contacts.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Traceable embeddable class that contains audit trail information for
 * entities.
 * This class tracks who created/modified an entity and when those actions
 * occurred.
 * 
 * Used as an embedded component in JPA entities to separate audit concerns from
 * business logic fields.
 */
@Embeddable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Traceable {

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

    // Constructors
    public Traceable() {
    }

    public Traceable(String createdBy, LocalDateTime createdTimestamp,
            String updatedBy, LocalDateTime updatedTimestamp) {
        this.createdBy = createdBy;
        this.createdTimestamp = createdTimestamp;
        this.updatedBy = updatedBy;
        this.updatedTimestamp = updatedTimestamp;
    }

    /**
     * Copy constructor for creating an immutable copy of Traceable
     */
    public Traceable(Traceable other) {
        if (other != null) {
            this.createdBy = other.createdBy;
            this.createdTimestamp = other.createdTimestamp;
            this.updatedBy = other.updatedBy;
            this.updatedTimestamp = other.updatedTimestamp;
        }
    }

    // Getters and Setters
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

    /**
     * Creates an immutable copy of this Traceable instance
     */
    public Traceable toImmutableCopy() {
        return new Traceable(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Traceable traceable = (Traceable) o;
        return Objects.equals(createdBy, traceable.createdBy) &&
                Objects.equals(createdTimestamp, traceable.createdTimestamp) &&
                Objects.equals(updatedBy, traceable.updatedBy) &&
                Objects.equals(updatedTimestamp, traceable.updatedTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdBy, createdTimestamp, updatedBy, updatedTimestamp);
    }

    @Override
    public String toString() {
        return "Traceable{" +
                "createdBy='" + createdBy + '\'' +
                ", createdTimestamp=" + createdTimestamp +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedTimestamp=" + updatedTimestamp +
                '}';
    }
}
