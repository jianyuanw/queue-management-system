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
@RequestMapping("manage/vendor-admin-account")
public class ManageVendorAdminAccountController {
	private final AccountService accountService;
	private final VendorService vendorService;
	private final PermissionService permissionService;
	private final AlertService alertService;
	
	@Autowired
	public ManageVendorAdminAccountController(AccountService accountService, VendorService vendorService, PermissionService permissionService, AlertService alertService) {
		this.accountService = accountService;
		this.vendorService = vendorService;
		this.permissionService = permissionService;
		this.alertService = alertService;
	}
	
	@GetMapping("/list")
	public String manageVendorAdminAccountList(Model model) {
		model.addAttribute("vendorAdminAccounts", accountService.findAllUsersByRole(Role.VENDOR_ADMIN));
		return "manage/vendor-admin-account/list";
	}
	
	@GetMapping("/create/{vendorId}")
	public String createVendorAdminAccountForm(Model model, @PathVariable("vendorId") Long vendorId, RedirectAttributes redirAttr) {
		var vendor = vendorService.findVendorById(vendorId);
		if (vendor.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "Vendor id not found", redirAttr);
			return "redirect:/manage/vendor-admin-account/list";
		}
		model.addAttribute("vendor", vendorService.findVendorById(vendorId));
		model.addAttribute("user", new User());
		return "manage/vendor-admin-account/create";
	}
	
	@PostMapping("/create")
	public String createVendorAdminAccount(@Valid @ModelAttribute User user, @Valid @ModelAttribute Vendor vendor, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if(bindingResult.hasErrors()) {
			return "manage/vendor-admin-account/create";
		}
		accountService.createUser(user, vendor);
		alertService.createAlert(AlertColour.GREEN, "Vendor Admin Account successfully created", redirAttr);
		
		return "redirect:/manage/vendor-admin-account/list";
		
	}
	
	@GetMapping("/update/{vendorAdminAccId}")
	public String updateVendorAdminAccountForm(Model model, @PathVariable("vendorAdminAccId") Long vendorAdminAccId, RedirectAttributes redirAttr) {
		var vendorAdminAcc = accountService.findUserById(vendorAdminAccId);
		
		if (vendorAdminAcc.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "Vendor id not found", redirAttr);
			return "redirect:/manage/vendor-account/list";			
		}
		model.addAttribute("vendorAdminAcc", accountService.findUserById(vendorAdminAccId));
		return "manage/vendor-admin-account/update";
	}
	
	@PostMapping("/update")
	public String updateVendorAdminAccount(@ModelAttribute @Valid User vendorAdminAcc, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if (bindingResult.hasErrors()) {
			return "manage/vendor-admin-account/update";
		} else {
			accountService.updateUser(vendorAdminAcc);
		}
		alertService.createAlert(AlertColour.GREEN, "Vendor Admin Account successfully updated", redirAttr);
		return "redirect:/manage/vendor-admin-account/list"; 
	} 
	
	@GetMapping("/delete/{vendorAdminAccId}")
	public String deleteVendor(@PathVariable("vendorAdminAccId") Long vendorAdminAccId, RedirectAttributes redirAttr) {
		var user = accountService.findUserById(vendorAdminAccId);
		
		if (user.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "Vendor Admin id not found", redirAttr);
		} else {
			accountService.deleteUser(user.get());
			alertService.createAlert(AlertColour.GREEN, "Vendor Admin Account successfully deleted", redirAttr);
		}
		
		
		return "redirect:/manage/vendor-admin-account/list";
	}
	
}
