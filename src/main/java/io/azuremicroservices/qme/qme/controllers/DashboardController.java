package io.azuremicroservices.qme.qme.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.azuremicroservices.qme.qme.configurations.security.MyUserDetails;
import io.azuremicroservices.qme.qme.models.Branch;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.services.PermissionService;
import io.azuremicroservices.qme.qme.services.QueuePositionService;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
public class DashboardController {

	private final QueuePositionService queuePositionService;
	private final PermissionService permissionService;
	private final QueueService queueService;
	
	@Autowired
	public DashboardController(QueuePositionService queuePositionService, PermissionService permissionService, QueueService queueService) {
		this.queuePositionService = queuePositionService;
		this.permissionService = permissionService;
		this.queueService = queueService;
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, Authentication authentication) {
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		List<Branch> branches = permissionService.getBranchPermissions(user.getId());
		
		List<Queue> queues = queueService.findAllQueuesInBranches(branches);
		System.out.println(queues.size());
		
		List<QueuePosition> queuePositions = queuePositionService.findAllQueuePositionsInQueues(queues);
		System.out.println(queuePositions.size());

		Map<String, Integer> queueCountData = queuePositionService.generateQueueCountData(queuePositions);
		Map<String, Long> estWaitingTimeData = queuePositionService.generateEstimatedWaitingTimeData(queuePositions);

		model.addAttribute("queueCountData", queueCountData);
		model.addAttribute("estWaitingTimeData", estWaitingTimeData);

		// To add in services for actual
		var forecastQcDataMonthly = queuePositionService.generateQueueCountForecast(queuePositions, 10);
		var forecastEWTDataMonthly = queuePositionService.generateEWTCountForecast(queuePositions, 10);
				
		model.addAttribute("forecastQcDataMonthly", forecastQcDataMonthly);
		model.addAttribute("forecastEWTDataMonthly", forecastEWTDataMonthly);

		// Under consideration
		/*
		 * var forecastQcDataDaily = qps.stream() .filter(qp -> qp.getQueueStartTime()
		 * != null) .filter(qp ->
		 * qp.getQueueStartTime().isAfter(LocalDateTime.now().minusDays(10)))
		 * .collect(Collectors.groupingBy(qp -> ((QueuePosition)
		 * qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
		 * "'T'00:00:00'+0800'", Collectors.counting()));
		 * model.addAttribute("forecastQcDataDaily", forecastQcDataDaily);
		 * 
		 * var forecastQcDataHourly = qps.stream() .filter(qp -> qp.getQueueStartTime()
		 * != null) .filter(qp ->
		 * qp.getQueueStartTime().isAfter(LocalDateTime.now().minusHours(10)))
		 * .collect(Collectors.groupingBy(qp -> ((QueuePosition)
		 * qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH")
		 * ) + ":00:00'+0800'", Collectors.counting()));
		 * model.addAttribute("forecastQcDataHourly", forecastQcDataHourly);
		 */

		var intervalMap = queuePositionService.generateDateIntervals(2, 2, 2);
		
		model.addAttribute("intervalMap", intervalMap);
		
		return "prototype/dashboard-proto";
	}
}