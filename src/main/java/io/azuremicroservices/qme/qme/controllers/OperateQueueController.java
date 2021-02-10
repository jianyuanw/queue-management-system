package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.*;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.services.AccountService;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueuePositionService;
import io.azuremicroservices.qme.qme.services.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@Controller
@RequestMapping("/OperateQueue")
public class OperateQueueController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private QueuePositionService queuePositionService;

    private User cUser;
    private Vendor cVendor;
    private List<Branch> cBranches;
    private HashSet<Branch> cUniqueBranches;
    private List<Queue> cQueues;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        User user = myUserDetails.getUser();
        return user;
    }

    public void pullUpdatedUserVendorBranchesQueues() {
        cUser = getCurrentUser();
        cVendor = null;    // currently not figure out how to find vendor for non vendoradmin user
        cBranches = permissionService.getBranchPermissions(cUser.getId());
        cQueues = permissionService.getQueuePermissions(cUser.getId());
        cUniqueBranches = new HashSet<>(cBranches);
    }

    public String redirectTo404Page() {
        return "error/404";
    }

    @GetMapping("/ViewQueue")
    public String operatorViewQueue(Model model) {
        pullUpdatedUserVendorBranchesQueues();

        HashMap<Long, Integer> queueIdWithCurrentPax = new HashMap<>();
        for (Queue queue: cQueues) {
            List<QueuePosition> queuePositions = queueService.findActiveQueuePositionsForPrototype(queue.getId());
            int pax = queuePositions.size();
            queueIdWithCurrentPax.put(queue.getId(),pax);
        }

        model.addAttribute("vendor","cVendor.getName()");
        model.addAttribute("queuesWithPax", queueIdWithCurrentPax);
        model.addAttribute("queues",cQueues);
        model.addAttribute("branches",cUniqueBranches);
        return "branch-operator/viewQueuePage";
    }

    @PostMapping("/UpdateQueueState/{queueId}")
    public String updateQueueState(@PathVariable("queueId") Long queueId) {
        queueService.updateQueueState(queueId);
        return "redirect:/OperateQueue/ViewQueue";
    }

    @GetMapping("/ViewSelectedQueue/{queueId}")
    public String viewSelectedQueue(@PathVariable("queueId") Long queueId,Model model) {
        pullUpdatedUserVendorBranchesQueues();

        Queue queue = queueService.findQueue(queueId);
        // List<QueuePosition> qps = queueService.findActiveQueuePositionsForPrototype(queueId);
        List<QueuePosition> qps = queueService.findAllQueuePositions(queueId);
        sortQueuePositionsByPriorityAndPosition(qps);

        model.addAttribute("vendor","cVendor.getName()");
        model.addAttribute("state",queue.getState().getDisplayValue());
        model.addAttribute("queue",queue);
        model.addAttribute("positions",qps);
        return "branch-operator/viewSelectedQueuePage";
    }

    @GetMapping("/ViewSelectedQueue/{queueId}/{reassignedId}")
    public String reassign(@PathVariable("queueId") Long queueId, @PathVariable("reassignedId")Long reassignedId) {

        queuePositionService.updateReassignedIdPriority(reassignedId);
        return "redirect:/OperateQueue/ViewSelectedQueue/"+queueId;
    }

    public void sortQueuePositionsByPriorityAndPosition(List<QueuePosition> qps) {
        qps.sort(new Comparator<QueuePosition>() {
            @Override
            public int compare(QueuePosition o1, QueuePosition o2) {
                if(o1.getPriority() > o2.getPriority())
                    return -1;
                else if (o1.getPriority() < o2.getPriority())
                    return 1;
                else {
                    if(o1.getPosition() < o2.getPosition())
                        return -1;
                    else if (o1.getPosition() > o2.getPosition())
                        return 1;
                    else
                        return 0;
                }
            }
        });
    }

}
