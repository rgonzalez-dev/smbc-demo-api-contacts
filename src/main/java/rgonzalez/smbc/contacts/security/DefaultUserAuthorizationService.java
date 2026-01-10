package rgonzalez.smbc.contacts.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of UserAuthorizationService.
 * This is a stub implementation that returns empty authorities.
 * 
 * In production, implement a custom version that:
 * - Queries a user roles table/database
 * - Integrates with an external authorization service (LDAP, AD, OAuth
 * provider, etc.)
 * - Caches authorization data with appropriate TTL
 * 
 * Example custom implementation would look like:
 * 
 * <pre>
 * &#64;Service
 * public class DatabaseUserAuthorizationService implements UserAuthorizationService {
 *     &#64;Autowired
 *     private UserRoleRepository userRoleRepository;
 * 
 *     @Override
 *     public List<GrantedAuthority> getAuthoritiesForUser(String userId) {
 *         List<UserRole> roles = userRoleRepository.findByUserId(userId);
 *         return roles.stream()
 *                 .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
 *                 .collect(Collectors.toList());
 *     }
 * }
 * </pre>
 */
@Service
@ConditionalOnMissingBean(UserAuthorizationService.class)
public class DefaultUserAuthorizationService implements UserAuthorizationService {

    @Override
    public List<GrantedAuthority> getAuthoritiesForUser(String userId) {
        // TODO: Implement actual authorization retrieval from database or external
        // service
        // For now, return empty list
        return Collections.emptyList();
    }

    @Override
    public List<GrantedAuthority> getAuthoritiesForUser(String networkId, String userId) {
        // TODO: Implement actual authorization retrieval using network context
        // For now, return empty list
        return Collections.emptyList();
    }

    /**
     * Helper method to convert role names to Spring Security GrantedAuthority
     * objects.
     * Adds "ROLE_" prefix as Spring Security convention.
     *
     * @param roleNames the list of role names
     * @return list of GrantedAuthority objects
     */
    protected List<GrantedAuthority> convertRolesToAuthorities(List<String> roleNames) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String roleName : roleNames) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
        }
        return authorities;
    }
}
