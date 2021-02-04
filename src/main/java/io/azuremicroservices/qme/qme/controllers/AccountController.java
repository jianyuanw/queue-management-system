package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.services.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }

    // Following method will be replaced by Spring Security's in-built authentication
    @PostMapping("/login")
    public String login(String username, String password, Model model) {
        boolean isValidUser = accountService.verifyUser(username, password);
        if (isValidUser) {
            return "account/temp-login-success"; // TODO: Proper landing page
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "account/login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "account/create-client";
    }

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "account/create-client";
        }
        accountService.createClient(user);
        return "account/temp-register-success"; // TODO: Proper landing page
    }
}
