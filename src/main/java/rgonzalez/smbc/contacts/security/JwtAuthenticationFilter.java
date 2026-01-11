package rgonzalez.smbc.contacts.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

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
    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    @Autowired(required = true)
    private JwtTokenProvider jwtTokenProvider;

    @Autowired(required = false)
    private UserAuthorizationService userAuthorizationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            logger.info("JwtAuthenticationFilter processing request: " + request.getRequestURI());

            // Extract JWT token from Authorization header
            String jwt = extractTokenFromRequest(request);

            if (jwt != null) {
                logger.info("JWT token found in Authorization header");

                // If token exists and token provider is available, validate and process
                if (jwtTokenProvider != null) {
                    logger.info("JwtTokenProvider is available, validating token");

                    // Validate JWT token
                    if (jwtTokenProvider.validateToken(jwt)) {
                        logger.info("JWT token is valid");

                        // Extract userid from token
                        String userId = jwtTokenProvider.getUserIdFromToken(jwt);
                        logger.info("Extracted userId from token: " + userId);

                        // Retrieve authorization list based on userid
                        List<GrantedAuthority> authorities = Collections.emptyList();
                        if (userAuthorizationService != null && userId != null) {
                            authorities = userAuthorizationService.getAuthoritiesForUser(userId);
                        }

                        // Create authentication token
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userId,
                                null, authorities);

                        // Set additional details from request
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // Set authentication in security context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authentication set in security context for userId: " + userId);
                    } else {
                        logger.warning("JWT token validation failed");
                    }
                } else {
                    logger.warning("JwtTokenProvider is not available");
                }
            } else {
                logger.info("No JWT token found in Authorization header");
            }
        } catch (Exception ex) {
            logger.severe("Could not set user authentication in security context: " + ex.getMessage());
            ex.printStackTrace();
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

        logger.info("Authorization header value: " + authHeader);

        // Log all headers for debugging
        Enumeration<String> headerNames = request.getHeaderNames();
        logger.info("All request headers:");
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            logger.info("  " + headerName + ": "
                    + (headerValue != null && headerValue.startsWith("Bearer ") ? "Bearer [token]" : headerValue));
        }

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
