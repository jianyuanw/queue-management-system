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

import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.VendorService;

@Controller
@RequestMapping("manage/vendor-account")
public class ManageVendorAccountController {
	private final AccountService accountService;
	private final VendorService vendorService;
	
	@Autowired
	public ManageVendorAccountController(AccountService accountService, VendorService vendorService) {
		this.accountService = accountService;
		this.vendorService = vendorService;
	}
	
	@GetMapping("/list")
	public String ManageVendorList(Model model) {
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
	public String CreateVendorAccount(@Valid @ModelAttribute User user, @Valid @ModelAttribute Vendor vendor, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "manage/vendor-account/create";
		}
		accountService.createVendorAdmin(user, vendor);
		
		return "redirect:/manage/vendor-account/list";
		
	}
	
	@GetMapping("/update/{vendorAccId}")
	public String UpdateVendorAccountForm(Model model, @PathVariable("vendorAccId") Long vendorAccId) {
		model.addAttribute("vendorAcc", accountService.findUserById(vendorAccId));
		return "manage/vendor-account/update";
	}
	
	@PostMapping("/update")
	public String updateVendor(@ModelAttribute @Valid User vendorAcc, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "manage/vendor-account/update";
		} else {
			accountService.updateUser(vendorAcc);
		}
		return "redirect:/manage/vendor-account/list"; 
	} 
	
	@GetMapping("/delete/{vendorAccId}")
	public String deleteVendor(@PathVariable("vendorAccId") Long vendorAccId) {
		accountService.deleteUserById(vendorAccId);
		return "redirect:/manage/vendor-account/list";
	}
	
}
