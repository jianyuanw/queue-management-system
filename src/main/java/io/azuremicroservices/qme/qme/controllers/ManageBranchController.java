package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.VendorRepository;
import io.azuremicroservices.qme.qme.services.BranchService;

@Controller
@RequestMapping("manage/branch")
public class ManageBranchController {
	private final VendorRepository vendorRepo;
	private final BranchRepository branchRepo;
	private final BranchService branchService;
	
	@Autowired
	public ManageBranchController(VendorRepository vendorRepo, BranchRepository branchRepo, BranchService branchService) {
		this.vendorRepo = vendorRepo;
		this.branchRepo = branchRepo;
		this.branchService = branchService;
	}
	
	@GetMapping("/list")
	public String initManageBranchList(Model model) {
		model.addAttribute("branches", branchRepo.findAll());
		return "manage/branch/list";
	}
	
	@GetMapping("/create")
	public String initCreateBranchForm(Model model) {
		Branch branch = new Branch();
		// Temporary solution because no login
		// TODO: Link to current admin's vendor ID
		branch.setVendor(vendorRepo.findById(1L).get());
		model.addAttribute("branch", branch);
		return "manage/branch/create";
	}
	
	@PostMapping("/create")
	public String createBranch(@ModelAttribute @Valid Branch branch, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "manage/branch/create";
		} else {
			branchRepo.save(branch);
		}
		return "redirect:/";
	}
}
