package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

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
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import io.azuremicroservices.qme.qme.repositories.VendorRepository;

@Controller
@RequestMapping("manage/vendor")
public class ManageVendorController {
	private final VendorRepository vendorRepo;
	private final UserRepository userRepo;
	
	@Autowired
	public ManageVendorController(VendorRepository vendorRepo, UserRepository userRepo) {
		this.vendorRepo = vendorRepo;
		this.userRepo = userRepo;
	}
	
	@GetMapping("/list")
	public String ManageVendorList(Model model) {
		model.addAttribute("vendors", vendorRepo.findAll());
		return "manage/vendor/list";
	}
	
	@GetMapping("/create")
	public String CreateVendorForm(@ModelAttribute Vendor vendor) {
		return "manage/vendor/create";
	}
	
	@PostMapping("/create")
	public String CreateVendor(@Valid @ModelAttribute Vendor vendor, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "manage/vendor/create";
		}
		vendorRepo.save(vendor);
		
		return "redirect:/manage/vendor/list";
	}
	
	@GetMapping("/update/{vendorId}")
	public String UpdateVendorForm(Model model, @PathVariable("vendorId") Long vendorId) {
		model.addAttribute("vendor", vendorRepo.findById(vendorId));
		return "manage/vendor/update";
	}
	
	@PostMapping("/update")
	public String updateVendor(@ModelAttribute @Valid Vendor vendor, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "manage/vendor/update";
		} else {
			vendorRepo.save(vendor);
		}
		return "redirect:/manage/vendor/list";
	}
	
	@GetMapping("/delete/{vendorId}")
	public String deleteVendor(@PathVariable("vendorId") Long vendorId) {
		Vendor vendor = vendorRepo.findById(vendorId).get();
		if (vendor != null) {
			vendorRepo.delete(vendor);
		}
		return "redirect:/manage/vendor/list";
	}

}
