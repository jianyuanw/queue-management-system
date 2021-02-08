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
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;

@Controller
@RequestMapping("manage/app-admin-account")
public class ManageAppAdminAccountController {
	
	private final AccountService accountService;
	private final AlertService alertService;
	
	@Autowired
	public ManageAppAdminAccountController(AccountService accountService, AlertService alertService) {
		this.accountService = accountService;
		this.alertService = alertService;
	}
	
	@GetMapping("/list")
	public String ManageAppAdminAccountList(Model model) {
		model.addAttribute("appAdminAccounts", accountService.findAllUsersByRole(Role.APP_ADMIN));
		return "manage/app-admin-account/list";
	}
	
	@GetMapping("/create")
	public String CreateAppAdminAccountForm(Model model) {
		model.addAttribute("user", new User());
		return "manage/app-admin-account/create";
	}
	
	@PostMapping("/create")
	public String CreateAppAdminAccount(@Valid @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if(bindingResult.hasErrors()) {
			return "manage/app-admin-account/create";
		}
		accountService.createUser(user, Role.APP_ADMIN);
		alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully created", redirAttr);
		
		return "redirect:/manage/app-admin-account/list";
		
	}
	
	@GetMapping("/update/{appAdminAccId}")
	public String UpdateAppAdminAccountForm(Model model, @PathVariable("appAdminAccId") Long appAdminAccId, RedirectAttributes redirAttr) {
		var appAdmin = accountService.findUserById(appAdminAccId);
		if (appAdmin.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "App Admin Account not found", redirAttr);
			return "redirect:/manage/app-admin-account/list";
		} else {
			model.addAttribute("appAdminAcc", accountService.findUserById(appAdminAccId));
		}
		
		return "manage/app-admin-account/update";
	}
	
	@PostMapping("/update")
	public String updateVendor(@ModelAttribute @Valid User appAdminAcc, BindingResult bindingResult, RedirectAttributes redirAttr) {	
		if (bindingResult.hasErrors()) {
			return "manage/app-admin-account/update";
		} else {
			accountService.updateUser(appAdminAcc);
		}
		alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully updated", redirAttr);
		return "redirect:/manage/app-admin-account/list"; 
	} 
	
	@GetMapping("/delete/{appAdminAccId}")
	public String deleteVendor(@PathVariable("appAdminAccId") Long appAdminAccId, RedirectAttributes redirAttr) {
		var appAdminAcc = accountService.findUserById(appAdminAccId);

		if (appAdminAcc.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "App Admin Account not found", redirAttr);
			return "redirect:/manage/app-admin-account/list";			
		}
		
		alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully deleted", redirAttr);
		return "redirect:/manage/app-admin-account/list";
	}
	
}
