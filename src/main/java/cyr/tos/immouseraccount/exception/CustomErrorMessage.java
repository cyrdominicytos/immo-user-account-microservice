package cyr.tos.immouseraccount.exception;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomErrorMessage {

    public static HttpServletResponse sendMessage(HttpServletResponse response, String message ) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        String timestamp = java.time.ZonedDateTime.now().toString();
        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\"}",
                timestamp, HttpServletResponse.SC_FORBIDDEN, message
        );
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        return response;
    }
}
