package rgonzalez.smbc.contacts.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Default JWT token generator that creates simple JWT tokens.
 * This is a basic implementation suitable for development and testing.
 * 
 * For production use, replace with JJWT or Nimbus JOSE+JWT library.
 * 
 * Token format: {header}.{payload}.{signature}
 * Where each part is base64url encoded JSON.
 */
@Service
public class DefaultTokenService implements TokenService {

    private static final String TOKEN_TYPE = "JWT";
    private static final String ALGORITHM = "HS256";

    @Value("${jwt.secret:your-secret-key-change-in-production}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Override
    public String generateToken(Long networkId) {
        return generateToken(networkId, "user-" + networkId);
    }

    @Override
    public String generateToken(Long networkId, String userId) {
        try {
            // Create header
            String header = createHeader();

            // Create payload with networkId and userId
            String payload = createPayload(networkId, userId);

            // Create signature (simplified - not cryptographically secure for production)
            String signature = createSignature(header, payload);

            // Combine all parts
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    private String createHeader() throws Exception {
        String header = "{\"alg\":\"" + ALGORITHM + "\",\"typ\":\"" + TOKEN_TYPE + "\"}";
        return base64UrlEncode(header);
    }

    private String createPayload(Long networkId, String userId) throws Exception {
        long now = System.currentTimeMillis();
        long expirationTime = now + jwtExpirationMs;

        String payload = "{" +
                "\"sub\":\"" + userId + "\"," +
                "\"networkId\":" + networkId + "," +
                "\"iat\":" + (now / 1000) + "," +
                "\"exp\":" + (expirationTime / 1000) +
                "}";

        return base64UrlEncode(payload);
    }

    private String createSignature(String header, String payload) throws Exception {
        // Simple signature creation - use HMAC-SHA256 in production
        String message = header + "." + payload;
        String signature = Integer.toHexString((message + jwtSecret).hashCode());
        return base64UrlEncode(signature);
    }

    private String base64UrlEncode(String input) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(input.getBytes());
    }
}
