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
import io.azuremicroservices.qme.qme.models.UserVendorPermission;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import io.azuremicroservices.qme.qme.repositories.UserVendorPermissionRepository;
import io.azuremicroservices.qme.qme.repositories.VendorRepository;

@Controller
@RequestMapping("manage/vendor-account")
public class ManageVendorAccountController {
	private final VendorRepository vendorRepo;
	private final UserRepository userRepo;
	private final UserVendorPermissionRepository uvpRepo;
	
	@Autowired
	public ManageVendorAccountController(VendorRepository vendorRepo, UserRepository userRepo, UserVendorPermissionRepository uvpRepo) {
		this.vendorRepo = vendorRepo;
		this.userRepo = userRepo;
		this.uvpRepo = uvpRepo;
	}
	
	@GetMapping("/list")
	public String ManageVendorList(Model model) {
		//Get all users first
		//TODO: Return only app_admin and vendor_admin
		model.addAttribute("vendorAccounts", userRepo.findAll());
		return "manage/vendor-account/list";
	}
	
	@GetMapping("/create/{vendorId}")
	public String CreateVendorAccountForm(Model model,@ModelAttribute User user, @PathVariable("vendorId") Long vendorId) {
		model.addAttribute("vendor", vendorRepo.findById(vendorId));
		model.addAttribute("uvp", new UserVendorPermission());
		return "manage/vendor-account/create";
	}
	
	@PostMapping("/create")
	public String CreateVendorAccount(@Valid @ModelAttribute User user, @Valid @ModelAttribute UserVendorPermission uvp, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "manage/vendor-account/create";
		}
		userRepo.save(user);
		uvpRepo.save(uvp);
		
		return "redirect:/manage/vendor-account/list";
		
	}
	
}
