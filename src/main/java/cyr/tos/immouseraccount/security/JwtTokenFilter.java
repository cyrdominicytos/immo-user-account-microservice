package cyr.tos.immouseraccount.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author  Cyriaque TOSSOU
 * This class prefilter every incomming request to
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String CERTIFICATION_KEY="auth-server";
    private final KeyStore keyStore;

    public JwtTokenFilter(KeyStore keyStore) {
        this.keyStore = keyStore;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String path = request.getRequestURI();
       String method = request.getMethod();
       System.out.println("====== Path : " + path + " Method : " + method + " ======");
        // Exclure les requêtes POST sur /user-account/** de la validation JWT
        if (HttpMethod.POST.matches(method) && path.startsWith("/api/user-account")) {
            filterChain.doFilter(request, response); // Passer la requête sans validation JWT
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                PublicKey publicKey = keyStore.getCertificate(CERTIFICATION_KEY).getPublicKey();
                Claims claims = Jwts.parser().setSigningKey(publicKey)
                        .parseClaimsJws(token)
                        .getBody();

                List<String> roles = claims.get("role", List.class);
                List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                Long userId = claims.get("userId", Long.class);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired. Please login again.");
                return;
            } catch (io.jsonwebtoken.SignatureException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature. Authentication failed.");
                return;
            } catch (Exception e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token. Unable to authenticate.");
                return;
            }
        }else{
            System.out.println("======== Authorization header not found =========");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authorization Token not provided.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        String timestamp = java.time.ZonedDateTime.now().toString();
        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\"}",
                timestamp, status, message
        );
        response.getWriter().write(jsonResponse);
    }
}
