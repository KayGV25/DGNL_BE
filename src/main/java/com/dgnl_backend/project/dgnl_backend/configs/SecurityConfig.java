package com.dgnl_backend.project.dgnl_backend.configs;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.dgnl_backend.project.dgnl_backend.security.AuthFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthFilter authFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf((csrf)->{csrf.disable();})
            .sessionManagement((sm)->{
                    sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
            .headers(
                headers ->
                headers.xssProtection(
                        xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                ).contentSecurityPolicy(
                        cps -> cps.policyDirectives("script-src 'self'")
                )
            )
            .cors(corsCustomizer->corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
                        CorsConfiguration corsConfiguration=new CorsConfiguration();
                        corsConfiguration.setAllowCredentials(true);// allows taking authentication with credentials (e.g Cookies, Header)
                        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                      // providing the allowed origin details, can provide multiple origins here, 5173 is the port number of client application here
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));// allowing all HTTP methods GET,POST,PUT etc, can configure on your need
                        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));// allowing all the request headers, can configure according to your need, which headers to allow
                        corsConfiguration.setMaxAge(Duration.ofMinutes(5L)); // setting the max time till which the allowed origin will not make a pre-flight request again to check if the CORS is allowed on not
                        return corsConfiguration;
                    }
                }))
            .authorizeHttpRequests(rq->
                rq
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/teacher/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/auth/**").authenticated()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().denyAll()
            )
            .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
