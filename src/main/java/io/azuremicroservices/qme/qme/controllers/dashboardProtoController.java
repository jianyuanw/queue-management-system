package io.azuremicroservices.qme.qme.controllers;

import java.time.format.DateTimeFormatter;
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
	        
	        Map<String, Integer> data = new TreeMap<>();

	        for (QueuePosition qp : qps) {
	        	if(qp.getQueueStartTime() != null) {
	        		if (data.containsKey(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")))) {
	        			data.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), data.get(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'"))) + 1);
	        		} else {
	        			data.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), 1);
	        		}
	        	}
	        }

	        model.addAttribute("chartData", data);
	        return "dashboardProto";
	    }
}