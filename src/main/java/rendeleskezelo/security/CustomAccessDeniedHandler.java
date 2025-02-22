package rendeleskezelo.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Hibák kezelése
        request.setAttribute("errorMessage", "Hozzáférés megtagadva: " + accessDeniedException.getMessage());
        request.setAttribute("statusCode", HttpStatus.FORBIDDEN.value());
        request.setAttribute("statusReason", HttpStatus.FORBIDDEN.getReasonPhrase());

        // Hibakezelés átirányítás
        request.getRequestDispatcher("/403").forward(request, response);
    }
}
