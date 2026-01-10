package rgonzalez.smbc.contacts.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
 * Interface for retrieving user authorization (roles/permissions).
 * Implementations should load authorities from a database, directory service,
 * or other source.
 */
public interface UserAuthorizationService {

    /**
     * Retrieves the list of authorities/roles for a specific user.
     *
     * @param userId the user identifier
     * @return list of GrantedAuthority objects, or empty list if no authorities
     *         found
     */
    List<GrantedAuthority> getAuthoritiesForUser(String userId);

    /**
     * Retrieves the list of authorities/roles for a specific user by network ID.
     *
     * @param networkId the network/domain identifier
     * @param userId    the user identifier within the network
     * @return list of GrantedAuthority objects, or empty list if no authorities
     *         found
     */
    List<GrantedAuthority> getAuthoritiesForUser(String networkId, String userId);
}
