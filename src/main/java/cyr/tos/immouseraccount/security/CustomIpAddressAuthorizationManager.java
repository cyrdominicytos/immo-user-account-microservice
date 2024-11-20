package cyr.tos.immouseraccount.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;
//@Component
public class CustomIpAddressAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {


    private final String allowedIp;
    private final int allowedPort;

    public CustomIpAddressAuthorizationManager(String allowedIp, int allowedPort) {
        this.allowedIp = allowedIp;
        this.allowedPort = allowedPort;
    }

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();

        // Récupérer l'adresse IP et le port de la requête
        String ip = request.getRemoteAddr();
        int port = request.getServerPort();


        // Vérifier si l'IP et le port correspondent
        boolean isAuthorized = allowedIp.equals(ip) && allowedPort == port;
        System.out.println("+++++++++++++>"+isAuthorized);
        return new AuthorizationDecision(isAuthorized);
    }
}
