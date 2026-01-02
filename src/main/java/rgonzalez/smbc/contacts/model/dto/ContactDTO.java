package rgonzalez.smbc.contacts.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Contact Data Transfer Object containing contact information")
public interface ContactDTO {

    @Schema(description = "Unique identifier for the contact", example = "1")
    Long getId();

    @Schema(description = "Full name of the contact", example = "John Doe")
    String getName();

    @Schema(description = "User who created the contact", example = "admin")
    String getCreatedBy();

    @Schema(description = "ISO 8601 timestamp when contact was created", example = "2025-12-20T10:30:00")
    String getCreatedTimestamp();

    @Schema(description = "User who last updated the contact", example = "user123")
    String getUpdatedBy();

    @Schema(description = "ISO 8601 timestamp when contact was last updated", example = "2025-12-20T14:45:00")
    String getUpdatedTimestamp();
}
