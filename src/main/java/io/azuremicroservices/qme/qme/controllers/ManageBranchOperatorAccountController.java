package io.azuremicroservices.qme.qme.controllers;

import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
@RequestMapping("manage/branch-operator-account")
public class ManageBranchOperatorAccountController {
	
	private final AccountService accountService;
	private final AlertService alertService;
	private final PermissionService permissionService;
	private final QueueService queueService;
	
	@Autowired
	public ManageBranchOperatorAccountController(AccountService accountService, AlertService alertService, 
			PermissionService permissionService, QueueService queueService) {
		this.accountService = accountService;
		this.alertService = alertService;
		this.permissionService = permissionService;
		this.queueService = queueService;
	}
	
	@GetMapping("/list")
	public String initManageBranchOperatorAccountList(Model model, Authentication authentication) {
    	MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
    	
    	List<Branch> branches = permissionService.getBranchPermissions(user.getId());		
		
    	model.addAttribute("branches", branches);
		model.addAttribute("branchOperators", accountService.findAllUsersByRoleAndBranchIn(Role.BRANCH_OPERATOR, branches));
		return "manage/branch-operator-account/list";
	}
	
	@GetMapping("/create")
	public String initCreateBranchOperatorAccountForm(Model model, Authentication authentication) {
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		List<Branch> branches = permissionService.getBranchPermissions(user.getId());
		
		model.addAttribute("user", new User());
		model.addAttribute("branches", branches);
		model.addAttribute("queues", queueService.findAllQueuesInBranches(branches));
		return "manage/branch-operator-account/create";
	}
	
	@PostMapping("/create")
	public String createBranchOperatorAccount(Model model, @Valid @ModelAttribute User user, BindingResult bindingResult, Authentication authentication,
			@RequestParam(name = "checkboxes", required = false) List<String> checkboxes, @RequestParam("branchId") String branchId, RedirectAttributes redirAttr) {
		bindingResult = accountService.verifyUser(user, bindingResult);

		
		if (branchId == null) {
			model.addAttribute("branchIdError", "Branch needs to be selected");
		}
		
		if (checkboxes == null) {
			model.addAttribute("queueIdError", "At least one queue permission needs to be given");
		}

		if (bindingResult.hasErrors() || model.containsAttribute("queueIdError") || model.containsAttribute("branchIdError")) {
			MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
			
			List<Branch> branches = permissionService.getBranchPermissions(userDetails.getId());
			
			model.addAttribute("branches", branches);
			model.addAttribute("queues", queueService.findAllQueuesInBranches(branches));			
			return "manage/branch-operator-account/create";
		}
		
		accountService.createUser(user, checkboxes);
		alertService.createAlert(AlertColour.GREEN, "Branch Operator Account successfully created", redirAttr);
		
		return "redirect:/manage/branch-operator-account/list";
		
	}
//	
	@GetMapping("/update/{userId}")
	public String initUpdateBranchOperatorAccountForm(Model model, @PathVariable("userId") Long userId, Authentication authentication, RedirectAttributes redirAttr) {
		var branchOperator = accountService.findUserById(userId);
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();	
		
		if (branchOperator.isEmpty() || ! permissionService.checkAuthorityOver(userDetails.getUser(), branchOperator.get())) {
			alertService.createAlert(AlertColour.YELLOW, "Branch Operator Account not found", redirAttr);
			return "redirect:/manage/branch-operator-account/list";
		}
		
		model.addAttribute("user", branchOperator.get());
		return "manage/branch-operator-account/update";
	}
	
	@PostMapping("/update")
	public String updateBranchOperator(Model model, @ModelAttribute @Valid User user, BindingResult bindingResult, Authentication authentication, RedirectAttributes redirAttr) {
		if (bindingResult.hasErrors()) {
			return "manage/branch-operator-account/update";
		} 
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();	
		
		if (!permissionService.checkAuthorityOver(userDetails.getUser(), user)) {
			alertService.createAlert(AlertColour.YELLOW, "Branch Operator Account not found", redirAttr);
			return "redirect:/manage/branch-operator-account/list";
		}		
		
		accountService.updateUser(user);
		alertService.createAlert(AlertColour.GREEN, "Branch Operator Account successfully updated", redirAttr);
		return "redirect:/manage/branch-operator-account/list"; 
	} 
	
	@GetMapping("/delete/{branchOperatorAccId}")
	public String deleteBranchOperator(@PathVariable("branchOperatorAccId") Long branchOperatorAccId, Authentication authentication, RedirectAttributes redirAttr) {
		var branchOperatorAcc = accountService.findUserById(branchOperatorAccId);

		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
		
		if (branchOperatorAcc.isEmpty() || !permissionService.checkAuthorityOver(userDetails.getUser(), branchOperatorAcc.get())) {
			alertService.createAlert(AlertColour.YELLOW, "Branch Operator Account not found", redirAttr);
			return "redirect:/manage/branch-operator-account/list";			
		}
		
		accountService.deleteUser(branchOperatorAcc.get());
		alertService.createAlert(AlertColour.GREEN, "Branch Operator Account successfully deleted", redirAttr);
		return "redirect:/manage/branch-operator-account/list";
	}
	
}