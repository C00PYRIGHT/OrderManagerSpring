package rendeleskezelo.exception;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.nio.file.AccessDeniedException;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Model model, HttpServletResponse response) {
        BindingResult result = e.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuilder errorMessage = new StringBuilder("A következő mezők érvényesítése nem sikerült:\n");
        fieldErrors.forEach(fieldError -> errorMessage.append("* ").append(fieldError.getField()).append(" (").append(fieldError.getDefaultMessage()).append(")\n"));
        errorMessage.delete(errorMessage.length() - 1, errorMessage.length());

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorMessage", errorMessage.toString().replace("\n", "<br>"));
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.BAD_REQUEST.getReasonPhrase());
        log.error(errorMessage.toString());
        return "error_page";
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model, HttpServletResponse response) {
        String errorMessage = "Hiba történt: " + e.getMessage();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.BAD_REQUEST.getReasonPhrase());
        log.error("IllegalArgumentException: " + e.getMessage());
        return "error_page";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e, Model model, HttpServletResponse response) {
        String errorMessage = "A keresett könyv nem található: " + e.getMessage();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.NOT_FOUND.getReasonPhrase());
        log.error("BookNotFoundException: " + e.getMessage());
        return "error_page";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException e, Model model, HttpServletResponse response) {
        String errorMessage = "A hozzáadni kívánt könyv már szerepel a listán. " + e.getMessage();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.BAD_REQUEST.getReasonPhrase());
        log.error("DataIntegrityViolation: " + e.getMessage());
        return "error_page";
    }
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException e, Model model, HttpServletResponse response) {
        String errorMessage = "IllegalStateException: " + e.getMessage();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.BAD_REQUEST.getReasonPhrase());
        log.error("IllegalStateException: " + e.getMessage());
        return "error_page";
    }
    @ExceptionHandler(CustomAccessDeniedException.class)
    public String handleAccessDeniedException(CustomAccessDeniedException e, Model model, HttpServletResponse response) {
        String errorMessage = "Hozzáférés megtagadva  " + e.getMessage();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.FORBIDDEN.getReasonPhrase());
        log.error("Hozzáférés megtagadva: " + e.getMessage());
        return "error_page";
    }
    @ExceptionHandler(NotEnoughLampException.class)
    public String handleNotEnoughLampException(NotEnoughLampException e, Model model, HttpServletResponse response) {
        String errorMessage = "Elfogyott a lámpa:  " + e.getMessage();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.BAD_REQUEST.getReasonPhrase());
        log.error("Elfogyott a lámpa: " + e.getMessage());
        return "error_page";
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public String handleAuthDeniedException(AuthorizationDeniedException e, Model model, HttpServletResponse response) {
        String errorMessage = "Hozzáférés megtagadva  " + e.getMessage();
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.FORBIDDEN.getReasonPhrase());
        log.error("Hozzáférés megtagadva: " + e.getMessage());
        return "error_page";
    }
    @ExceptionHandler(EmailFailException.class)
    public String handleEmailfailException(EmailFailException e, Model model, HttpServletResponse response) {
        String errorMessage = "Email hiba:  " + e.getMessage();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        log.error("Email hiba: " + e.getMessage());
        return "error_page";
    }
    // Egyéb exception-ek kezelése
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model, HttpServletResponse response) {
        String errorMessage = "Ismeretlen hiba történt: " + e.getMessage();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("statusCode", response.getStatus());
        model.addAttribute("statusReason", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        log.error("Exception: " + e.getMessage(), e);
        return "error_page";
    }
}
