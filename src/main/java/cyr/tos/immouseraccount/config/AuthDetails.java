package cyr.tos.immouseraccount.config;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthDetails {
    public static Long getAuthUserId(){
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
