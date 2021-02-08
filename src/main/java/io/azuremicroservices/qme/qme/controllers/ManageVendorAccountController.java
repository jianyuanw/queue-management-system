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
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.VendorService;

@Controller
@RequestMapping("manage/vendor-account")
public class ManageVendorAccountController {
	private final AccountService accountService;
	private final VendorService vendorService;
	private final PermissionService permissionService;
	private final AlertService alertService;
	
	@Autowired
	public ManageVendorAccountController(AccountService accountService, VendorService vendorService, PermissionService permissionService, AlertService alertService) {
		this.accountService = accountService;
		this.vendorService = vendorService;
		this.permissionService = permissionService;
		this.alertService = alertService;
	}
	
	@GetMapping("/list")
	public String ManageVendorAccountList(Model model) {
		model.addAttribute("vendorAccounts", accountService.findAllUsersByRole(Role.VENDOR_ADMIN));
		return "manage/vendor-account/list";
	}
	
	@GetMapping("/create/{vendorId}")
	public String CreateVendorAccountForm(Model model, @PathVariable("vendorId") Long vendorId) {
		model.addAttribute("vendor", vendorService.findVendorById(vendorId));
		model.addAttribute("user", new User());
		return "manage/vendor-account/create";
	}
	
	@PostMapping("/create")
	public String CreateVendorAccount(@Valid @ModelAttribute User user, @Valid @ModelAttribute Vendor vendor, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if(bindingResult.hasErrors()) {
			return "manage/vendor-account/create";
		}
		accountService.createUser(user, vendor);
		alertService.createAlert(AlertColour.GREEN, "Vendor Account successfully created", redirAttr);
		
		return "redirect:/manage/vendor-account/list";
		
	}
	
	@GetMapping("/update/{vendorAccId}")
	public String UpdateVendorAccountForm(Model model, @PathVariable("vendorAccId") Long vendorAccId) {
		model.addAttribute("vendorAcc", accountService.findUserById(vendorAccId));
		return "manage/vendor-account/update";
	}
	
	@PostMapping("/update")
	public String updateVendor(@ModelAttribute @Valid User vendorAcc, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if (bindingResult.hasErrors()) {
			return "manage/vendor-account/update";
		} else {
			accountService.updateUser(vendorAcc);
		}alertService.createAlert(AlertColour.GREEN, "Vendor Account successfully updated", redirAttr);
		return "redirect:/manage/vendor-account/list"; 
	} 
	
	@GetMapping("/delete/{vendorAccId}")
	public String deleteVendor(@PathVariable("vendorAccId") Long vendorAccId, RedirectAttributes redirAttr) {
		accountService.deleteUserById(vendorAccId);
		alertService.createAlert(AlertColour.GREEN, "Vendor Account successfully deleted", redirAttr);
		return "redirect:/manage/vendor-account/list";
	}
	
}
