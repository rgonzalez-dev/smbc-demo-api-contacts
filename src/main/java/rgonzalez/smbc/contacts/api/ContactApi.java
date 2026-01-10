package rgonzalez.smbc.contacts.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rgonzalez.smbc.contacts.model.Contact;

import java.util.List;

@Tag(name = "Contact Management", description = "APIs for managing contacts")
public interface ContactApi {

    @PostMapping
    @Operation(summary = "Create a new contact", description = "Creates a new contact with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Contact created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Contact> createContact(@RequestBody Contact contact);

    @GetMapping("/{id}")
    @Operation(summary = "Get contact by ID", description = "Retrieves a contact by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class))),
            @ApiResponse(responseCode = "404", description = "Contact not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Contact> getContactById(@PathVariable Long id);

    @GetMapping
    @Operation(summary = "Get all contacts", description = "Retrieves a list of all contacts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<List<Contact>> getAllContacts();

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing contact", description = "Updates a contact with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Contact.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Contact not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact", description = "Deletes a contact by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contact deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contact not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> deleteContact(@PathVariable Long id);

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if contact exists", description = "Checks if a contact exists by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact existence check completed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Boolean> contactExists(@PathVariable Long id);
}
