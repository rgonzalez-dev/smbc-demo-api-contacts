# Custom JWT Authentication Filter Setup Guide

## Overview

The custom JWT authentication filter automatically handles JWT token validation and user authentication within the Spring Security context. It performs three main tasks:

1. **Extract**: Retrieves the JWT token from the `Authorization` HTTP header (format: `Bearer {token}`)
2. **Validate**: Validates the JWT token structure and content
3. **Authorize**: Loads user authorities/roles and sets the authentication in the Security Context

## Architecture

### Components

#### 1. **JwtAuthenticationFilter** (`JwtAuthenticationFilter.java`)
- Main filter implementation extending `OncePerRequestFilter`
- Registered in Spring Security filter chain before `UsernamePasswordAuthenticationFilter`
- Extracts token from `Authorization: Bearer {token}` header
- Delegates validation to `JwtTokenProvider`
- Delegates authorization retrieval to `UserAuthorizationService`

#### 2. **JwtTokenProvider Interface** (`JwtTokenProvider.java`)
- Defines contract for JWT validation and claims extraction
- Default implementation: `DefaultJwtTokenProvider` (basic structure validation)
- Must implement:
  - `validateToken(String token)`: Validates JWT structure, signature, and expiration
  - `getUserIdFromToken(String token)`: Extracts user ID from token claims
  - `getUsernameFromToken(String token)`: Extracts username from token claims

#### 3. **UserAuthorizationService Interface** (`UserAuthorizationService.java`)
- Defines contract for loading user authorities/roles
- Default implementation: `DefaultUserAuthorizationService` (stub, returns empty list)
- Must implement:
  - `getAuthoritiesForUser(String userId)`: Returns list of user authorities
  - `getAuthoritiesForUser(String networkId, String userId)`: Returns authorities with network context

#### 4. **SecurityConfig** (`SecurityConfig.java`)
- Registers the JWT filter in the Spring Security filter chain
- Maintains existing authorization rules for public endpoints

## How It Works

```
HTTP Request with Authorization Header
         ↓
JwtAuthenticationFilter.doFilterInternal()
         ↓
Extract token from "Authorization: Bearer {token}"
         ↓
JwtTokenProvider.validateToken(token)
         ↓
If valid:
  - Extract userId: JwtTokenProvider.getUserIdFromToken()
  - Load authorities: UserAuthorizationService.getAuthoritiesForUser(userId)
  - Create UsernamePasswordAuthenticationToken with userId and authorities
  - Set in SecurityContextHolder
         ↓
Filter Chain continues
```

## Implementation Guide

### Option 1: Use Default Implementations (Development/Testing)

The default implementations are automatically provided and require no setup:

```bash
# Just start the application - defaults are activated automatically
mvn spring-boot:run
```

**Limitations:**
- `DefaultJwtTokenProvider`: Basic structure validation only (no signature or expiration checking)
- `DefaultUserAuthorizationService`: Returns empty authorities (no roles/permissions loaded)

### Option 2: Custom JWT Token Provider

Implement `JwtTokenProvider` with your JWT library (e.g., JJWT, Nimbus JOSE+JWT):

#### Add dependency to `pom.xml`:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

#### Create implementation:
```java
package rgonzalez.smbc.contacts.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JjwtTokenProvider implements JwtTokenProvider {

    @Value("${jwt.secret:your-secret-key-change-in-production}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private int jwtExpirationMs;

    @Override
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("sub", String.class);
    }

    @Override
    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("name", String.class);
    }

    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
```

#### Add to `application.properties`:
```properties
jwt.secret=your-very-long-secret-key-change-in-production-minimum-256-bits
jwt.expiration=86400000
```

### Option 3: Custom Authorization Service

Implement `UserAuthorizationService` to load authorities from database or LDAP:

#### Example: Database-backed implementation
```java
package rgonzalez.smbc.contacts.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseUserAuthorizationService extends DefaultUserAuthorizationService {

    @Autowired(required = false)
    private UserRoleRepository userRoleRepository;

    @Override
    public List<GrantedAuthority> getAuthoritiesForUser(String userId) {
        if (userRoleRepository == null) {
            return super.getAuthoritiesForUser(userId);
        }

        return userRoleRepository.findByUserId(userId)
            .stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<GrantedAuthority> getAuthoritiesForUser(String networkId, String userId) {
        if (userRoleRepository == null) {
            return super.getAuthoritiesForUser(networkId, userId);
        }

        return userRoleRepository.findByNetworkIdAndUserId(networkId, userId)
            .stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .collect(Collectors.toList());
    }
}
```

### Option 4: LDAP/Active Directory Integration

```java
@Configuration
public class LdapAuthorizationService extends DefaultUserAuthorizationService {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Override
    public List<GrantedAuthority> getAuthoritiesForUser(String userId) {
        // Query LDAP for user groups/roles
        // Convert to GrantedAuthority list
    }
}
```

## Usage Examples

### Request with JWT Token

```bash
# Extract token from your identity provider
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

# Make request with Authorization header
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/users
```

### Token Claims Expected

The JWT token should contain these standard claims:

```json
{
  "sub": "user123",                    // User ID (extracted as userId)
  "name": "John Doe",                  // Username
  "exp": 1234567890,                   // Expiration timestamp (seconds since epoch)
  "iat": 1234567800,                   // Issued at timestamp
  "custom_roles": ["ADMIN", "USER"]    // Custom claims for your app
}
```

## Security Context Access

After JWT authentication, retrieve authenticated user in controllers/services:

```java
@RestController
public class MyController {

    @GetMapping("/user/profile")
    public UserProfile getUserProfile() {
        // Get authenticated user from security context
        String userId = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        // Get authorities
        Collection<GrantedAuthority> authorities = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities();
        
        // Use getCurrentUsername() helper from BusinessActivityRecorder
        return userService.getUserProfile(userId);
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboard getAdminDashboard() {
        // Only accessible to ADMIN role
        return adminService.getDashboard();
    }
}
```

## Configuration

### Application Properties

```properties
# JWT Configuration (if using custom provider)
jwt.secret=your-secret-key-change-in-production
jwt.expiration=86400000

# Spring Security - Enable HTTP Basic for testing (optional)
spring.security.user.name=testuser
spring.security.user.password=testpass
```

### Exclude Endpoints from JWT Requirement

Update `SecurityConfig.java` to add more public endpoints:

```java
.requestMatchers(
    "/api/v1/public/**",
    "/health",
    "/swagger-ui/**",
    "/v3/api-docs/**"
).permitAll()
```

## Testing

### Test with Mock Token

```java
@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testValidJwtToken() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        
        mockMvc.perform(get("/api/v1/users")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    public void testInvalidJwtToken() throws Exception {
        mockMvc.perform(get("/api/v1/users")
            .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMissingAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isUnauthorized());
    }
}
```

## Troubleshooting

### Filter Not Applied

- Check that `JwtAuthenticationFilter` is registered as a `@Component`
- Verify `SecurityConfig` imports the filter
- Enable debug logging: `logging.level.org.springframework.security=DEBUG`

### Token Not Extracted

- Verify header format: `Authorization: Bearer {token}`
- Check for case sensitivity (header names are case-insensitive in HTTP)
- Enable filter-level logging to diagnose extraction issues

### Authorities Not Loaded

- Verify `UserAuthorizationService` is implemented
- Check database connectivity and user role mappings
- Enable query logging: `spring.jpa.show-sql=true`

### Token Validation Fails

- Verify JWT signature using correct secret key
- Check token expiration date
- Enable token decoding to inspect claims (use jwt.io for debugging)

## Security Best Practices

1. **Secret Key Management**
   - Store JWT secret in environment variables, not in code
   - Use strong secrets (256+ bits)
   - Rotate secrets periodically

2. **Token Expiration**
   - Set reasonable expiration times (e.g., 1-24 hours)
   - Implement token refresh mechanism for longer sessions
   - Always validate expiration in `JwtTokenProvider`

3. **HTTPS Only**
   - Always use HTTPS in production to prevent token interception
   - Set `Secure` flag on cookies if using session tokens

4. **Token Storage**
   - Never log or store tokens in plaintext
   - Use `HttpOnly` flag for token cookies

5. **CORS Configuration**
   - Restrict allowed origins when accepting cross-origin JWT requests
   - Implement proper CORS handling in `SecurityConfig`

## Related Components

- **BusinessActivityRecorder**: Uses `getCurrentUsername()` from SecurityContextHolder for audit trails
- **Traceable Embeddable**: Stores `createdBy` and `updatedBy` using authenticated user from security context
- **ServiceLoggingAspect**: Logs service method calls with authenticated user information

## Next Steps

1. Choose JWT implementation (JJWT recommended)
2. Create custom `JwtTokenProvider` implementation
3. Create custom `UserAuthorizationService` implementation
4. Configure JWT secret and expiration in properties
5. Test with real JWT tokens from your identity provider
6. Update SecurityConfig to restrict public endpoints as needed
