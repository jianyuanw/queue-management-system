package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.services.AccountService;
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

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
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
//            redirAttr.addFlashAttribute("alert", new Alert("success", "Login successful"));
            if (user.getRole() == User.Role.APP_ADMIN) {
                return "app-admin/landing-page";
            } else if (user.getRole() == User.Role.VENDOR_ADMIN) {
                return "vendor-admin/landing-page";
            } else if (user.getRole() == User.Role.BRANCH_ADMIN) {
                return "branch-admin/landing-page";
            } else if (user.getRole() == User.Role.BRANCH_OPERATOR) {
                return "redirect:/BranchOperator";
            } else {
                return "client/landing-page";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/login/error")
    public String loginError(RedirectAttributes redirAttr) {
//        redirAttr.addFlashAttribute("alert", new Alert("warning", "Incorrect username or password"));
        redirAttr.addFlashAttribute("error", "Incorrect username or password");
        return "redirect:/login";
    }

    @GetMapping("/login/expired")
    public String loginExpired(RedirectAttributes redirAttr) {
//        redirAttr.addFlashAttribute("alert", new Alert("warning", "Your session has expired. Please login again."));
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "account/logout";
    }

    @GetMapping("/logout/success")
    public String logoutSuccess(RedirectAttributes redirAttr) {
//        redirAttr.addFlashAttribute("alert", new Alert("success", "Logout successful"));
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
        accountService.createClient(user);
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

    @GetMapping("/branch-operator")
    public String landingPageBranchOperator() {
        return "branch-operator/landing-page";
    }
}
