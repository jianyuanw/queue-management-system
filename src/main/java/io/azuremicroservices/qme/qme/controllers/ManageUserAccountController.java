package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;

@Controller
@RequestMapping("/manage/user-account")
public class ManageUserAccountController {
	private final AccountService accountService;
	private final AlertService alertService;
	
	@Autowired
	public ManageUserAccountController(AccountService accountService, AlertService alertService) {
		this.accountService = accountService;
		this.alertService = alertService;
	}
	
	@GetMapping("/list")
	public String initManageUserAccountList(Model model) {
		model.addAttribute("accounts", accountService.findAllUsers());
		return "manage/user-account/list";
	}
	
	@GetMapping("/update/{userId}")
	public String initUpdateUserAccountForm(Model model, @PathVariable("userId") Long userId, RedirectAttributes redirAttr) {
		var user = accountService.findUserById(userId);
		
		if (user.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "User Account not found", redirAttr);
			return "redirect:/manage/user-account/list";
		}
		
		model.addAttribute("user", user.get());
		return "manage/user-account/update";
	}
	
	@PostMapping("/update")
	public String updateUser(Model model, @ModelAttribute @Valid User user, BindingResult bindingResult, RedirectAttributes redirAttr) {
		bindingResult = accountService.verifyUser(user, bindingResult);
		
		if (bindingResult.hasErrors()) {
			return "manage/user-account/update";
		} 
			
		accountService.updateUser(user);
		alertService.createAlert(AlertColour.GREEN, "User Account successfully updated", redirAttr);
		return "redirect:/manage/user-account/list"; 
	} 
	
	@GetMapping("/delete/{userId}")
	public String deleteUser(@PathVariable("userId") Long userId, RedirectAttributes redirAttr) {
		var user = accountService.findUserById(userId);

		if (user.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "User Account not found", redirAttr);
			return "redirect:/manage/user-account/list";			
		}
		
		accountService.deleteUser(user.get());
		alertService.createAlert(AlertColour.GREEN, "User Account successfully deleted", redirAttr);
		return "redirect:/manage/user-account/list";
	}
	
	@GetMapping("/block/{userId}")
	public String blockUnblockUser(@PathVariable("userId") Long userId, RedirectAttributes redirAttr) {
		var user = accountService.findUserById(userId);

		if (user.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "User Account not found", redirAttr);
			return "redirect:/manage/user-account/list";			
		}
		
		if (user.get().isBlocked()) {
			accountService.unblockUser(user.get().getId());
			alertService.createAlert(AlertColour.GREEN, "User Account successfully unblocked", redirAttr);
		} else {
			accountService.blockUser(user.get().getId());
			alertService.createAlert(AlertColour.GREEN, "User Account successfully blocked", redirAttr);
		}
		
		
		return "redirect:/manage/user-account/list";		
	}
	
}
