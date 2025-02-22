package rendeleskezelo.controller;

import jakarta.validation.Valid;
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
import rendeleskezelo.model.Customer;
import rendeleskezelo.repository.CustomerRepository;
import rendeleskezelo.service.CustomerService;
import rendeleskezelo.service.UserService;

import java.util.stream.Collectors;

@Controller
public class CustomerController {
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/customer-list")
    public  String CustomerList(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Csak a szerepkör nevét adja vissza
                .collect(Collectors.toList()));
        model.addAttribute("customers", customerService.GetAllCustomers());
        return "customer-list";
    }
    @GetMapping("/add-new-customer")
    public  String AddNewCustomerForm(Model model) {
        model.addAttribute("Customers", customerService.GetAllCustomers());
        return "add-customer-form";
    }
    @PostMapping("/add-new-customer")
    public  String AddNewCustomer(Model model, @ModelAttribute @Valid Customer customer, RedirectAttributes redirectAttributes) {
        customerService.AddCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres vásárló hozzáadás");
        return "redirect:/customer-list";
    }
    @GetMapping("/update-customer/{id}")
    public  String UpdateForm(Model model, @PathVariable long id) {
        model.addAttribute("customer", customerService.GetCustomerByID(id));
        return "update-customer";
    }
    @PostMapping("/update-customer")
    public  String UpdateCustomer(Model model, @ModelAttribute  @Valid Customer customer, RedirectAttributes redirectAttributes) {
        customerService.UpdateCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres vásárló frissítés");
        return "redirect:/customer-list";
    }
    @GetMapping("/delete-customer/{id}")
    public  String DeleteCustomer(Model model,@PathVariable long id, RedirectAttributes redirectAttributes) {
        customerService.DeleteCustomer(customerService.GetCustomerByID(id));
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres vásárló törlés");
        return "redirect:/customer-list";
    }
}
