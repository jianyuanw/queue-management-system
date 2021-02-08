package io.azuremicroservices.qme.qme.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

import io.azuremicroservices.qme.qme.models.BranchOperatorNotification;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.services.BranchOperatorNotificationService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
public class NotificationController {

	private final QueueService queueService;
    private final BranchOperatorNotificationService BoNotifyService;
    
    public NotificationController(QueueService queueService,
    		BranchOperatorNotificationService BoNotifyService) {
        this.queueService = queueService;
        this.BoNotifyService=BoNotifyService;
    }
    
    @GetMapping("/manage-branchOperator/showNotification")
    public String showNotification(Model model) {

    	List<BranchOperatorNotification> BoNotifyList = BoNotifyService.findAllNotification();
    	model.addAttribute("BoNotifyList", BoNotifyList);
    	return "manage-branchOperator/branchOperatorNotification";   	
    }
    
    
    
}