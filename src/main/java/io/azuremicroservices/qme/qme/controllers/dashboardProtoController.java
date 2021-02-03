package io.azuremicroservices.qme.qme.controllers;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;

@Controller
public class dashboardProtoController {
	
     @Autowired
	QueuePositionRepository qpRepo;
     
     
	 @GetMapping("/dashboard")
	    public String dashboard(Model model) {
	        List<QueuePosition> qps = qpRepo.findAll();
	        
	        Map<String, Integer> queueCountData = new TreeMap<>();
	        Map<String, Long> estWaitingTimeData = new TreeMap<>();

	        for (QueuePosition qp : qps) {
	        	if(qp.getQueueStartTime() != null) {
	        		if (queueCountData.containsKey(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")))) {
	        			queueCountData.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), queueCountData.get(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'"))) + 1);
	        		} else {
	        			queueCountData.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), 1);
	        		}
	        	}
	        }
	        
	        for(QueuePosition qp: qps) {
	        	if(qp.getQueueStartTime() != null && qp.getQueueEndTime() != null) {
	        		long diff = ChronoUnit.MINUTES.between(qp.getQueueStartTime(),qp.getQueueEndTime());
	        		estWaitingTimeData.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), diff);
	        	}
	        } 

	        model.addAttribute("queueCountData", queueCountData);
	        model.addAttribute("estWaitingTimeData", estWaitingTimeData);
	        return "dashboardProto";
	    }
}