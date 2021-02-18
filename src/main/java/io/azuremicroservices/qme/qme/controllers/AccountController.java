package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AccountController {

    private final AccountService accountService;
    private final AlertService alertService;

    public AccountController(AccountService accountService, AlertService alertService) {
        this.accountService = accountService;
        this.alertService = alertService;
    }

	@GetMapping("/")
	public String landingRedirector(Authentication authentication) {
		if (authentication == null) {
			return "public-landing-page";
		} else {
			return "redirect:/landing-page";
		}
	}
	
	@GetMapping("/landing-page")
	public String landingPage(Authentication authentication) {
		if (authentication == null) {
			return "redirect:/login";
		}
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		
		User user = userDetails.getUser();
        if (user.getPerspective() == User.Role.APP_ADMIN) {
            return "app-admin/landing-page";
        } else if (user.getPerspective() == User.Role.VENDOR_ADMIN) {
            return "vendor-admin/landing-page";
        } else if (user.getPerspective() == User.Role.BRANCH_ADMIN) {
            return "branch-admin/landing-page";
        } else if (user.getPerspective() == User.Role.BRANCH_OPERATOR) {
            return "redirect:/operate-queue/view-queue";
        } else {
            return "redirect:/home";
        }		
	}

    @GetMapping("/login")
    public String loginClient(@ModelAttribute("error") String error, Model model) {
        if (!error.equals("")) {
            model.addAttribute("error", error);
        }
        return "account/login-client";
    }

    @GetMapping("/login-admin")
    public String loginAdmin() {
        return "account/login-admin";
    }

    @GetMapping("/login/success")
    public String loginSuccess(HttpServletRequest request, RedirectAttributes redirAttr) {
		var principal = request.getUserPrincipal();
		
		if (principal == null) {
			return "redirect:/login/error";
		}
		
		User user = accountService.findUserByUsername(request.getUserPrincipal().getName());
		alertService.createAlert(AlertService.AlertColour.GREEN, "Login successful", redirAttr);

        if (user.getPerspective() == User.Role.APP_ADMIN) {
            return "app-admin/landing-page";
        } else if (user.getPerspective() == User.Role.VENDOR_ADMIN) {
            return "vendor-admin/landing-page";
        } else if (user.getPerspective() == User.Role.BRANCH_ADMIN) {
            return "branch-admin/landing-page";
        } else if (user.getPerspective() == User.Role.BRANCH_OPERATOR) {
            return "redirect:/operate-queue/view-queue";
        } else {
            return "redirect:/home";
        }		
    }

    @GetMapping("/login/error")
    public String loginError(HttpServletRequest request, RedirectAttributes redirAttr) {
        String error = "An error occured. Please try again.";
        HttpSession session = request.getSession(false);
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex instanceof BadCredentialsException) {
                error = "Incorrect username or password";
            }
            if (ex instanceof LockedException) {
                error = "Your account is locked";
            }
        }
        redirAttr.addFlashAttribute("error", error);
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

    @GetMapping("/register")
    public String registerClient(Model model) {
        model.addAttribute("user", new User());
        return "account/register-client";
    }

    @PostMapping("/register")
    public String registerClient(@Valid User user, BindingResult bindingResult, RedirectAttributes redirAttr) {
    	accountService.verifyUser(user, bindingResult);
    	
        if (bindingResult.hasErrors()) {
            return "account/register-client";
        }
        
        alertService.createAlert(AlertColour.GREEN, "Account successfully created", redirAttr);
        accountService.createUser(user, Role.CLIENT);
        return "account/register-client-success";
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
}
