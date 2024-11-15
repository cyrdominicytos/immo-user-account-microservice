package cyr.tos.immouseraccount.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
//(prePostEnabled = true)
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
                                            return new CustomIpAddressAuthorizationManager("127.0.0.1", 8080).check(authentication, context);
                                        }
                                    }).anyRequest().authenticated();
                })
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
