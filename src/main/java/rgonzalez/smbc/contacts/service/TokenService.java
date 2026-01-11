package rgonzalez.smbc.contacts.service;

/**
 * Service for generating JWT tokens.
 * Generates tokens based on networkId that can be validated by
 * JwtAuthenticationFilter.
 */
public interface TokenService {

    /**
     * Generates a JWT token for the given networkId.
     *
     * @param networkId the network ID to include in the token
     * @return a JWT token string
     */
    String generateToken(Long networkId);

    /**
     * Generates a JWT token for the given networkId with custom userId.
     *
     * @param networkId the network ID to include in the token
     * @param userId    the user ID to include in the token
     * @return a JWT token string
     */
    String generateToken(Long networkId, String userId);
}
