package rgonzalez.smbc.contacts.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * Default implementation of JwtTokenProvider.
 * This is a basic implementation that validates token structure.
 * For production use, replace with a proper JWT library implementation (e.g.,
 * jjwt, nimbus-jose-jwt).
 *
 * Token format expected: {header}.{payload}.{signature}
 * Claims expected in payload (JSON): { "sub": "userid", "name": "username",
 * "exp": timestamp, ... }
 */
@Component
// @ConditionalOnMissingBean(JwtTokenProvider.class)
public class DefaultJwtTokenProvider implements JwtTokenProvider {

    @Override
    public boolean validateToken(String token) {
        try {
            // Basic validation: check token structure (3 parts separated by dots)
            String[] tokenParts = token.split("\\.");
            if (tokenParts.length != 3) {
                return false;
            }

            // Decode payload to verify it's valid Base64
            String payload = tokenParts[1];
            byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);

            // TODO: In production, verify JWT signature using a secret key or public key
            // TODO: Check token expiration (exp claim)

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getUserIdFromToken(String token) {
        try {
            String payload = extractPayload(token);
            // Extract "sub" claim (subject, typically the user ID)
            return extractClaimValue(payload, "sub");
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            String payload = extractPayload(token);
            // Extract "name" claim
            return extractClaimValue(payload, "name");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Extracts and decodes the payload from a JWT token.
     *
     * @param token the JWT token
     * @return decoded payload as a string
     */
    private String extractPayload(String token) {
        String[] tokenParts = token.split("\\.");
        if (tokenParts.length < 2) {
            return null;
        }
        byte[] decodedPayload = Base64.getUrlDecoder().decode(tokenParts[1]);
        return new String(decodedPayload);
    }

    /**
     * Extracts a specific claim value from the JWT payload.
     * This is a simple string parsing approach. For production, use a JSON parser.
     *
     * @param payload   the decoded JWT payload (JSON string)
     * @param claimName the claim name to extract
     * @return the claim value, or null if not found
     */
    private String extractClaimValue(String payload, String claimName) {
        if (payload == null) {
            return null;
        }

        // Simple regex-based extraction: look for "claimName":"value"
        String pattern = "\"" + claimName + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(payload);

        if (m.find()) {
            return m.group(1);
        }

        return null;
    }
}
