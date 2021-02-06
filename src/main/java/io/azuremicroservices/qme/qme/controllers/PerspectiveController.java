package io.azuremicroservices.qme.qme.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.services.AccountService;

@Controller
@RequestMapping("/perspective")
public class PerspectiveController {
	private final AccountService accountService;
	
	public PerspectiveController(AccountService accountService) {
		this.accountService = accountService;
	}
	
	@GetMapping("/{perspectiveValue}")
	public String changePerspective(@PathVariable("perspectiveValue") Integer perspectiveValue, Authentication authentication) {
		MyUserDetails details = (MyUserDetails) authentication.getPrincipal();
		accountService.changePerspective(details, Role.values()[perspectiveValue]);
		return "redirect:/";
	}
}
