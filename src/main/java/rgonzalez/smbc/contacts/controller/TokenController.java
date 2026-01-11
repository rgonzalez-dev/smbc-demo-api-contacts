package rgonzalez.smbc.contacts.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.converter.HttpMessageNotReadableException;
import rgonzalez.smbc.contacts.api.TokenApi;
import rgonzalez.smbc.contacts.model.dto.TokenRequest;
import rgonzalez.smbc.contacts.model.dto.TokenResponse;
import rgonzalez.smbc.contacts.service.TokenService;

/**
 * REST controller for token generation.
 * Provides endpoints to obtain JWT tokens for authentication.
 * Generated tokens can be used in the Authorization header for subsequent API
 * requests.
 */
@RestController
@RequestMapping("/api/v1/token")
public class TokenController implements TokenApi {

    private final TokenService tokenService;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request) {
        // Validate networkId is provided
        if (request.getNetworkId() == null || request.getNetworkId() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Generate token using the TokenService
            String token;
            if (request.getUserId() != null && !request.getUserId().isBlank()) {
                token = tokenService.generateToken(request.getNetworkId(), request.getUserId());
            } else {
                token = tokenService.generateToken(request.getNetworkId());
            }

            // Create response with token and expiration info
            TokenResponse response = new TokenResponse(token, "Bearer", jwtExpirationMs / 1000);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exception handler for invalid JSON requests.
     * Provides clear error message when request body is malformed.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body("Invalid request format. Expected JSON object with 'networkId' field. "
                        + "Example: {\"networkId\": 1} or {\"networkId\": 1, \"userId\": \"username\"}");
    }
}
