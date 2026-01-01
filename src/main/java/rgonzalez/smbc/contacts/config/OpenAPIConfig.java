package rgonzalez.smbc.contacts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * OpenAPI Configuration for Contact Management API
 * Defines API documentation programmatically using OpenAPI bean
 * This approach keeps the controller clean without any Swagger annotations
 */
@Configuration
public class OpenAPIConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenAPIConfig.class);

    /**
     * Creates and configures the OpenAPI specification bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Contact Management API")
                        .version("1.0.0")
                        .description(
                                "Comprehensive API for managing contact information with support for multiple communication channels")
                        .contact(new Contact()
                                .name("API Support Team")
                                .email("support@rgonzalez.smbc")
                                .url("https://github.com/yourusername/contacts-api")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://contacts-api-demo.azurewebsites.net")
                                .description("Production Server - Azure Web App")))
                .tags(Arrays.asList(
                        new Tag()
                                .name("Contact Management")
                                .description("Operations for managing contacts")));

        // Initialize components and add error response schema
        Components components = new Components();
        components.addSchemas("ErrorResponse", createErrorSchema());
        openAPI.setComponents(components);

        return openAPI;
    }

    /**
     * Adds a generic 400 Bad Request response to all endpoints
     * This customizer applies the ErrorResponse schema to all operations
     */
    @Bean
    public OpenApiCustomizer addCommon400Response() {
        return openAPI -> {
            logger.info("Adding common 400 responses to all endpoints");

            // Ensure components exist
            if (openAPI.getComponents() == null) {
                openAPI.setComponents(new Components());
            }

            // Ensure ErrorResponse schema is in components
            if (openAPI.getComponents().getSchemas() == null ||
                    !openAPI.getComponents().getSchemas().containsKey("ErrorResponse")) {
                logger.warn("ErrorResponse schema not found in components, adding it now");
                openAPI.getComponents().addSchemas("ErrorResponse", createErrorSchema());
            }

            // Only proceed if paths exist
            if (openAPI.getPaths() == null || openAPI.getPaths().isEmpty()) {
                logger.warn("No paths found in OpenAPI spec");
                return;
            }

            logger.info("Found {} paths in OpenAPI spec", openAPI.getPaths().size());

            // Apply 400 response to all paths and operations
            openAPI.getPaths().values().forEach(pathItem -> {
                // POST operations
                if (pathItem.getPost() != null && pathItem.getPost().getResponses() != null) {
                    pathItem.getPost().getResponses().addApiResponse("400", new ApiResponse()
                            .description("Bad Request - Validation or input error")
                            .content(new Content()
                                    .addMediaType("application/json",
                                            new MediaType()
                                                    .schema(new Schema<>()
                                                            .$ref("#/components/schemas/ErrorResponse")))));
                    logger.debug("Added 400 response to POST operation");
                }
                // PUT operations
                if (pathItem.getPut() != null && pathItem.getPut().getResponses() != null) {
                    pathItem.getPut().getResponses().addApiResponse("400", new ApiResponse()
                            .description("Bad Request - Validation or input error")
                            .content(new Content()
                                    .addMediaType("application/json",
                                            new MediaType()
                                                    .schema(new Schema<>()
                                                            .$ref("#/components/schemas/ErrorResponse")))));
                    logger.debug("Added 400 response to PUT operation");
                }
                // DELETE operations
                if (pathItem.getDelete() != null && pathItem.getDelete().getResponses() != null) {
                    pathItem.getDelete().getResponses().addApiResponse("400", new ApiResponse()
                            .description("Bad Request - Validation or input error")
                            .content(new Content()
                                    .addMediaType("application/json",
                                            new MediaType()
                                                    .schema(new Schema<>()
                                                            .$ref("#/components/schemas/ErrorResponse")))));
                    logger.debug("Added 400 response to DELETE operation");
                }
                // PATCH operations
                if (pathItem.getPatch() != null && pathItem.getPatch().getResponses() != null) {
                    pathItem.getPatch().getResponses().addApiResponse("400", new ApiResponse()
                            .description("Bad Request - Validation or input error")
                            .content(new Content()
                                    .addMediaType("application/json",
                                            new MediaType()
                                                    .schema(new Schema<>()
                                                            .$ref("#/components/schemas/ErrorResponse")))));
                    logger.debug("Added 400 response to PATCH operation");
                }
            });

            logger.info("Successfully added 400 responses to all applicable endpoints");
        };
    }

    /**
     * Creates a reusable error response schema for 400 Bad Request errors
     */
    private Schema<Object> createErrorSchema() {
        Schema<Object> errorSchema = new Schema<>();
        errorSchema.setType("object");
        errorSchema.setDescription("Standard error response object");

        errorSchema.addProperty("timestamp", new Schema<>()
                .type("string")
                .format("date-time")
                .description("Timestamp when the error occurred")
                .example("2026-01-01T12:00:00Z"));

        errorSchema.addProperty("status", new Schema<>()
                .type("integer")
                .format("int32")
                .description("HTTP status code")
                .example(400));

        errorSchema.addProperty("error", new Schema<>()
                .type("string")
                .description("Error type/category")
                .example("Bad Request"));

        errorSchema.addProperty("message", new Schema<>()
                .type("string")
                .description("Detailed error message")
                .example("Invalid input: firstName cannot be empty"));

        errorSchema.addProperty("path", new Schema<>()
                .type("string")
                .description("API endpoint path where error occurred")
                .example("/api/v1/contacts"));

        Schema<Object> validationErrorSchema = new Schema<>();
        validationErrorSchema.setType("object");
        validationErrorSchema.addProperty("field", new Schema<>()
                .type("string")
                .example("firstName"));
        validationErrorSchema.addProperty("message", new Schema<>()
                .type("string")
                .example("must not be empty"));

        Schema<Object> validationErrorsArray = new Schema<>();
        validationErrorsArray.setType("array");
        validationErrorsArray.setItems(validationErrorSchema);
        validationErrorsArray.setDescription("Field-level validation errors (if applicable)");

        errorSchema.addProperty("validationErrors", validationErrorsArray);

        errorSchema.setRequired(Arrays.asList("timestamp", "status", "error", "message"));

        return errorSchema;
    }
}
