package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.services.QueueService;
import sg.edu.iss.ims.item.Item;
import sg.edu.iss.ims.product.Product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

@Controller
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
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
    	//display queue list
    	//model.addAttribute("name",queueService.getName());
    	//model.addAttribute("name",queueService.getTimePerClient());
    	//model.addAttribute("name",queueService.getNotificationPosition());
    	//model.addAttribute("name",queueService.getNotificationDelay());
    	
    	return "manage-branchAdmin/manageQueue";
    }
    
    @GetMapping("/manage-branchAdmin/manageQueue/manageCounters")
    public String manageCounter(Model model) {
    	//manage counters
    	return "manage-branchAdmin/manageCounters";
    }
    
    @GetMapping("/manage-branchAdmin/manageQueue/createNewQueue")
    public void createNewQueue(Model model) {
    	@Autowired
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
    							Double newNotifyPosition, Double newDelayBeforeCall, LocalDateTime newCreateTime
    							Model model, RedirectAttributes redirAttr){
       	model.addAttribute("name",newName);
    	model.addAttribute("description",newDescription);
    	model.addAttribute("timePerClient",newTimePerClient);
    	model.addAttribute("notificationPosition",newNotifyPosition);
    	model.addAttribute("notificationPosition",newDelayBeforeCall); 
    	model.addAttribute("createTime",newCreateTime); 
    	return "redirect:manage-branchAdmin/manageQueue"; 	
    }
    
    @GetMapping("/manage-branchOperator/notification-create")
    public String showNotification(Model model) {
    	//get queue info
    	model.addAttribute()
    	return "manage-branchOperator/branchOperatorNotification";
    }
    
    @GetMapping("/manage-branchOperator/notification-edit")
    public String showNotification(Model model) {
    	//get queue info
    	model.addAttribute()
    	return "manage-branchOperator/branchOperatorNotification";
    }
    
    @GetMapping("/manage-branchOperator/notification-delete")
    public String showNotification(Model model) {
    	//get queue info
    	model.addAttribute()
    	return "manage-branchOperator/branchOperatorNotification";
    }
    

    @GetMapping("/prototype/sse")
    public SseEmitter registerClient() {
        return queueService.addEmitter();
    }
}
