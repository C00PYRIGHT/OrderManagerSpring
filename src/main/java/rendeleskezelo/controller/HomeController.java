package rendeleskezelo.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import rendeleskezelo.exception.EmailFailException;
import rendeleskezelo.model.LitophaneOrder;
import rendeleskezelo.service.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class HomeController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final LampService lampService;
    private final StatusService statusService;
    private final EmailService emailService;
    private final WebhookService webhookService;



    public HomeController(OrderService orderService, CustomerService customerService, LampService lampService, StatusService statusService, EmailService emailService, WebhookService webhookService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.lampService = lampService;
        this.statusService = statusService;
        this.emailService = emailService;
        this.webhookService = webhookService;
    }


    @GetMapping("/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // A felhasználó szerepköreinek lekérése
        model.addAttribute("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Csak a szerepkör nevét adja vissza
                .collect(Collectors.toList()));

        // Rendelések lekérése
        List<LitophaneOrder> litophaneOrders = orderService.getAllOrders(); // Az OrderService a rendeléseket kezeli
        model.addAttribute("lampalerts",lampService.lampsWithSupplyZeroOrLess());
        model.addAttribute("deadlines",orderService.findOrdersWithDeadlineWithinAWeek());
        model.addAttribute("orders", litophaneOrders);
        model.addAttribute("intake", orderService.sumPrice());
        model.addAttribute("outtake",orderService.sumOut());
        model.addAttribute("profit", orderService.sumProfit());

        return "home"; // A home.html sablon visszaadása
    }
    @GetMapping("/")
    public String base(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // A felhasználó szerepköreinek lekérése

        model.addAttribute("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Csak a szerepkör nevét adja vissza
                .collect(Collectors.toList()));

        // Rendelések lekérése
        List<LitophaneOrder> litophaneOrders = orderService.getAllOrders(); // Az OrderService a rendeléseket kezeli
        model.addAttribute("lampalerts",lampService.lampsWithSupplyZeroOrLess());
        model.addAttribute("deadlines",orderService.findOrdersWithDeadlineWithinAWeek());
        model.addAttribute("orders", litophaneOrders); // A rendeléseket hozzáadjuk a modelhez
        model.addAttribute("intake", orderService.sumPrice());
        model.addAttribute("outtake",orderService.sumOut());
        model.addAttribute("profit", orderService.sumProfit());

        return "home"; // A home.html sablon visszaadása
    }

    @GetMapping("/order-add")
    public String showOrderForm(Model model) {
        model.addAttribute("customers", customerService.GetAllCustomers());
        model.addAttribute("lamps", lampService.GetAllLamps());
        model.addAttribute("statuses", statusService.getAllStatus());
        return "add-order";
    }

    @PostMapping("/order-add")
    public String addOrder(@ModelAttribute @Valid LitophaneOrder order, RedirectAttributes redirectAttributes) {
        // Rendelés mentése
        orderService.addOrder(order);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres rendelés hozzáadás");

        return "redirect:/"; // Például rendelés lista oldalra irányítunk
    }
    @PostMapping("/order-add-email")
    public String addOrderEmail(@ModelAttribute @Valid LitophaneOrder order, RedirectAttributes redirectAttributes) {
        // Rendelés mentése
        orderService.addOrder(order);
        String subject = "Értesítés a rendelés létrehozásáról";
        String htmlContent = "";
        try {
            if (order.getStatus().getName().equals("Képekre vár") && order.getLamp().getId() == 2) {
                htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Létrehozás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben létrehozta a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <hr>
                                    <p>A rendszer úgy érzékelte, hogy Ön még nem küldött képeket a gyártáshoz. Ha segítségre van szüksége a kép kiválasztásához és feltöltéséhez, hasznos információkat talál az alábbi oldalon:</p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kepek.html">Bővebben a képekről</a>
                                    <p> </p>
                                    <hr>
                                        <p>A rendszer úgy érzékelte, hogy Ön még nem választott lámpatestet. Lámpa alapokat az alábbi linken választhat:</p>
                                        <a class="gomb" href="https://rendelesek.bencedaniel.hu/lampak.html">Választható lámpák</a>
                                        <p>Amennyiben kiválasztotta az Önnek legjobban tetsző lámpatestet, kérjük, jelezze felénk az alábbi e-mail címen:</p>
                                        <a class="gomb" href="mailto:info@bencedaniel.hu?subject={} kiválasztott">info@bencedaniel.hu</a>
                                        <p> </p>
                                    <hr>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/teljesemailletrehoz.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());
            } else if (order.getStatus().getName().equals("Képekre vár")) {
                htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Létrehozás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben létrehozta a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <hr>
                                    <p>A rendszer úgy érzékelte, hogy Ön még nem küldött képeket a gyártáshoz. Ha segítségre van szüksége a kép kiválasztásához és feltöltéséhez, hasznos információkat talál az alábbi oldalon:</p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kepek.html">Bővebben a képekről</a>
                                    <p> </p>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/kephianyemailletrehoz.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());

            } else if (order.getLamp().getId() == 2) {
                htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Módosítás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben létrehozta a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <hr>
                                        <p>A rendszer úgy érzékelte, hogy Ön még nem választott lámpatestet. Lámpa alapokat az alábbi linken választhat:</p>
                                        <a class="gomb" href="https://rendelesek.bencedaniel.hu/lampak.html">Választható lámpák</a>
                                        <p>Amennyiben kiválasztotta az Önnek legjobban tetsző lámpatestet, kérjük, jelezze felénk az alábbi e-mail címen:</p>
                                        <a class="gomb" href="mailto:info@bencedaniel.hu?subject={} kiválasztott">info@bencedaniel.hu</a>
                                        <p> </p>
                                    <hr>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/lampahianyemailletrehoz.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());
            }else if (order.getStatus().getName().equals("Nyomtatás alatt")){
                webhookService.sendPostRequest(order.getCustomer().getCustomerId()+"|"+order.getId()+"|"+order.getCustomer().getEmail());
                htmlContent = String.format("""
                    <html>
<head>
    <meta charset="UTF-8">
    <title>Rendelés létrehozás Értesítés</title>
    <style>
        .center {{
            font-family: Arial, sans-serif;
            justify-content: center;
            align-content: center;
            text-align: center;
            border-radius: 10px;
            max-width: 800px;
            margin: 30px auto;
        }}
        .photo {{
            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
            background-size: cover;
        }}
        .header, .footer {{
            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
            color: #fff;
            align-items: center;
            justify-self: center;
            text-align: center;
            padding: 10px;
            margin-top: 10px;
        }}
        .gomb {{
            background: linear-gradient(135deg, #ffd900, #8b4513);
            color: #fff;
            align-items: center;
            justify-self: center;
            text-align: center;
            padding: 10px;
            border-radius: 10px;
            margin: 30px auto;
            margin-top: 10px;
            margin-bottom: 10px;
            display: inline-block;
        }}
        .container {{
            max-width: 800px;
            margin: 30px auto;
            border-radius: 10px;
        }}
        .egyedi {{
            background-color: #444;
            margin-top: 20px;
            margin-bottom: 20px;
            margin-left: 70px;
            margin-right: 70px;
            padding: 20px;
        }}
        h1 {{
            color: #ffffff;
        }}
        p {{
            color: #ffffff;
            line-height: 1.4;
        }}
        a {{
            color: #58a6ff;
            text-decoration: none;
        }}
        a:hover {{
            text-decoration: underline;
        }}
        .signature {{
            margin-top: 20px;
            color: #bba2a2;
        }}
        strong {{
            color: #fff;
            font-weight: bold;
        }}
        .footer a {{
            color: #fff;
        }}
    </style>
</head>
<body>
    <div class="center">
        <div class="photo container">
            <div class="header container">
                <h1>3D lámpa értesítés</h1>
            </div>
            <div class="egyedi container">
                <p>Kedves <strong>%s</strong>,</p>
                <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben létrehozta a rendelését!</p>
                <p>A részleteket a rendeléskövetőben tudja megtekinteni:</p>
                <a class="gomb" href="https://3dlampa.bencedaniel.hu/kovetes.html?azonosito=%s">Részletek megnyitása</a>
                <p>Az ön azonosító száma: %s.</p>
                <hr>
                <p>Az alábbi gombra kattintva lehetősége van nyomon követni a nyomtatás aktuális állapotát és előrehaladottságát: </p>
                <a class="gomb" href="https://rendelesek.bencedaniel.hu/nowprinting.html">Bővebben a nyomtatásomról</a>
                <p> </p>
                <hr>
                <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
            </div>
            <div class="footer container">
                <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/nowprintemailletrehoz.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
            </div>
        </div>
    </div>
</body>
</html>

                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());
            }else {
                htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés létrehozás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben létrehozta a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/mindenjoemailletrehoz.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());

            }
            emailService.sendHtmlEmail(order.getCustomer().getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            throw new EmailFailException(e.getMessage());
        }
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres rendelés hozzáadás, Sikeres email küldés");

        return "redirect:/"; // Például rendelés lista oldalra irányítunk
    }
    @GetMapping("/delete-order/{id}")
    public String deleteOrder(@PathVariable long id, RedirectAttributes redirectAttributes) {
        // Rendelés mentése
        orderService.deleteOrder(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres rendelés törlés");

        return "redirect:/"; // Például rendelés lista oldalra irányítunk
    }
    @GetMapping("/update-order/{id}")
    public String updateOrderForm(@PathVariable long id, RedirectAttributes redirectAttributes,Model model) {
        // Rendelés mentése

        model.addAttribute("customers", customerService.GetAllCustomers());
        model.addAttribute("lamps", lampService.GetAllLamps());
        model.addAttribute("statuses", statusService.getAllStatus());
        model.addAttribute("order", orderService.findOrderById(id));

        return "update-order"; // Például rendelés lista oldalra irányítunk
    }
    @PostMapping("/order-update")
    public String updateOrder(@ModelAttribute @Valid LitophaneOrder order, RedirectAttributes redirectAttributes,Model model) {

        orderService.updateOrder(order);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres rendelés módosítás");

        return "redirect:/"; // Például rendelés lista oldalra irányítunk
    }
    @PostMapping("/order-update-email")
    public String updateOrderEmail(@ModelAttribute @Valid LitophaneOrder order, RedirectAttributes redirectAttributes, Model model) {
        log.info("lefut");
        orderService.updateOrder(order);
        redirectAttributes.addFlashAttribute("successMessage", "Sikeres rendelés módosítás, Email küldés");
            String subject = "Értesítés a rendelés módosításáról";
            String htmlContent = "";
        try {
        if (order.getStatus().getName().equals("Képekre vár") && order.getLamp().getId() == 2) {
            htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Módosítás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben módosította a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <hr>
                                    <p>A rendszer úgy érzékelte, hogy Ön még nem küldött képeket a gyártáshoz. Ha segítségre van szüksége a kép kiválasztásához és feltöltéséhez, hasznos információkat talál az alábbi oldalon:</p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kepek.html">Bővebben a képekről</a>
                                    <p> </p>
                                    <hr>
                                        <p>A rendszer úgy érzékelte, hogy Ön még nem választott lámpatestet. Lámpa alapokat az alábbi linken választhat:</p>
                                        <a class="gomb" href="https://rendelesek.bencedaniel.hu/lampak.html">Választható lámpák</a>
                                        <p>Amennyiben kiválasztotta az Önnek legjobban tetsző lámpatestet, kérjük, jelezze felénk az alábbi e-mail címen:</p>
                                        <a class="gomb" href="mailto:info@bencedaniel.hu?subject={} kiválasztott">info@bencedaniel.hu</a>
                                        <p> </p>
                                    <hr>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/teljesemailmodositas.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());
        } else if (order.getStatus().getName().equals("Képekre vár")) {
            htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Módosítás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben módosította a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <hr>
                                    <p>A rendszer úgy érzékelte, hogy Ön még nem küldött képeket a gyártáshoz. Ha segítségre van szüksége a kép kiválasztásához és feltöltéséhez, hasznos információkat talál az alábbi oldalon:</p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kepek.html">Bővebben a képekről</a>
                                    <p> </p>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/kephianyemailmodositas.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());

        } else if (order.getLamp().getId() == 2) {
             htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Módosítás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben módosította a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <hr>
                                        <p>A rendszer úgy érzékelte, hogy Ön még nem választott lámpatestet. Lámpa alapokat az alábbi linken választhat:</p>
                                        <a class="gomb" href="https://rendelesek.bencedaniel.hu/lampak.html">Választható lámpák</a>
                                        <p>Amennyiben kiválasztotta az Önnek legjobban tetsző lámpatestet, kérjük, jelezze felénk az alábbi e-mail címen:</p>
                                        <a class="gomb" href="mailto:info@bencedaniel.hu?subject={} kiválasztott">info@bencedaniel.hu</a>
                                        <p> </p>
                                    <hr>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/lampahianyemailmodositas.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());
        }else if (order.getStatus().getName().equals("Nyomtatás alatt")){
            log.info("lefutawebhookcall");
            webhookService.sendPostRequest(order.getCustomer().getCustomerId()+"|"+order.getId()+"|"+order.getCustomer().getEmail());
             htmlContent = String.format("""
                    <html>
<head>
    <meta charset="UTF-8">
    <title>Rendelés Módosítás Értesítés</title>
    <style>
        .center {{
            font-family: Arial, sans-serif;
            justify-content: center;
            align-content: center;  
            text-align: center;
            border-radius: 10px;
            max-width: 800px;
            margin: 30px auto;
        }}
        .photo {{
            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
            background-size: cover;
        }}
        .header, .footer {{
            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
            color: #fff;
            align-items: center;
            justify-self: center;
            text-align: center;
            padding: 10px;
            margin-top: 10px;
        }}
        .gomb {{
            background: linear-gradient(135deg, #ffd900, #8b4513);
            color: #fff;
            align-items: center;
            justify-self: center;
            text-align: center;
            padding: 10px;
            border-radius: 10px;
            margin: 30px auto;
            margin-top: 10px;
            margin-bottom: 10px;
            display: inline-block;
        }}
        .container {{
            max-width: 800px;
            margin: 30px auto;
            border-radius: 10px;
        }}
        .egyedi {{
            background-color: #444;
            margin-top: 20px;
            margin-bottom: 20px;
            margin-left: 70px;
            margin-right: 70px;
            padding: 20px;
        }}
        h1 {{
            color: #ffffff;
        }}
        p {{
            color: #ffffff;
            line-height: 1.4;
        }}
        a {{
            color: #58a6ff;
            text-decoration: none;
        }}
        a:hover {{
            text-decoration: underline;
        }}
        .signature {{
            margin-top: 20px;
            color: #bba2a2;
        }}
        strong {{
            color: #fff;
            font-weight: bold;
        }}
        .footer a {{
            color: #fff;
        }}
    </style>
</head>
<body>
    <div class="center">
        <div class="photo container">
            <div class="header container">
                <h1>3D lámpa értesítés</h1>
            </div>
            <div class="egyedi container">
                <p>Kedves <strong>%s</strong>,</p>
                <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben módosította a rendelését!</p>
                <p>A részleteket a rendeléskövetőben tudja megtekinteni:</p>
                <a class="gomb" href="https://3dlampa.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                <p>Az ön azonosító száma: %s.</p>
                <hr>
                <p>Az alábbi gombra kattintva lehetősége van nyomon követni a nyomtatás aktuális állapotát és előrehaladottságát: </p>
                <a class="gomb" href="https://rendelesek.bencedaniel.hu/nowprinting.php">Bővebben a nyomtatásomról</a>
                <p> </p>
                <hr>
                <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
            </div>
            <div class="footer container">
                <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/nowprintemailmodosit.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
            </div>
        </div>
    </div>
</body>
</html>

                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());
        }else {
             htmlContent = String.format("""
                    <html>
                    <head>
                    <meta charset="UTF-8">
                    <title>Rendelés Módosítás Értesítés</title>
                    <style>
                        .center {
                            font-family: Arial, sans-serif;
                            justify-content: center;
                            align-content: center;
                            text-align: center;
                            border-radius: 10px 10px 10px 10px;
                            max-width: 800px;
                            margin: 30 auto;
                        }
                        .photo {
                            background-image: url('https://rendelesek.bencedaniel.hu/hatter.jpg');
                            background-size: cover;
                        }
                        .header, .footer {
                            background: linear-gradient(135deg, #ffd900b2, #8b4513e1);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: px;
                            margin-top: 10px;
                        }
                        .gomb {
                            background: linear-gradient(135deg, #ffd900, #8b4513);
                            color: #fff;
                            align-items: center;
                            justify-self: center;
                            text-align: center;
                            padding: 10px;
                            border-radius: 10px 10px 10px 10px;
                            margin: 30 auto;
                            margin-top: 10px;
                            margin-bottom: 10px;
                        }
                        .container {
                            max-width: 800px;
                            margin: 30 auto;
                            border-radius: 10px;
                        }
                        .egyedi {
                            background-color: #444;
                            margin-top: 20px;
                            margin-bottom: 20px;
                            margin-left: 70px;
                            margin-right: 70px;
                            padding: 20px;
                        }
                    
                        h1 {
                            color: #ffffff;
                        }
                        p {
                            color: #ffffff;
                            line-height: 1.4;
                        }
                        a {
                            color: #58a6ff;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .signature {
                            margin-top: 20px;
                            color: #bba2a2;
                        }
                        strong {
                            color: #fff;
                            font-weight: bold;
                        }
                        .footer a {
                            color: #fff;
                        }
                    </style>
                    </head>
                    <body>
                        <div class="center">
                            <div class="photo container">
                                <div class="header container">
                                    <h1>3D lámpa értesítés</h1>
                                </div>
                                <div class="egyedi container">
                                    <p>Kedves %s,</p>
                                    <p>Ezúton értesítjük, hogy munkatársunk a rendszerünkben módosította a rendelését!</p>
                                    <p>A részleteket a rendeléskövetőben tudja megtekinteni: </p>
                                    <a class="gomb" href="https://rendelesek.bencedaniel.hu/kovetes.php?azonosito=%s">Részletek megnyitása</a>
                                    <p>Az ön azonosító száma: %s.</p>
                                    <p class="signature">Üdvözlettel,<br>Manilla Dekorkuckója</p>
                                </div>
                                <div class="footer container">
                                    <p class="wrongmail">Ha az email nem jelenik meg rendesen, ezen a linken tudja elolvasni: <a href="https://rendelesek.bencedaniel.hu/emails/mindenjoemailmodositas.html?nev=%s&azonosito=%s">Formázott email megtekintése</a></p>
                                    <p>Ez egy automatikus email. Le szeretné mondani a rendelést? Lépjen velünk kapcsolatba! <a href="mailto:info@bencedaniel.hu">info@bencedaniel.hu</a></p>
                                </div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """, order.getCustomer().getName(), order.getCustomer().getCustomerId(),order.getCustomer().getCustomerId(), order.getCustomer().getName(), order.getCustomer().getCustomerId());

        }
            emailService.sendHtmlEmail(order.getCustomer().getEmail(), subject, htmlContent);
        } catch (MessagingException e) {
            throw new EmailFailException(e.getMessage());
        }







        return "redirect:/"; // Például rendelés lista oldalra irányítunk
    }
}
