package rgonzalez.smbc.contacts.model;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "business_events", schema = "contacts")
@EntityListeners(AuditingEntityListener.class)
public class BusinessEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String eventId;

    @Column(nullable = false, length = 100)
    private String aggregateId;

    @Column(nullable = false, length = 100)
    private String aggregateName;

    @Column(nullable = false, length = 100)
    private String eventName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventPayload;

    @Column(nullable = false, length = 500)
    private String schemaVersion;

    @Column(nullable = true, length = 100)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusinessEvent.EventDirection eventDirection;

    @Embedded
    private Traceable traceable = new Traceable();

    // Constructors
    public BusinessEvent() {
    }

    public BusinessEvent(String eventId, String aggregateId, String aggregateName,
            String eventName, String eventPayload, String schema, String correlationId,
            BusinessEvent.EventDirection eventDirection) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.aggregateName = aggregateName;
        this.eventName = eventName;
        this.eventPayload = eventPayload;
        this.schemaVersion = schema;
        this.correlationId = correlationId;
        this.eventDirection = eventDirection;
        this.traceable = new Traceable();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public void setAggregateName(String aggregateName) {
        this.aggregateName = aggregateName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schema) {
        this.schemaVersion = schema;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public BusinessEvent.EventDirection getEventDirection() {
        return eventDirection;
    }

    public void setEventDirection(BusinessEvent.EventDirection eventDirection) {
        this.eventDirection = eventDirection;
    }

    public Traceable getTraceable() {
        return traceable;
    }

    public void setTraceable(Traceable traceable) {
        this.traceable = traceable != null ? traceable : new Traceable();
    }

    @Override
    public String toString() {
        return "BusinessEvent{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateName='" + aggregateName + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                ", schema='" + schemaVersion + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", eventDirection='" + eventDirection + '\'' +
                ", traceable=" + traceable +
                '}';
    }

    public enum EventDirection {
        OUTBOUND("outbound"),
        INBOUND("inbound");

        private final String displayName;

        EventDirection(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
