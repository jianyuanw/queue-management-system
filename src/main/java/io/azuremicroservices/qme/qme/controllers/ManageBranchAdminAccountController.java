package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.UserBranchPermission;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.repositories.UserBranchPermissionRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import io.azuremicroservices.qme.qme.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/manage/branch-admin-account")
public class ManageBranchAdminAccountController {
    private final BranchRepository branchRepo;
    private final UserRepository userRepo;
    private final AccountService accountService;
    private final UserBranchPermissionRepository uBPRepo;
    private final PasswordEncoder pwdEncoder;

    @Autowired
    public ManageBranchAdminAccountController(BranchRepository branchRepo, UserRepository userRepo, AccountService accountService, UserBranchPermissionRepository uBPRepo, PasswordEncoder pwdEncoder) {
        this.branchRepo = branchRepo;
        this.userRepo = userRepo;
        this.accountService = accountService;
        this.uBPRepo = uBPRepo;
        this.pwdEncoder = pwdEncoder;
    }

    /**
     * @return the webpage of list all of branch admin account
     */
    @GetMapping("/list")
    public String ManageBranchAdminList(Model model) {
        model.addAttribute("branchAdminAccounts", userRepo.findBranchAdmin());
        return "manage/branch-admin-account/list";
    }

    /**
     *
     * @return the form of create branch admin account
     */
    @GetMapping("/create/{branchAdminId}")
    public String CreateBranchAdminAccountForm(Model model, @PathVariable("branchAdminId") Long branchId) {
        model.addAttribute("branch", branchRepo.findById(branchId));
        model.addAttribute("user", new User());
        return "manage/branch-admin-account/create";
    }

    @PostMapping("/create")
    public String CreateBranchAdminAccount( @Valid @ModelAttribute Branch branch, @Valid @ModelAttribute User user, BindingResult bindingResult) {
        if (accountService.usernameExists(user.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username exists.");
        }
        if (accountService.emailExists(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email exists.");
        }
        if(bindingResult.hasErrors()) {
            return "/manage/branch-admin-account/create";
        }
        System.out.println(branch);
        user.setPerspective(User.Role.BRANCH_ADMIN);
        user.setPassword(pwdEncoder.encode(user.getPassword()));
        accountService.createBranchAdmin(user, branch);

        return "redirect:/manage/branch-admin-account/list";

    }

    /**
     *
     * @return the form of selected branch admin account information
     */
    @GetMapping("/update/{branchAccId}")
    public String UpdateBranchAdminForm(Model model, @PathVariable("branchAccId") Long branchAccId) {
        User branchAdminAcc = userRepo.findById(branchAccId).get();
        model.addAttribute("branchAdminAcc", branchAdminAcc);
        return "manage/branch-admin-account/update";
    }

    @PostMapping("/update")
    public String updateBranchAdmin(Model model, @ModelAttribute @Valid User branchAdminAcc, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("branchAdminAcc", branchAdminAcc);
            return "manage/branch-admin-account/update";
        } else {
            userRepo.save(branchAdminAcc);
        }
        return "redirect:/manage/branch-admin-account/list";
    }

    @GetMapping("/delete/{branchAdminAccId}")
    public String deleteBranchAdminAcc(@PathVariable("branchAdminAccId") Long branchAdminAccId) {
        User user = userRepo.findById(branchAdminAccId).get();
        List<UserBranchPermission> list= uBPRepo.findByUser(user);
        for(UserBranchPermission u : list){
            u.setUser(null);
        }
        uBPRepo.saveAll(list);
        if (user != null) {
            userRepo.delete(user);
        }
        return "redirect:/manage/branch-admin-account/list";
    }
}
