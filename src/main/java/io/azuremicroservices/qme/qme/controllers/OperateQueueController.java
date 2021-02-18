package io.azuremicroservices.qme.qme.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.helpers.QRCodeGenerator;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePositionDto;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueuePositionService;
import io.azuremicroservices.qme.qme.services.QueueService;


@Controller
@RequestMapping("/operate-queue")
public class OperateQueueController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private QueueService queueService;
    @Autowired
    private QueuePositionService queuePositionService;
    @Autowired
    private AlertService alertService;

    @GetMapping("/view-queue")
    public String operatorViewQueue(Model model, Authentication authentication, RedirectAttributes redirAttr) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        List<Queue> queues = permissionService.getQueuePermissions(myUserDetails.getId());
        
        HashMap<Queue, Integer> queueIdWithCurrentPax = queuePositionService.countAllQueuePositionsInQueues(queues);

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

    @GetMapping("/update-queue-state/{queueId}")
    public String updateQueueState(@PathVariable("queueId") Long queueId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var queue = queueService.findQueueById(queueId);
    	
    	if (queue.isEmpty() || !permissionService.authenticateQueue(myUserDetails.getUser(), queueService.findQueue(queueId))) {
    		alertService.createAlert(AlertColour.YELLOW, "Queue not found", redirAttr);
    		return "redirect:/operate-queue";
    	}
    	
        queueService.updateQueueState(queueId);
        alertService.createAlert(AlertColour.GREEN, "Queue state updated", redirAttr);
        return "redirect:/operate-queue/view-queue";
    }
    
    @PostMapping("/sign-in")
    public String signInCounter(@RequestParam("counterId") Long counterId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterById(counterId);
    	
    	if (counter.isEmpty() || !permissionService.authenticateQueue(myUserDetails.getUser(), counter.get().getQueue())) {
    		alertService.createAlert(AlertColour.YELLOW, "Counter not found", redirAttr);
    		return "redirect:/operate-queue/view-queue";
    	}
    	
    	queueService.signInCounter(myUserDetails.getUser(), counter.get());
    	alertService.createAlert(AlertColour.GREEN, "Signed in to counter", redirAttr);
    	return "redirect:/operate-queue/view-queue";
    }
    
    @PostMapping("/sign-out")
    public String signOutCounter(@RequestParam("counterId") Long counterId, Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterById(counterId);
    	
    	if (counter.isEmpty() || !permissionService.authenticateQueue(myUserDetails.getUser(), counter.get().getQueue())) {
    		alertService.createAlert(AlertColour.YELLOW, "Counter not found", redirAttr);
    		return "redirect:/operate-queue/view-queue";
    	}
    	
    	queueService.signOutCounter(myUserDetails.getUser(), counter.get());
    	alertService.createAlert(AlertColour.GREEN, "Signed out of counter", redirAttr);
    	return "redirect:/operate-queue/view-queue";
    }
    
    @PostMapping(value = "/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQrCode(@RequestParam("queueURL") String queueURL, Authentication authentication, RedirectAttributes redirAttr) {
		return ResponseEntity.status(HttpStatus.OK).body(QRCodeGenerator.getQRCodeImage(queueURL, 800, 800));
    }

    @GetMapping("/view-selected-queue/{queueId}")
    public String viewSelectedQueue(@PathVariable("queueId") Long queueId,Model model) {
        Queue queue = queueService.findQueue(queueId);
        Vendor vendor = queue.getBranch().getVendor();
        Branch branch = queue.getBranch();

        List<QueuePosition> qps = queuePositionService.getActiveSortedQueuePositions(queueId);

        model.addAttribute("vendor",vendor);
        model.addAttribute("branch", branch);
        model.addAttribute("queue",queue);
        model.addAttribute("positions",qps);
        return "branch-operator/view-selected-queue";
    }

    @GetMapping("/no-show-list/{queueId}")
    public String viewNoShowList(@PathVariable("queueId") Long queueId, Model model) {

        Map<String, String> noShowQP = queuePositionService.findQueuePositionForNoShowListDisplaying(queueId);
        Queue queue = queueService.findQueue(queueId);
        Branch branch = queue.getBranch();
        Vendor vendor = branch.getVendor();

        model.addAttribute("noShowList",noShowQP);
        model.addAttribute("queue",queue);
        model.addAttribute("vendor",vendor);
        model.addAttribute("branch",branch);
        return "branch-operator/no-show-list";
    }

    @GetMapping("/my-counter")
    public String myCounter(Authentication authentication, Model model, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	var counter = queueService.findCounterByUserId(myUserDetails.getId());
    	
    	if (counter == null) {
    		alertService.createAlert(AlertColour.YELLOW, "You are not signed into a counter", redirAttr);
    		return "redirect:/operate-queue/view-queue";
    	}
    	
    	List<QueuePositionDto> viewQueuePositions = queueService.generateViewQueuePositions(counter);
    	
    	model.addAttribute("counter", counter);
    	model.addAttribute("viewQueuePositions", viewQueuePositions);        
        model.addAttribute("queueLength", viewQueuePositions.size());
        return "branch-operator/counter";
    }
       
    @PostMapping("/my-counter/reassign")
    public String reorderClient(@RequestParam("queuePositionId") Long queuePositionId, @RequestParam("counterId") Long counterId, @RequestParam("position") Integer position, 
    		Authentication authentication, RedirectAttributes redirAttr) {
    	MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
    	
    	if (!queuePositionService.reassignPosition(counterId, queuePositionId, myUserDetails.getId(), position)) {
    		alertService.createAlert(AlertColour.YELLOW, "Failed to reassign queue position", redirAttr);		
    	} else {
    		alertService.createAlert(AlertColour.GREEN, "Successfully reassigned queue position", redirAttr);
    	}
    	
    	return "redirect:/operate-queue/my-counter";
    }    

    @PostMapping("/my-counter")
    public String callNextNumber(@RequestParam("command") String command, Authentication authentication, RedirectAttributes redirAttr) {
        User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
        Counter counter = queueService.findCounterByUser(user);

        if (counter == null) {
            alertService.createAlert(AlertColour.RED, "An error occurred. Please try again.", redirAttr);
            return "redirect:/operate-queue/my-counter";
        }        
        
        switch (command) {
        	case "next":
                String nextNumber = queueService.callNextNumber(counter);
                queueService.refreshBrowsers(counter.getQueue().getId());
                if (nextNumber == null) {
                    alertService.createAlert(AlertColour.RED, "Failed to call next number. No people in queue.", redirAttr);
                } else {
                    alertService.createAlert(AlertColour.GREEN, "Called queue number: " + nextNumber, redirAttr);
                }
                break;
        	case "no-show":
                String noShowNumber = queueService.noShow(counter);
                queueService.refreshBrowsers(counter.getQueue().getId());
                if (noShowNumber == null) {
                    alertService.createAlert(AlertColour.RED, "An error occurred. Please try again.", redirAttr);
                } else {
                    alertService.createAlert(AlertColour.GREEN, "Queue number (" + noShowNumber + ") did not show up. You may call the next queue number.", redirAttr);
                }        		
        		break;
        }
        
        return "redirect:/operate-queue/my-counter";
    }

    @PostMapping("/no-show")
    public String noShow(Authentication authentication, RedirectAttributes redirAttr) {
        User user = ((MyUserDetails) authentication.getPrincipal()).getUser();
        Counter counter = queueService.findCounterByUser(user);
        if (counter == null) {
            alertService.createAlert(AlertColour.RED, "An error occurred. Please try again.", redirAttr);
            return "redirect:/operate-queue/my-counter";
        }

        return "redirect:/operate-queue/my-counter";
    }

    @GetMapping("/queue-number-screen/{queueId}")
    public String queueNumberScreen(@PathVariable String queueId, Model model, RedirectAttributes redirAttr) {
        Queue queue = queueService.findQueue(Long.valueOf(queueId));
        List<Counter> counters = queue.getCounters();
        if (counters.size() == 0) {
            alertService.createAlert(AlertColour.RED, "Queue has no counters.", redirAttr);
            return "redirect:/operate-queue/view-queue";
        }
        String[] missedQueueNumbers = queuePositionService.findCurrentDayMissedQueueNumbersByQueue(queue);
        model.addAttribute("missedQueueNumbers", missedQueueNumbers);
        model.addAttribute("counters", counters);
        model.addAttribute("queue", queue);
        return "branch-operator/queue-number-screen";
        // Screen design currently works best for <= 10 counters
    }

    @GetMapping("/sse/{queueId}")
    public SseEmitter registerForLiveQueueUpdate(@PathVariable String queueId) {
        return queueService.addEmitter(Long.valueOf(queueId));
    }
}
