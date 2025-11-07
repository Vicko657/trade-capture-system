package com.technicalchallenge.config;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Config for Spring security.
 * 
 * Has a series of security filter chains that
 * process HTTP requests in order
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            HandlerMappingIntrospector handlerMappingIntrospector) throws Exception {

        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(handlerMappingIntrospector);
        // All users can access the login endpoint but other endpoints are authenticated
        http.csrf(AbstractHttpConfigurer::disable)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth.requestMatchers(mvc.pattern("/api/login")).permitAll()
                        .anyRequest().authenticated())
                .httpBasic(withDefaults());

        return http.build();

    }

    // Future: Will need to add secure crypto encoder for password -
    // BCryptPasswordEncoder for security
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Allows raw password
        return NoOpPasswordEncoder.getInstance();
    }

}
