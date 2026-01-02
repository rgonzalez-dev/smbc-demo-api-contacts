package rgonzalez.smbc.contacts.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error response DTO that matches the OpenAPI ErrorResponse schema
 * Used by ControllerAdvice to return consistent error responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Standard error response object")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2026-01-01T19:45:30", type = "string", format = "date-time")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400", type = "integer")
    private int status;

    @Schema(description = "Error type/category", example = "Bad Request", type = "string")
    private String error;

    @Schema(description = "Detailed error message", example = "Validation failed - see validationErrors for details", type = "string")
    private String message;

    @Schema(description = "API endpoint path where error occurred", example = "/api/v1/contacts", type = "string")
    private String path;

    @Schema(description = "Field-level validation errors (if applicable)", type = "array")
    private List<ValidationError> validationErrors;

    // Constructors
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path,
            List<ValidationError> validationErrors) {
        this(status, error, message, path);
        this.validationErrors = validationErrors;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", status=" + status +
                ", error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", validationErrors=" + validationErrors +
                '}';
    }

    /**
     * Represents a field-level validation error
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(name = "ValidationError", description = "Field-level validation error details")
    public static class ValidationError {

        @Schema(description = "Name of the field that failed validation", example = "firstName", type = "string")
        private String field;

        @Schema(description = "Validation error message for this field", example = "must not be empty", type = "string")
        private String message;

        // Constructors
        public ValidationError() {
        }

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        // Getters and Setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "ValidationError{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
