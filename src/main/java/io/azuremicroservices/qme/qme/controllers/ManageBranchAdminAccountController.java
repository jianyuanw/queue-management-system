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
    public String manageBranchAdminList(Model model, Authentication authentication) {
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
    public String createBranchAdminAccountForm(Model model, @PathVariable("branchId") Long branchId, Authentication authentication, RedirectAttributes redirAttr) {
    	var branch = branchService.findBranchById(branchId);
    	
    	User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
    	
    	if (branch.isEmpty() || !permissionService.authenticateBranch(user, branch.get())) {
    		alertService.createAlert(AlertColour.YELLOW, "Vendor id not found", redirAttr);
    		return "redirect:/manage/branch-admin-account/list";
    	}
        model.addAttribute("branch", branch.get());
        model.addAttribute("user", new User());
        return "manage/branch-admin-account/create";
    }

    @PostMapping("/create")
    public String createBranchAdminAccount( @Valid @ModelAttribute Branch branch, @Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/manage/branch-admin-account/create";
        }
//        accountService.createUser(user, branch);

        return "redirect:/manage/branch-admin-account/list";

    }

    /**
     *
     * @return the form of selected branch admin account information
     */
    @GetMapping("/update/{branchAccId}")
    public String updateBranchAdminForm(Model model, @PathVariable("branchAccId") Long branchAccId, RedirectAttributes redirAttr) {
    	var user = accountService.findUserById(branchAccId);
    	
    	if (user.isEmpty()) {
    		// TODO: Also check that he has sufficient authority to update this account
    		alertService.createAlert(AlertColour.YELLOW, "User id not found", redirAttr);
    		return "redirect:/manage/branch-admin-account/list";
    	}    	
    	        
        model.addAttribute("branchAdminAcc", user.get());
        return "manage/branch-admin-account/update";
    }

    @PostMapping("/update")
    public String updateBranchAdmin(Model model, @ModelAttribute @Valid User branchAdminAcc, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("branchAdminAcc", branchAdminAcc);
            return "manage/branch-admin-account/update";
        } else {
            accountService.updateUser(branchAdminAcc);
        }
        return "redirect:/manage/branch-admin-account/list";
    }

    @GetMapping("/delete/{branchAdminAccId}")
    public String deleteBranchAdminAcc(@PathVariable("branchAdminAccId") Long branchAdminAccId, RedirectAttributes redirAttr) {
        var user = accountService.findUserById(branchAdminAccId);

       	if (user.isEmpty()) {
    		// TODO: Also check that he has sufficient authority to update this account
    		alertService.createAlert(AlertColour.YELLOW, "User id not found", redirAttr);
    		return "redirect:/manage/branch-admin-account/list";
    	} else {
    		accountService.deleteUser(user.get());
    		alertService.createAlert(AlertColour.GREEN, "Branch Admin successfully deleted", redirAttr);
    	}
        
        return "redirect:/manage/branch-admin-account/list";
    }
}
