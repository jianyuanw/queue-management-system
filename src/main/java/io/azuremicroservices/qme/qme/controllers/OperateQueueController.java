package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.*;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;



@Controller
@RequestMapping("/OperateQueue")
public class OperateQueueController {

    private final List<User.Role> permittedPerspectiveToOperate = Arrays.asList(User.Role.BRANCH_OPERATOR);

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private QueueService queueService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        User user = myUserDetails.getUser();
        return user;
    }

    public String redirectToNoOperatePermissionPage() {
        return "branch-operator/friendlyNotifyNoOperatePermission";
    }

    public String redirectTo404Page() {
        return "error/404";
    }

    @GetMapping("/test")
    public String test() {
        return redirectToNoOperatePermissionPage();
    }

    @GetMapping("/ViewQueue")
    public String operatorViewQueue(Model model) {
        User user = getCurrentUser();
        List<Queue> queues = permissionService.getQueuePermissions(user.getId());
        HashMap<Long, Integer> queueIdWithCurrentPax = new HashMap<>();
        HashSet<Branch> branches = new HashSet<>();
        for (Queue queue: queues) {
            List<QueuePosition> queuePositions = queueService.findActiveQueuePositionsForPrototype(queue.getId());
            int pax = queuePositions.size();
            queueIdWithCurrentPax.put(queue.getId(),pax);
            branches.add(queue.getBranch());
        }
        Vendor cVendor = permissionService.getVendorPermission(user.getId());
        model.addAttribute("vendor",cVendor);
        model.addAttribute("queuesWithPax", queueIdWithCurrentPax);
        model.addAttribute("queues",queues);
        model.addAttribute("branches",branches);
        return "branch-operator/viewQueuePage";
    }

    @PostMapping("/UpdateQueueState/{queueId}")
    public String updateQueueState(@PathVariable("queueId") Long queueId) {
        queueService.updateQueueState(queueId);
        return "redirect:/OperateQueue/ViewQueue";
    }

    @GetMapping("/ViewSelectedQueue/{queueId}")
    public String viewSelectedQueue(@PathVariable("queueId") Long queueId) {
        return redirectTo404Page();
    }
}
