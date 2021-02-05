package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

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
import io.azuremicroservices.qme.qme.services.PermissionService;

@Controller
@RequestMapping("manage/branch")
public class ManageBranchController {
	private final VendorRepository vendorRepo;
	private final BranchRepository branchRepo;
	private final BranchService branchService;
	private final PermissionService permissionService;
	
	@Autowired
	public ManageBranchController(VendorRepository vendorRepo, BranchRepository branchRepo, BranchService branchService, PermissionService permissionService) {
		this.vendorRepo = vendorRepo;
		this.branchRepo = branchRepo;
		this.branchService = branchService;
		this.permissionService = permissionService;
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
		return "redirect:/manage/branch/list";
	}
	
	@GetMapping("/update/{branchId}")
	public String initUpdateBranchForm(Model model, @PathParam("branchId") Long branchId) {
		Branch branch = branchRepo.findById(branchId).get();
		// TODO: Authenticate when security is in
		//if (permissionService.authenticateVendor(user, vendor))
		// Temporary solution because no login
		// TODO: Link to current admin's vendor ID		
		model.addAttribute("branch", branch);
		return "manage/branch/update";
	}

	@PostMapping("/update")
	public String updateBranch(@ModelAttribute @Valid Branch branch, BindingResult bindingResult, @PathParam("branchId") Long branchId) {
		if (bindingResult.hasErrors()) {
			return "manage/branch/create";
		} else {
			branchRepo.save(branch);
		}
		return "redirect:/manage/branch/list";
	}
	
	@PostMapping("/delete")
	public String deleteBranch(@PathParam("branchId") Long branchId) {
//		Branch branch = branchRepo.findById(branchId).get();
//		// TODO: Authenticate when security is in
//		//if (permissionService.authenticateVendor(user, vendor))
//		// Temporary solution because no login
//		// TODO: Link to current admin's vendor ID		
//		model.addAttribute("branch", branch);
//		return "manage/branch/update";
		return "redirect:/";
	}
}
