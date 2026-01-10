package rgonzalez.smbc.contacts.security;

/**
 * Interface for JWT token validation and extraction.
 * Implementations should handle JWT parsing, validation, and claims extraction.
 */
public interface JwtTokenProvider {

    /**
     * Validates the JWT token.
     *
     * @param token the JWT token to validate
     * @return true if token is valid (signature, expiration, etc.), false otherwise
     */
    boolean validateToken(String token);

    /**
     * Extracts the userid from the JWT token claims.
     *
     * @param token the JWT token
     * @return the userid from the token, or null if not found
     */
    String getUserIdFromToken(String token);

    /**
     * Extracts the username from the JWT token claims.
     *
     * @param token the JWT token
     * @return the username from the token, or null if not found
     */
    String getUsernameFromToken(String token);
}
