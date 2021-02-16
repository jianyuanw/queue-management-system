package io.azuremicroservices.qme.qme.controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.BranchOperatorNotification;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.Vendor;
import io.azuremicroservices.qme.qme.services.BranchOperatorNotificationService;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueuePositionService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
public class QueueController {
	/*
		Feature available control:
			 List<User.Role> permittedRoleForViewQueue : contains allowed roles for view
			 List<User.Role> permittedRoleForEditQueue : contains allowed roles for edit
			 pass user to html and recognize user.getPerspective() to show/not show edit buttons
			 pass user to html and recognize user.getRole() to show different navigation bar
			 return "no_permission_error.html" or 404 page for no permission.

		Use cases relevant with queue
		Branch operator: advance queue
						call next in line
						check-in to counter
						close / open queue
						move client forward
						notified rejoined
						raise ticket
						view queue
						view rejoin list
						view notification

		Branch admin: Manage operator account
					Manage queue
					View branch analytics
					View branch queues
					View operators
		Vendor admin:

		App admin:
	 */

	private final List<User.Role> permittedRoleForViewQueue = Arrays.asList(User.Role.BRANCH_OPERATOR, User.Role.BRANCH_ADMIN, User.Role.VENDOR_ADMIN);
	private final List<User.Role> permittedRoleForEditQueue = Arrays.asList(User.Role.BRANCH_OPERATOR);

    private final QueueService queueService;
    private final BranchOperatorNotificationService BoNotifyService;
    private final QueuePositionService queuePositionService;    
    private PermissionService permissionService;

    public QueueController(QueueService queueService, QueuePositionService queuePositionService, PermissionService permissionService,
    		BranchOperatorNotificationService BoNotifyService) {
        this.queueService = queueService;
        this.BoNotifyService=BoNotifyService;
        this.queuePositionService = queuePositionService;
        this.permissionService = permissionService;
    }
    
    @GetMapping("/manage-branchAdmin/manageQueue")
    public String showQueueManageForm(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
		User user = myUserDetails.getUser();
		if(!permittedRoleForViewQueue.contains(user.getRole()))
			return "/no_permission_error";

		List<Queue> queues = permissionService.getQueuePermissions(user.getId());
		model.addAttribute("queuelist", queues);
		model.addAttribute("user",user);
    	return "manage-branchAdmin/manageQueue";
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
    
    @GetMapping("/manage-branchAdmin/editQueue/{queueId}")
    public String editNewQueue(@PathVariable("queueId") Long id, @ModelAttribute @Valid Queue queue, BindingResult queueBinding, 
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
    
    @GetMapping("/manage-branchAdmin/deleteQueue/{queueId}")
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
}