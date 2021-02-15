package io.azuremicroservices.qme.qme.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.models.QueuePositionDto;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueuePositionService;
import io.azuremicroservices.qme.qme.services.QueueService;


@Controller
@RequestMapping("/OperateQueue")
public class OperateQueueController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private QueuePositionService queuePositionService;
    @Autowired
    private AlertService alertService;
    

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
        cBranches = permissionService.getBranchPermissions(cUser.getId());
        cVendor = permissionService.getVendorPermission(cUser.getId());
        cQueues = permissionService.getQueuePermissions(cUser.getId());
        cUniqueBranches = new HashSet<>(cBranches);
    }

    public String redirectTo404Page() {
        return "error/404";
    }

    @GetMapping("/ViewQueue")
    public String operatorViewQueue(Model model, Authentication authentication, RedirectAttributes redirAttr) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        List<Queue> queues = permissionService.getQueuePermissions(myUserDetails.getId());
        
        HashMap<Queue, Integer> queueIdWithCurrentPax = queuePositionService.findAllQueuePositionsInQueues(queues);

        if (queues.size() < 1) {
        	alertService.createAlert(AlertColour.YELLOW, "You do not have any queue permissions", redirAttr);
        	return "redirect:/";
        }
        
        List<Branch> branches = queues.stream().map(Queue::getBranch).distinct().collect(Collectors.toList());
        Map<Long, List<Counter>> queueCounters = queuePositionService.findAllCountersByQueueId(queues);
        Counter counter = queuePositionService.findCounterByUserId(myUserDetails.getId());        
        
        model.addAttribute("vendor", queues.get(0).getBranch().getVendor());
        model.addAttribute("queueMap", queueIdWithCurrentPax);
        model.addAttribute("branches", branches);
        model.addAttribute("counter", counter);
        model.addAttribute("counters", queueCounters);
        return "branch-operator/queues";
    }

    @GetMapping("/UpdateQueueState/{queueId}")
    public String updateQueueState(@PathVariable("queueId") Long queueId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var queue = queueService.findQueueById(queueId);
    	
    	if (queue.isEmpty() || !permissionService.authenticateQueue(myUserDetails.getUser(), queueService.findQueue(queueId))) {
    		alertService.createAlert(AlertColour.YELLOW, "Queue not found", redirAttr);
    		return "redirect:/OperateQueue";
    	}
    	
        queueService.updateQueueState(queueId);
        alertService.createAlert(AlertColour.GREEN, "Queue state updated", redirAttr);
        return "redirect:/OperateQueue/ViewQueue";
    }
    
    @PostMapping("/sign-in")
    public String signInCounter(@RequestParam("counterId") Long counterId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterById(counterId);
    	
    	if (counter.isEmpty() || !permissionService.authenticateQueue(myUserDetails.getUser(), counter.get().getQueue())) {
    		alertService.createAlert(AlertColour.YELLOW, "Counter not found", redirAttr);
    		return "redirect:/OperateQueue/ViewQueue";
    	}
    	
    	queueService.signInCounter(myUserDetails.getUser(), counter.get());
    	alertService.createAlert(AlertColour.GREEN, "Signed in to counter", redirAttr);
    	return "redirect:/OperateQueue/ViewQueue";
    }
    
    @PostMapping("/sign-out")
    public String signOutCounter(@RequestParam("counterId") Long counterId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterById(counterId);
    	
    	if (counter.isEmpty() || !permissionService.authenticateQueue(myUserDetails.getUser(), counter.get().getQueue())) {
    		alertService.createAlert(AlertColour.YELLOW, "Counter not found", redirAttr);
    		return "redirect:/OperateQueue/ViewQueue";
    	}
    	
    	queueService.signOutCounter(myUserDetails.getUser(), counter.get());
    	alertService.createAlert(AlertColour.GREEN, "Signed out of counter", redirAttr);
    	return "redirect:/OperateQueue/ViewQueue";
    }
    
    @GetMapping("/current-counter")
    public String initCurrentCounter(Model model, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterByUserId(myUserDetails.getId());
    	
    	if (counter == null) {
    		alertService.createAlert(AlertColour.YELLOW, "You are not signed into a counter", redirAttr);
    		return "redirect:/OperateQueue/ViewQueue";
    	}
    	
    	List<QueuePositionDto> viewQueuePositions = queueService.generateViewQueuePositions(counter);
    	model.addAttribute("counter", counter);
    	model.addAttribute("viewQueuePositions", viewQueuePositions);
    	
    	return "branch-operator/current-counter";
    }

    @GetMapping("/ViewSelectedQueue/{queueId}")
    public String viewSelectedQueue(@PathVariable("queueId") Long queueId,Model model) {

        pullUpdatedUserVendorBranchesQueues();
        Queue queue = queueService.findQueue(queueId);

        // List<QueuePosition> qps = queuePositionService.getActiveSortedQueuePositions(queueId);
        List<QueuePosition> qps = queuePositionService.getAllSortedQueuePositions(queueId);

        model.addAttribute("vendor",cVendor.getName());
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

    @GetMapping("/NoShow/{queueId}")
    public String viewNoShowList(@PathVariable("queueId")Long queueId, Model model) {

        pullUpdatedUserVendorBranchesQueues();
        List<QueuePosition> noShowQP = queuePositionService.findAllQueuePositionsByQueueIdAndState(queueId, State.INACTIVE_NO_SHOW);
        Queue queue = queueService.findQueue(queueId);
        model.addAttribute("noShowList",noShowQP);
        model.addAttribute("queue",queue);
        model.addAttribute("vendor",cVendor.getName());
        model.addAttribute("branches",cBranches);
        model.addAttribute("queues",cQueues);
        return "branch-operator/noShowListPage";
    }

    @GetMapping("/my-counter")
    public String myCounter(Authentication authentication, Model model, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterByUserId(myUserDetails.getId());
    	
    	if (counter == null) {
    		alertService.createAlert(AlertColour.YELLOW, "You are not signed into a counter", redirAttr);
    		return "redirect:/OperateQueue/ViewQueue";
    	}
    	
    	List<QueuePositionDto> viewQueuePositions = queueService.generateViewQueuePositions(counter);
    	
    	model.addAttribute("counter", counter);
    	model.addAttribute("viewQueuePositions", viewQueuePositions);        
        model.addAttribute("queueLength", viewQueuePositions.size());
        return "branch-operator/counter";
    }

    @PostMapping("/my-counter")
    public String callNextNumber(@RequestParam("command") String command, Authentication authentication, RedirectAttributes redirAttr) {
        User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
        Counter counter = queueService.findCounterByUser(user);

        if (counter == null) {
            alertService.createAlert(AlertColour.RED, "An error occurred. Please try again.", redirAttr);
            return "redirect:/OperateQueue/my-counter";
        }        
        
        switch (command) {
        	case "next":
                String nextNumber = queueService.callNextNumber(counter);
                if (nextNumber == null) {
                    alertService.createAlert(AlertColour.RED, "Failed to call next number. No people in queue.", redirAttr);
                } else {
                    alertService.createAlert(AlertColour.GREEN, "Called queue number: " + nextNumber, redirAttr);
                }
                break;
        	case "no-show":
                String noShowNumber = queueService.noShow(counter);
                if (noShowNumber == null) {
                    alertService.createAlert(AlertColour.RED, "An error occurred. Please try again.", redirAttr);
                } else {
                    alertService.createAlert(AlertColour.GREEN, "Queue number (" + noShowNumber + ") did not show up. You may call the next queue number.", redirAttr);
                }        		
        		break;
        }
        
        return "redirect:/OperateQueue/my-counter";
    }

    @PostMapping("/no-show")
    public String noShow(Authentication authentication, RedirectAttributes redirAttr) {
        User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
        Counter counter = queueService.findCounterByUser(user);
        if (counter == null) {
            alertService.createAlert(AlertColour.RED, "An error occurred. Please try again.", redirAttr);
            return "redirect:/OperateQueue/my-counter";
        }

        return "redirect:/OperateQueue/my-counter";
    }

    @GetMapping("/queue-number-screen/{queueId}")
    public String queueNumberScreen(@PathVariable String queueId, Model model, RedirectAttributes redirAttr) {
        Queue queue = queueService.findQueue(Long.valueOf(queueId));
        List<Counter> counters = queue.getCounters();
        if (counters.size() == 0) {
            alertService.createAlert(AlertColour.RED, "Queue has no counters.", redirAttr);
            return "redirect:/OperateQueue/ViewQueue";
        }
        model.addAttribute("counters", counters);
        model.addAttribute("queue", queue);
        return "branch-operator/queue-number-screen";
        // Screen design currently works best for <= 10 counters
    }
}
