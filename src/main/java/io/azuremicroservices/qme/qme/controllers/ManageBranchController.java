package io.azuremicroservices.qme.qme.controllers;

import java.io.IOException;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.BranchService;
import io.azuremicroservices.qme.qme.services.PermissionService;

@Controller
@RequestMapping("/manage/branch")
public class ManageBranchController {
	private final BranchService branchService;
	private final PermissionService permissionService;
	private final AlertService alertService;	
	private final AccountService accountService;

	
	@Autowired
	public ManageBranchController(BranchService branchService, PermissionService permissionService, AlertService alertService, AccountService accountService) {
		this.branchService = branchService;
		this.permissionService = permissionService;
		this.alertService = alertService;
		this.accountService = accountService;
	}
	
	@GetMapping("/list")
	public String initManageBranchList(Model model, Authentication authentication) {
		Vendor vendor = permissionService.getVendorPermission(((MyUserDetails) authentication.getPrincipal()).getId());
		
		model.addAttribute("branches", branchService.findAllBranchesByVendorId(vendor.getId()));
		
		return "manage/branch/list";
	}
	
	@GetMapping("/create")
	public String initCreateBranchForm(Model model, Authentication authentication) {		
		Vendor vendor = permissionService.getVendorPermission(((MyUserDetails) authentication.getPrincipal()).getId());
		
		Branch branch = new Branch();
		branch.setVendor(vendor);
		model.addAttribute("branch", branch);
		
		return "manage/branch/create";
	}
	
	@PostMapping("/create")
	public String createBranch(Model model, @ModelAttribute @Valid Branch branch, BindingResult bindingResult, RedirectAttributes redirAttr, @RequestParam("file") MultipartFile branchImage)
	throws IOException{
		
		if (branchService.branchNameExistsForVendor(branch.getName(), branch.getVendor().getId())) {
			bindingResult.rejectValue("name", "error.name", "Branch name already exists");
		}
		if (bindingResult.hasErrors()) {
			return "manage/branch/create";
		}
		
		branchService.createBranch(branchImage, branch);
		
		alertService.createAlert(AlertColour.GREEN, "Branch successfully created", redirAttr);
		return "redirect:/manage/branch/list";
	}
	
	@GetMapping("/update/{branchId}")
	public String initUpdateBranchForm(Model model, @PathVariable("branchId") Long branchId, Authentication authentication, RedirectAttributes redirAttr) {
		var branch = branchService.findBranchById(branchId);
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		if (branch.isEmpty() || !permissionService.authenticateVendor(accountService.findUserByUsername(user.getUsername()), branch.get().getVendor())) {
			alertService.createAlert(AlertColour.YELLOW, "Branch could not be found", redirAttr);			
			return "redirect:/manage/branch/list";
		}
		
		model.addAttribute("branch", branch.get());
		return "manage/branch/update";
	}

	@PostMapping("/update")
	public String updateBranch(@ModelAttribute Branch branch, BindingResult bindingResult, @PathParam("branchId") Long branchId, RedirectAttributes redirAttr, @RequestParam("file") MultipartFile branchImage) throws IOException {
		if (bindingResult.hasErrors()) {
			return "manage/branch/update";
		} 

		branchService.updateBranch(branchImage, branch);
		alertService.createAlert(AlertColour.GREEN, "Branch successfully updated", redirAttr);
		return "redirect:/manage/branch/list";
	}
	
	@GetMapping("/delete/{branchId}")
	public String deleteBranch(@PathVariable("branchId") Long branchId, Authentication authentication, RedirectAttributes redirAttr) throws IOException {
		var branch = branchService.findBranchById(branchId);
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		if (branch.isEmpty() || !permissionService.authenticateVendor(accountService.findUserByUsername(user.getUsername()), branch.get().getVendor())) {
			alertService.createAlert(AlertColour.YELLOW, "Branch could not be found", redirAttr);
		} else {
			branchService.deleteBranch(branch.get());
			alertService.createAlert(AlertColour.GREEN, "Branch successfully deleted", redirAttr);			
		}
		
		return "redirect:/manage/branch/list";
	}
}
