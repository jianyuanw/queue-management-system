package io.azuremicroservices.qme.qme.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.services.PermissionService;

@Controller
@RequestMapping("/BranchOperator")
public class BranchOperatorController {
    
    @Autowired
    PermissionService permissionService;

    @GetMapping
    public String landing(Model model) {
        /*
            Explanation for why use basic security codes below:
            Before tried to use @AuthenticationPrincipal and User user = (User) authentication.getPrincipal();
            Both failed, and browser noticed that MyUserDetails object cannot cast down to User object.
            So, added getUser() method in MyUserDetails class
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        User user = myUserDetails.getUser();

        List<Queue> queues = permissionService.getQueuePermissions(user.getId());
        model.addAttribute("queues", queues);
        return "branch-operator/landing-page";
    }

}
