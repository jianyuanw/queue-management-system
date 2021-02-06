package io.azuremicroservices.qme.qme.controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchOperatorNotification;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.BranchOperatorNotificationService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
public class QueueController {

    private final QueueService queueService;
    private final BranchOperatorNotificationService BoNotifyService;

    public QueueController(QueueService queueService,
    		BranchOperatorNotificationService BoNotifyService) {
        this.queueService = queueService;
        this.BoNotifyService=BoNotifyService;
    }

    @GetMapping("/prototype/viewQueue")
    public String viewQueuePrototype(Model model) {
        /* Will shift code to service layer */
        List<QueuePosition> queuePositions = queueService.findActiveQueuePositionsForPrototype(1L);
        Map<QueuePosition, Duration> queuePositionDurationMap = new LinkedHashMap<>();
        for (QueuePosition queuePosition : queuePositions) {
            queuePositionDurationMap.put(queuePosition,
                    Duration.between(queuePosition.getQueueStartTime(), LocalDateTime.now()));
        }
        Queue queue = queueService.findQueue(1L);
        model.addAttribute("queuePositionDurationMap", queuePositionDurationMap);
        model.addAttribute("queue", queue);
        return "prototype/viewQueue";
    }

    @GetMapping("/prototype/addQueueNumber")
    public String addQueueNumberPrototype() {
        return "prototype/addQueueNumber";
    }

    @PostMapping("/prototype/addQueueNumber")
    public String addRandomQueueNumberPrototype() {
        queueService.addRandomQueueNumber(1L);
        queueService.refreshBrowsers();
        return "prototype/addQueueNumber";
    }
    
    @GetMapping("/manage-branchAdmin/manageQueue")
    public String showQueueManageForm(Model model) {

    	//model.addAttribute("name",queueService.getName());
    	//model.addAttribute("name",queueService.getTimePerClient());
    	//model.addAttribute("name",queueService.getNotificationPosition());
    	//model.addAttribute("name",queueService.getNotificationDelay());
    	
    	return "manage-branchAdmin/manageQueue";
    }
    
    public String createNewQueuePrototype() {
    	// Commented out due to error in Service
    	// queueService.createNewQueue();
    	return "prototype/manageQueue";
    }
    
    @GetMapping("/manage-branchAdmin/manageCounters")
    public String manageCounter(Model model) {
    	//manage counters
    	return "manage-branchAdmin/manageCounters";
    }
    
    @GetMapping("/manage-branchAdmin/createNewQueue")
    public String createNewQueue(Model model) {
    	Queue queue=new Queue();  	
    	model.addAttribute("name",queue.getName());
    	model.addAttribute("description",queue.getDescription());
    	model.addAttribute("timePerClient",queue.getTimePerClient());
    	model.addAttribute("notificationPosition",queue.getNotificationPosition());
    	model.addAttribute("notificationDelay",queue.getNotificationDelay());
    	return "manage-branchAdmin/createQueue";
    }
    
    @PostMapping("/manage-branchAdmin/saveNewQueue")
    public String saveNewQueue(@ModelAttribute @Valid Queue queue, BindingResult queueBinding, 
    							@ModelAttribute @Valid Vendor vendor, BindingResult vendorBinding,
    							@ModelAttribute @Valid Branch branch, BindingResult branchBinding,
    							String newName, String newDescription, Double newTimePerClient, 
    							Integer newNotifyPosition, Double newDelayBeforeCall,
    							Model model, RedirectAttributes redirAttr){
       	//create queue
    	queueService.createNewQueue(vendor, branch, newName,newDescription,
    			newTimePerClient, newNotifyPosition, newDelayBeforeCall);  
    	
    	model.addAttribute("name",newName);
    	model.addAttribute("description",newDescription);
    	model.addAttribute("timePerClient",newTimePerClient);
    	model.addAttribute("notificationPosition",newNotifyPosition);
    	model.addAttribute("notificationDelay",newDelayBeforeCall);
    	
    	//create queue notification
    	LocalTime localTime=LocalTime.now();    	
    	String message="create new queue: "+
    			branch+newName+
    			newDescription+newTimePerClient+
    			newNotifyPosition+newDelayBeforeCall;
    	BoNotifyService.saveNotification(message,localTime);
    	
    	return "redirect:manage-branchAdmin/manageQueue"; 	
    }
    
    @GetMapping("/manage-branchAdmin/editQueue")
    public String editNewQueue(@ModelAttribute @Valid Queue queue, BindingResult queueBinding, 
			@ModelAttribute @Valid Vendor vendor, BindingResult vendorBinding,
			@ModelAttribute @Valid Branch branch, BindingResult branchBinding,
			Model model) { 
 	
    	model.addAttribute("oldName",queue.getName());
    	model.addAttribute("oldDescription",queue.getDescription());
    	model.addAttribute("oldTimePerClient",queue.getTimePerClient());
    	model.addAttribute("oldNotificationPosition",queue.getNotificationPosition());
    	model.addAttribute("oldNotificationDelay",queue.getNotificationDelay());
    	
    	Queue newQueue=new Queue();  	
    	model.addAttribute("name",newQueue.getName());
    	model.addAttribute("description",newQueue.getDescription());
    	model.addAttribute("timePerClient",newQueue.getTimePerClient());
    	model.addAttribute("notificationPosition",newQueue.getNotificationPosition());
    	model.addAttribute("notificationDelay",newQueue.getNotificationDelay());
    	return "manage-branchAdmin/editQueue";
    }
    
    @PostMapping("/manage-branchAdmin/saveEditQueue")
    public String saveEditQueue(@ModelAttribute @Valid Queue queue, BindingResult queueBinding, 
			@ModelAttribute @Valid Vendor vendor, BindingResult vendorBinding,
			@ModelAttribute @Valid Branch branch, BindingResult branchBinding,
			String newName, String newDescription, Double newTimePerClient, 
			Integer newNotifyPosition, Double newDelayBeforeCall, LocalDateTime newCreateTime,
			Model model, RedirectAttributes redirAttr){
    	//editQueue
    	Long id=queue.getId();
    	queueService.editQueue(id, newName, newDescription,
    			newTimePerClient, newNotifyPosition, newDelayBeforeCall);
    	//modify model
    	model.addAttribute("name",newName);
    	model.addAttribute("description",newDescription);
    	model.addAttribute("timePerClient",newTimePerClient);
    	model.addAttribute("notificationPosition",newNotifyPosition);
    	model.addAttribute("notificationDelay",newDelayBeforeCall);
    	//editQueue notification
    	LocalTime localTime=LocalTime.now();
    	String message="edit queue: "+
    			"old name->"+newName+
    			"old description->"+newDescription+
    			"old time per client->"+newTimePerClient+
    			"old notify position->"+newNotifyPosition+
    			"old delay beforecall->"+newDelayBeforeCall;
    	BoNotifyService.saveNotification(message,localTime);
  	
    	return "redirect:manage-branchAdmin/manageQueue";
    }
    
    @GetMapping("/manage-branchAdmin/deleteQueue")
    public String deleteQueue(@ModelAttribute @Valid Queue queue, BindingResult queueBinding, 
			@ModelAttribute @Valid Vendor vendor, BindingResult vendorBinding,
			@ModelAttribute @Valid Branch branch, BindingResult branchBinding,
			Model model, RedirectAttributes redirAttr) {
    	
    	Long id=queue.getId();    	
    	queueService.deleteQueue(id);
    	
    	LocalTime localTime=LocalTime.now();
    	String queueName=queue.getName();
    	String vendorName=vendor.getName();
    	String branchName=branch.getName();  
    	String message="delete queue: "+vendorName+" "+branchName+" "+queueName;
    	BoNotifyService.saveNotification(message,localTime);
    	
    	return "redirect:manage-branchAdmin/manageQueue";
    }
    
    
    // Commented out due to errors, please either rename the methods or overload properly or combine the methods
  @GetMapping("/manage-branchOperator/notification")
    public String showNotification(Model model) {
	  List<BranchOperatorNotification> BoNotifyList=
			  BoNotifyService.findAllNotification();
      model.addAttribute("BoNotifyList",BoNotifyList);
      return "manage-branchOperator/branchOperatorNotification";
    }    

    @GetMapping("/prototype/sse")
    public SseEmitter registerClient() {
        return queueService.addEmitter();
    }
}