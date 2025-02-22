package rendeleskezelo.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rendeleskezelo.model.Lamp;
import rendeleskezelo.model.LampAmount;
import rendeleskezelo.service.LampService;
import java.util.stream.Collectors;
@Slf4j
@Controller
public class LampController {
    private final LampService lampService;

    @Autowired
    public LampController(LampService lampService) {
        this.lampService = lampService;
    }


    @GetMapping("/lamp-list")
    public  String LampList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Csak a szerepkör nevét adja vissza
                .collect(Collectors.toList()));
        model.addAttribute("lamps", lampService.GetAllLamps());
        return "lamp-list";
    }
    @GetMapping("/add-new-lamp")
    public  String AddNewLampForm(Model model) {
        model.addAttribute("lamps", lampService.GetAllLamps());
        return "add-lamp-form";
    }
    @PostMapping("/add-new-lamp")
    public  String AddNewLamp(Model model, @ModelAttribute @Valid Lamp lamp, RedirectAttributes redirectAttributes) {
        lampService.AddLamp(lamp);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres lámpa hozzáadás");
        return "redirect:/lamp-list";
    }
    @GetMapping("/update-lamp/{id}")
    public  String UpdateForm(Model model, @PathVariable long id) {
        model.addAttribute("lamp", lampService.GetLampByID(id));
        return "update-lamp";
    }
    @PostMapping("/update-lamp")
    public  String UpdateLamp(Model model, @ModelAttribute  @Valid Lamp lamp, RedirectAttributes redirectAttributes) {
        lampService.UpdateLamp(lamp);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres lámpa frissítés");
        return "redirect:/lamp-list";
    }
    @GetMapping("/delete-lamp/{id}")
    public  String DeleteLamp(Model model,@PathVariable long id, RedirectAttributes redirectAttributes) {
        lampService.DeleteLamp(lampService.GetLampByID(id));
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres lámpa törlés");
        return "redirect:/lamp-list";
    }
    @GetMapping("/increase-lamp")
    public  String IncreaseForm(Model model) {
        model.addAttribute("lamps", lampService.GetAllLamps());
        return "increase-lamp";
    }
    @PostMapping("/increase-lamp")
    public  String IncreaseLamp(Model model, @ModelAttribute LampAmount lampAmount, RedirectAttributes redirectAttributes) {
      //  log.info(lampAmount.getLamp().toString()+" "+lampAmount.getAmount());
        lampService.UpdateLampSupply(lampAmount.getLamp(),(int)lampAmount.getAmount());
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres raktárkészlet hozzáadás");
        return "redirect:/lamp-list";
    }

}
