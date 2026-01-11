package rgonzalez.smbc.contacts.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired(required = false)
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                                // Allow public access to Swagger UI
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/v3/api-docs/**",
                                                                "/v3/api-docs.yaml")
                                                .permitAll()
                                                // Allow public access to actuator health endpoint
                                                .requestMatchers("/actuator/health").permitAll()
                                                // Allow public access to token generation endpoint
                                                .requestMatchers("/api/v1/token/**").permitAll()
                                                // Allow temp public access to contact management endpoints
                                                // .requestMatchers("/api/v1/contacts/**").permitAll()
                                                // All other requests require authentication
                                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf.disable());

                // Add JWT authentication filter before the default username/password filter
                if (jwtAuthenticationFilter != null) {
                        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                }

                return http.build();
        }
}
