package io.azuremicroservices.qme.qme.controllers;

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
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.BranchService;
import io.azuremicroservices.qme.qme.services.PermissionService;

@Controller
@RequestMapping("/manage/branch-admin-account")
public class ManageBranchAdminAccountController {
    private final AccountService accountService;
    private final PermissionService permissionService;
    private final BranchService branchService;
    private final AlertService alertService;

    @Autowired
    public ManageBranchAdminAccountController(AccountService accountService, PermissionService permissionService, BranchService branchService, AlertService alertService) {
        this.accountService = accountService;
        this.permissionService = permissionService;
        this.branchService = branchService;
        this.alertService = alertService;
    }

    /**
     * @return the webpage of list all of branch admin account
     */
    @GetMapping("/list")
    public String initManageBranchAdminList(Model model, Authentication authentication) {
    	MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
    	
    	Vendor vendor = permissionService.getVendorPermission(user.getId());
    	
        model.addAttribute("branchAdminAccounts", accountService.findAllUsersByRoleAndVendor(Role.BRANCH_ADMIN, vendor));
        return "manage/branch-admin-account/list";
    }

    /**
     *
     * @return the form of create branch admin account
     */
    @GetMapping("/create/{branchId}")
    public String initCreateBranchAdminAccountForm(Model model, @PathVariable("branchId") Long branchId, Authentication authentication, RedirectAttributes redirAttr) {
    	var branch = branchService.findBranchById(branchId);
    	
    	User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
    	
    	if (branch.isEmpty() || !permissionService.authenticateVendor(user, branch.get().getVendor())) {
    		alertService.createAlert(AlertColour.YELLOW, "Branch id not found", redirAttr);
    		return "redirect:/manage/branch-admin-account/list";
    	}
    	
        model.addAttribute("branch", branch.get());
        model.addAttribute("user", new User());
        return "manage/branch-admin-account/create";
    }

    @PostMapping("/create")
    public String createBranchAdminAccount(@ModelAttribute Branch branch, @Valid @ModelAttribute User user, BindingResult bindingResult, RedirectAttributes redirAttr) {
        bindingResult = accountService.verifyUser(user, bindingResult);
    	
    	if (bindingResult.hasErrors()) {
            return "/manage/branch-admin-account/create";
        }
    	
        accountService.createUser(user, branch);
        alertService.createAlert(AlertColour.GREEN, "Branch admin successfully created", redirAttr);
        return "redirect:/manage/branch-admin-account/list";
    }

    /**
     *
     * @return the form of selected branch admin account information
     */
    @GetMapping("/update/{branchAccId}")
    public String initUpdateBranchAdminForm(Model model, @PathVariable("branchAccId") Long branchAccId, Authentication authentication, RedirectAttributes redirAttr) {
    	User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
    	var branchAdmin = accountService.findUserById(branchAccId);
    	
    	if (branchAdmin.isEmpty() || !permissionService.authenticateVendor(user, branchAdmin.get().getUserBranchPermissions().get(0).getBranch().getVendor())) {
    		alertService.createAlert(AlertColour.YELLOW, "User id not found", redirAttr);
    		return "redirect:/manage/branch-admin-account/list";
    	}    	
    	        
        model.addAttribute("user", branchAdmin.get());
        return "manage/branch-admin-account/update";
    }

    @PostMapping("/update")
    public String updateBranchAdmin(Model model, @ModelAttribute @Valid User user, BindingResult bindingResult) {
    	bindingResult = accountService.verifyUser(user, bindingResult);
    	
        if (bindingResult.hasErrors()) {
            return "manage/branch-admin-account/update";
        } 
            
    	accountService.updateUser(user);
        return "redirect:/manage/branch-admin-account/list";
    }

    @GetMapping("/delete/{branchAdminAccId}")
    public String deleteBranchAdmin(@PathVariable("branchAdminAccId") Long branchAccId, Authentication authentication, RedirectAttributes redirAttr) {
    	User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
    	var branchAdmin = accountService.findUserById(branchAccId);
    	
    	if (branchAdmin.isEmpty() || !permissionService.authenticateVendor(user, branchAdmin.get().getUserBranchPermissions().get(0).getBranch().getVendor())) {
    		alertService.createAlert(AlertColour.YELLOW, "User id not found", redirAttr);
    		return "redirect:/manage/branch-admin-account/list";
    	}   

		accountService.deleteUser(branchAdmin.get());
		alertService.createAlert(AlertColour.GREEN, "Branch Admin successfully deleted", redirAttr);
        
        return "redirect:/manage/branch-admin-account/list";
    }
}
