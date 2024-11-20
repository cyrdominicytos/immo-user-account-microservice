package cyr.tos.immouseraccount.security;

import cyr.tos.immouseraccount.exception.CustomAccessDeniedHandler;
import cyr.tos.immouseraccount.exception.CustomAuthenticationEntryPointException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//(prePostEnabled = true)
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Autowired
    private  CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private CustomAuthenticationEntryPointException customAuthenticationEntryPointException;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(requests -> {
                        requests
                                .requestMatchers(HttpMethod.POST, "/user-account")
                                .access((authentication, context) -> {
                                    String origin = context.getRequest().getHeader("X-Requested-From");
                                    boolean isAllowedOrigin = origin != null && origin.equals("immo-authentification-server");

                                    if (!isAllowedOrigin) {
                                        System.out.println("======> Not Origin Allowed : " + origin);

                                        // Vérifiez si l'utilisateur est authentifié, sinon ajoutez une authentification factice
                                        //if (authentication == null || !authentication.is) {
                                            SecurityContextHolder.getContext().setAuthentication(
                                                    new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of())
                                            );
                                       // }

                                        throw new AccessDeniedException("Invalid origin. Access denied.");
                                    }
                                    return new AuthorizationDecision(true);

                                }).anyRequest().authenticated();
                    })
                    .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling(exception -> exception
                            .accessDeniedHandler(customAccessDeniedHandler)// Associe le handler pour 403
                            .authenticationEntryPoint(customAuthenticationEntryPointException) // Associe le handler pour 401
                    );
            return http.build();
        }

    @Bean
    public SecurityFilterChain securityFilterChainOld(HttpSecurity http) throws Exception {
        System.out.println("======>In User Account");
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(HttpMethod.POST, "/user-account/**")
                            .access((authentication, context) -> {
                                String origin = context.getRequest().getHeader("Origin");
                                System.out.println("======>Origin: " + origin);
                                // Si "Origin" est null, essayer "Referer"
                                if (origin == null) {
                                    origin = context.getRequest().getHeader("Referer");
                                    System.out.println("======>Referer: " + origin);
                                    System.out.println("======>Headers: " + Collections.list(context.getRequest().getHeaderNames()));
                                    origin = context.getRequest().getHeader("X-Requested-From");
                                    System.out.println("======>origin: " + origin);
                                    String ip = context.getRequest().getRemoteAddr();
                                    System.out.println("======> IP: " + ip);

                                }
                                boolean isAllowedOrigin = origin != null && origin.startsWith("http://immo-user-account:8080");
                                isAllowedOrigin = isAllowedOrigin || origin != null && origin.equals("immo-authentification-server");
                                // Si l'origine est valide, autoriser l'accès sans authentification
                                if (isAllowedOrigin) {
                                    System.out.println("======> Origin Allowed : " + origin);
                                    return new AuthorizationDecision(true);
                                } else {
                                    System.out.println("======> Not Origin Allowed : " + origin);

                                    return new AuthorizationDecision(false);
                                    //return new CustomIpAddressAuthorizationManager("127.0.0.1", 8080).check(authentication, context);
                                }
                            }).anyRequest().authenticated();
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
