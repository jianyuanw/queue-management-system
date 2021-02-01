package io.azuremicroservices.qme.qme.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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

        for (int i = 1; i <= 12; i++) {
            String month = i >= 10 ? Integer.toString(i) : "0" + i;
            for (int j = 0; j < r.nextInt(15); j++) {
                LocalDateTime ldt = LocalDateTime.parse("01-" + month + "-2020 10:10", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
                var qp = new QueuePosition();
                qp.setQueueStartTime(ldt);
                qps.add(qp);
            }
        }
        return qps;
	}
	
	 @GetMapping("/dashboard")
	    public String getPieChart(Model model) {
	        var qps = pretendChartData();

	        Map<String, Integer> graphData = new TreeMap<>();

	        for (QueuePosition qp : qps) {
	            String month = Integer.toString(qp.getQueueStartTime().getMonthValue());
	            if (graphData.containsKey(month)) {
	                graphData.put(month, graphData.get(month) + 1);
	            } else {
	                graphData.put(month, 1);
	            }
	        }

	        model.addAttribute("chartData", graphData);
	        return "dashboardProto";
	    }
	

}
