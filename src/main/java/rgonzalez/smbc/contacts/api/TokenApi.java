package rgonzalez.smbc.contacts.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rgonzalez.smbc.contacts.model.dto.TokenRequest;
import rgonzalez.smbc.contacts.model.dto.TokenResponse;

@Tag(name = "Token Management", description = "APIs for JWT token generation")
public interface TokenApi {

    @PostMapping
    @Operation(summary = "Generate JWT token", description = "Generates a JWT token based on the provided networkId that can be used for API authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request - networkId is required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request);
}
