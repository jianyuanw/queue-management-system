package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class AccountController {

    private final AccountService accountService;
    private final AlertService alertService;

    public AccountController(AccountService accountService, AlertService alertService) {
        this.accountService = accountService;
        this.alertService = alertService;
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("error") String error, Model model) {
        if (!error.equals("")) {
            model.addAttribute("error", error);
        }
        return "account/login";
    }

    @GetMapping("/login/success")
    public String loginSuccess(HttpServletRequest request, RedirectAttributes redirAttr) {
        User user = accountService.findUserByUsername(request.getUserPrincipal().getName());
        if (user != null) {
            alertService.createAlert(AlertService.AlertColour.GREEN, "Login successful", redirAttr);
            if (user.getRole() == User.Role.APP_ADMIN) {
                return "redirect:/app-admin";
            } else if (user.getRole() == User.Role.VENDOR_ADMIN) {
                return "redirect:/vendor-admin";
            } else if (user.getRole() == User.Role.BRANCH_ADMIN) {
                return "redirect:/branch-admin";
            } else if (user.getRole() == User.Role.BRANCH_OPERATOR) {
                return "branch-operator/landing-page";
            } else {
                return "redirect:/client";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/login/error")
    public String loginError(RedirectAttributes redirAttr) {
        redirAttr.addFlashAttribute("error", "Incorrect username or password");
        return "redirect:/login";
    }

    @GetMapping("/login/expired")
    public String loginExpired(RedirectAttributes redirAttr) {
        alertService.createAlert(AlertService.AlertColour.YELLOW,
                "Your session has expired. Please login again.", redirAttr);
        return "redirect:/login";
    }

    // TODO: Remove the following endpoint (and html file) after client logout is implemented
    @GetMapping("/logout")
    public String logout() {
        return "account/logout";
    }

    @GetMapping("/logout/success")
    public String logoutSuccess(RedirectAttributes redirAttr) {
        alertService.createAlert(AlertService.AlertColour.GREEN, "Logout successful", redirAttr);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerClient(Model model) {
        model.addAttribute("user", new User());
        return "account/register-client";
    }

    @PostMapping("/register")
    public String registerClient(@Valid User user, BindingResult bindingResult) {
        if (accountService.usernameExists(user.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username exists.");
        }
        if (accountService.emailExists(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email exists.");
        }
        if (bindingResult.hasErrors()) {
            return "account/register-client";
        }
        accountService.createUser(user, Role.CLIENT);
        return "account/temp-register-success"; // TODO: Proper landing page
    }

    @GetMapping("/app-admin")
    public String landingPageAppAdmin() {
        return "app-admin/landing-page";
    }

    @GetMapping("/vendor-admin")
    public String landingPageVendorAdmin() {
        return "vendor-admin/landing-page";
    }

    @GetMapping("/branch-admin")
    public String landingPageBranchAdmin() {
        return "branch-admin/landing-page";
    }

    @GetMapping("/client")
    public String landingPageClient() {
        return "client/landing-page";
    }
}
