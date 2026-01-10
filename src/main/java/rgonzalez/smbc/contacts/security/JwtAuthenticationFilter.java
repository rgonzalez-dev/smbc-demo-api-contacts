package rgonzalez.smbc.contacts.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Custom JWT authentication filter that:
 * 1. Extracts identity token from HTTP Authorization header
 * 2. Validates the token content
 * 3. Retrieves authorization list based on userid
 * 4. Sets the authentication in the security context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired(required = false)
    private JwtTokenProvider jwtTokenProvider;

    @Autowired(required = false)
    private UserAuthorizationService userAuthorizationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extract JWT token from Authorization header
            String jwt = extractTokenFromRequest(request);

            // If token exists and token provider is available, validate and process
            if (jwt != null && jwtTokenProvider != null) {

                // Validate JWT token
                if (jwtTokenProvider.validateToken(jwt)) {

                    // Extract userid from token
                    String userId = jwtTokenProvider.getUserIdFromToken(jwt);

                    // Retrieve authorization list based on userid
                    List<GrantedAuthority> authorities = Collections.emptyList();
                    if (userAuthorizationService != null && userId != null) {
                        authorities = userAuthorizationService.getAuthoritiesForUser(userId);
                    }

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId,
                            null, authorities);

                    // Set additional details from request
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from the Authorization header.
     * Expected format: "Authorization: Bearer {token}"
     *
     * @param request the HTTP request
     * @return the JWT token, or null if not found or invalid format
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
