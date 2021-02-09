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
	public String createBranchOperatorAccount(@Valid @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes redirAttr) {
		bindingResult = accountService.verifyUser(user, bindingResult);

		if (bindingResult.hasErrors()) {
			return "manage/app-admin-account/create";
		}
		
		accountService.createUser(user, Role.APP_ADMIN);
		alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully created", redirAttr);
		
		return "redirect:/manage/app-admin-account/list";
		
	}
	
	@GetMapping("/update/{userId}")
	public String initUpdateBranchOperatorAccountForm(Model model, @PathVariable("userId") Long userId, RedirectAttributes redirAttr) {
		var appAdmin = accountService.findUserById(userId);
		
		if (appAdmin.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "App Admin Account not found", redirAttr);
			return "redirect:/manage/app-admin-account/list";
		}
		
		model.addAttribute("user", appAdmin.get());
		return "manage/app-admin-account/update";
	}
	
	@PostMapping("/update")
	public String updateBranchOperator(Model model, @ModelAttribute @Valid User user, BindingResult bindingResult, RedirectAttributes redirAttr) {
		bindingResult = accountService.verifyUser(user, bindingResult);
		
		if (bindingResult.hasErrors()) {
			return "manage/app-admin-account/update";
		} 
			
		accountService.updateUser(user);
		alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully updated", redirAttr);
		return "redirect:/manage/app-admin-account/list"; 
	} 
	
	@GetMapping("/delete/{appAdminAccId}")
	public String deleteBranchOperator(@PathVariable("appAdminAccId") Long appAdminAccId, RedirectAttributes redirAttr) {
		var appAdminAcc = accountService.findUserById(appAdminAccId);

		if (appAdminAcc.isEmpty()) {
			alertService.createAlert(AlertColour.YELLOW, "App Admin Account not found", redirAttr);
			return "redirect:/manage/app-admin-account/list";			
		}
		
		accountService.deleteUser(appAdminAcc.get());
		alertService.createAlert(AlertColour.GREEN, "App Admin Account successfully deleted", redirAttr);
		return "redirect:/manage/app-admin-account/list";
	}
	
}