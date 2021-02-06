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
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import io.azuremicroservices.qme.qme.repositories.VendorRepository;
import io.azuremicroservices.qme.qme.services.AccountService;

@Controller
@RequestMapping("manage/vendor-account")
public class ManageVendorAccountController {
	private final VendorRepository vendorRepo;
	private final UserRepository userRepo;
	private final AccountService accountService;
	
	@Autowired
	public ManageVendorAccountController(VendorRepository vendorRepo, UserRepository userRepo, AccountService accountService) {
		this.vendorRepo = vendorRepo;
		this.userRepo = userRepo;
		this.accountService = accountService;
	}
	
	@GetMapping("/list")
	public String ManageVendorList(Model model) {
		//Get all users first
		//TODO: Return only app_admin and vendor_admin
		model.addAttribute("vendorAccounts", userRepo.findAll());
		return "manage/vendor-account/list";
	}
	
	@GetMapping("/create/{vendorId}")
	public String CreateVendorAccountForm(Model model, @PathVariable("vendorId") Long vendorId) {
		model.addAttribute("vendor", vendorRepo.findById(vendorId));
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
		model.addAttribute("vendorAcc", userRepo.findById(vendorAccId).get());
		return "manage/vendor-account/update";
	}
	
	@PostMapping("/update")
	public String updateVendor(@ModelAttribute @Valid User vendorAcc, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "manage/vendor-account/update";
		} else {
			userRepo.save(vendorAcc);
		}
		return "redirect:/manage/vendor-account/list"; 
	} 
	
	@GetMapping("/delete/{vendorAccId}")
	public String deleteVendor(@PathVariable("vendorAccId") Long vendorAccId) {
		User user = userRepo.findById(vendorAccId).get();
		if (user != null) {
			userRepo.delete(user);
		}
		return "redirect:/manage/vendor-account/list";
	}
	
}
