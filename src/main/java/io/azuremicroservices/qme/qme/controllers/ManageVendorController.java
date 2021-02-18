package io.azuremicroservices.qme.qme.controllers;

import java.io.IOException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.VendorService;

@Controller
@RequestMapping("manage/vendor")
public class ManageVendorController {
	private final VendorService vendorService;
	private final AlertService alertService;
	
	@Autowired
	public ManageVendorController(VendorService vendorService, AlertService alertService) {
		this.vendorService = vendorService;
		this.alertService = alertService;
	}
	
	@GetMapping("/list")
	public String initManageVendorList(Model model) {
		model.addAttribute("vendors", vendorService.findAllVendors());
		return "manage/vendor/list";
	}
	
	@GetMapping("/create")
	public String initCreateVendorForm(@ModelAttribute Vendor vendor) {
		return "manage/vendor/create";
	}
	
	@PostMapping("/create")
	public String createVendor(@Valid @ModelAttribute Vendor vendor, BindingResult bindingResult, RedirectAttributes redirAttr, @RequestParam("file") MultipartFile vendorImage) throws IOException {
		if (vendorService.companyUidExists(vendor.getCompanyUid())) {
			bindingResult.rejectValue("companyUid", "error.companyUid", "Company UID already exists");
		}
		if (bindingResult.hasErrors()) {
			return "manage/vendor/create";
		}
		
		vendorService.createVendor(vendorImage, vendor);		
		alertService.createAlert(AlertColour.GREEN, "Vendor successfully created", redirAttr);
		
		return "redirect:/manage/vendor/list";
	}
	
	@GetMapping("/update/{vendorId}")
	public String initUpdateVendorForm(Model model, @PathVariable("vendorId") Long vendorId, RedirectAttributes redirAttr) {
		var vendor = vendorService.findVendorById(vendorId);
		
		if (vendor.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "Vendor could not be found", redirAttr);
			return "redirect:/manage/vendor/list";
		}
		
		model.addAttribute("vendor", vendor.get());
		return "manage/vendor/update";
	}
	
	@PostMapping("/update")
	public String updateVendor(@ModelAttribute @Valid Vendor vendor, BindingResult bindingResult, RedirectAttributes redirAttr, @RequestParam("file") MultipartFile vendorImage) throws IOException {
		if (bindingResult.hasErrors()) {
			return "manage/vendor/update";
		}
		
		vendorService.updateVendor(vendorImage, vendor);
		alertService.createAlert(AlertColour.GREEN, "Vendor successfully updated", redirAttr);
		return "redirect:/manage/vendor/list";
	}
	
	@GetMapping("/delete/{vendorId}")
	public String deleteVendor(@PathVariable("vendorId") Long vendorId, RedirectAttributes redirAttr) throws IOException{
		var vendor = vendorService.findVendorById(vendorId); 

		if (vendor.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "Vendor could not be found", redirAttr);
		}
		
		vendorService.deleteVendor(vendor.get());
		alertService.createAlert(AlertColour.GREEN, "Vendor successfully deleted", redirAttr);
		return "redirect:/manage/vendor/list";
	}

}
