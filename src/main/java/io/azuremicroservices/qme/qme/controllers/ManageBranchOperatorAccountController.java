package io.azuremicroservices.qme.qme.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.OperatorService;
import io.azuremicroservices.qme.qme.services.PermissionService;

@Controller
@RequestMapping("/manage/branch-operator-account")
public class ManageBranchOperatorAccountController {
    private final AccountService accountService;
    private final PermissionService permissionService;
    private final OperatorService operatorService;
    private final AlertService alertService;

    @Autowired
    public ManageBranchOperatorAccountController(AccountService accountService, PermissionService permissionService, OperatorService operatorService, AlertService alertService) {
        this.accountService = accountService;
        this.permissionService = permissionService;
        this.operatorService = operatorService;
        this.alertService = alertService;
    }

    @GetMapping("list")
    public String manageOperatorList(Model model){
        model.addAttribute("branchOperatroAccounts",accountService.findAllUsersByRole(Role.BRANCH_OPERATOR));
        return "manage/branch-operator-account/list";
    }
}
