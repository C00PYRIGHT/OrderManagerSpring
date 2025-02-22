package rendeleskezelo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rendeleskezelo.model.User;
import rendeleskezelo.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add-user")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // A regisztrációs űrlap
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return "redirect:/register?error";  // Ha a felhasználónév már létezik
        }
        userService.registerUser(user); // Felhasználó regisztrálása
        return "redirect:/user-list";  // Sikeres regisztráció után átirányítás a bejelentkezéshez
    }
    @GetMapping("/login")
    public String loginEndpoint() {
        return "login";
    }

    @GetMapping("/user-list")
    public  String UserList(Model model) {
        model.addAttribute("users",userService.getAllUsers());

        return "UserList";
    }
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/user-list";
    }
    @GetMapping("/update-user/{id}")
    public String updateUser(@PathVariable long id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        return "update-form";
    }
    @PostMapping("/update-user")
    public String updateUserPost(@ModelAttribute @Valid User user, RedirectAttributes redirectAttributes) {
        userService.updateUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        return "redirect:/user-list";
    }



}
