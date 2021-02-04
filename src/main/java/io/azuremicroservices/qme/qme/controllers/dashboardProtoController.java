package io.azuremicroservices.qme.qme.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	        		long diff = ChronoUnit.MINUTES.between(qp.getQueueEndTime(),qp.getQueueStartTime());
	        		estWaitingTimeData.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), diff);
	        	}
	        } 

	        model.addAttribute("queueCountData", queueCountData);
	        model.addAttribute("estWaitingTimeData", estWaitingTimeData);
	        
	        //To add in services for actual
	        var forecastQcDataMonthly = qps.stream()
	        		  .filter(qp -> qp.getQueueStartTime() != null)
					  .filter(qp -> qp.getQueueStartTime().isAfter(LocalDateTime.now().minusMonths(10)))  
					  .filter(qp -> qp.getQueueStartTime().isBefore(LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfMonth())))
					  .collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01'T'00:00:00'+0800'", 
					                                 Collectors.counting()));
	        model.addAttribute("forecastQcDataMonthly", forecastQcDataMonthly);
	        
	        var forecastEWTDataMonthly = qps.stream()
	        		  .filter(qp -> qp.getQueueStartTime() != null)
					  .filter(qp -> qp.getQueueStartTime().isAfter(LocalDateTime.now().minusMonths(10)))  
					  .filter(qp -> qp.getQueueStartTime().isBefore(LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfMonth())))
					  .collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01'T'00:00:00'+0800'", 
					                                 Collectors.averagingLong(qp -> (ChronoUnit.MINUTES.between(qp.getQueueEndTime(),qp.getQueueStartTime())))));
	        model.addAttribute("forecastEWTDataMonthly", forecastEWTDataMonthly);
	       
	        
	        
	       //Under consideration 
	       /* var forecastQcDataDaily = qps.stream()
	        		  .filter(qp -> qp.getQueueStartTime() != null)
					  .filter(qp -> qp.getQueueStartTime().isAfter(LocalDateTime.now().minusDays(10)))  
					  .collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'T'00:00:00'+0800'", 
					                                 Collectors.counting()));
	        model.addAttribute("forecastQcDataDaily", forecastQcDataDaily);
	        
	        var forecastQcDataHourly = qps.stream()
	        		  .filter(qp -> qp.getQueueStartTime() != null)
					  .filter(qp -> qp.getQueueStartTime().isAfter(LocalDateTime.now().minusHours(10)))  
					  .collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH")) + ":00:00'+0800'", 
					                                 Collectors.counting()));
	        model.addAttribute("forecastQcDataHourly", forecastQcDataHourly); */
	        
	        
	       
	        //Gangster way (to be reviewed)
	        model.addAttribute("today", LocalDateTime.now());
	        model.addAttribute("todayValue", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        model.addAttribute("yesterday",LocalDateTime.now().minusDays(1));
	        model.addAttribute("yesterdayValue",LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        model.addAttribute("previousDay",LocalDateTime.now().minusDays(2));
	        model.addAttribute("previousDayValue",LocalDateTime.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        model.addAttribute("lastMonth",LocalDateTime.now().minusMonths(1));
	        model.addAttribute("lastMonthValue",LocalDateTime.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        model.addAttribute("previousMonth", LocalDateTime.now().minusMonths(2));
	        model.addAttribute("previousMonthValue", LocalDateTime.now().minusMonths(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        model.addAttribute("lastYear", LocalDateTime.now().minusYears(1));
	        model.addAttribute("lastYearValue", LocalDateTime.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        model.addAttribute("previousYear", LocalDateTime.now().minusYears(2));
	        model.addAttribute("previousYearValue", LocalDateTime.now().minusYears(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")));
	        
	        return "dashboard-proto";
	 }
	 
}