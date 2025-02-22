package rendeleskezelo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/403")
    public String error403(Model model) {
        return "403"; // A 403.html sablont fogja használni
    }
}
