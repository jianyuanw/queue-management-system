package io.azuremicroservices.qme.qme.controllers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.azuremicroservices.qme.qme.models.QueuePosition;

@Controller
public class dashboardProtoController {
	
	public List<QueuePosition> pretendChartData() {
        // Just for simulation purposes, this should be in a Service when implemented
        Random r = new Random();

        List<QueuePosition> qps = new ArrayList<>();
        long minDay = LocalDateTime.of(2018, 1, 1, 0, 0).toInstant(ZoneOffset.UTC).getEpochSecond();
        long maxDay = LocalDateTime.of(2019, 12, 31, 23, 59).toInstant(ZoneOffset.UTC).getEpochSecond();
        
        for(int i = 0; i < 500; i++) {
        	 long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        	 LocalDateTime rStartDate = LocalDateTime.ofEpochSecond(randomDay, 0, ZoneOffset.UTC);
        	 LocalDateTime rEndDate = rStartDate.plusMinutes(r.nextInt(59));
        	 var qp = new QueuePosition();
        	 qp.setQueueStartTime(rStartDate);
        	 qp.setQueueEndTime(rEndDate);
        	 qps.add(qp);
        }
        return qps;
	}
	
	 @GetMapping("/dashboard")
	    public String dashboard(Model model, String yearSelect) {
	        var qps = pretendChartData();
	        
	        Map<String, Integer> data = new TreeMap<>();
	        Map<Integer, Map<Integer, Integer>> testdata = new TreeMap<>(); 

	        for (QueuePosition qp : qps) {
	            if (data.containsKey(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")))) {
	                data.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), data.get(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'"))) + 1);
	            } else {
	                data.put(qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")), 1);
	            }
	            int year = qp.getQueueStartTime().getYear();
	            int month = qp.getQueueStartTime().getMonthValue();
	            
	            // Please remember to remove this quick debug feature
	            if (testdata.containsKey(year)) {
	            	var monthmap = testdata.get(year);
	            	if (monthmap.containsKey(month)) {
	            		monthmap.put(month, monthmap.get(month) + 1);
	            	} else {
	            		monthmap.put(month, 1);
	            	}
	            } else {
	            	var treemap = new TreeMap<Integer, Integer>();
	            	treemap.put(month, 1);
	            	testdata.put(year, treemap);
	            }
	        }
	        
	        for (Integer year : testdata.keySet()) {
	        	for (Integer month : testdata.get(year).keySet()) {
	        		System.out.println((year + " " + month + " - " + testdata.get(year).get(month)));
	        	}
	        }
	        
	        model.addAttribute("chartData", data);
	        return "dashboardProto";
	    }
}