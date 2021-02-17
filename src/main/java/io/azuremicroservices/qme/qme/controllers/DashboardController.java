package io.azuremicroservices.qme.qme.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String dashboard(@RequestParam(required = false, value = "branchId") Long branchId, Model model, Authentication authentication) {
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		List<Branch> branches = permissionService.getBranchPermissions(user.getId());
		model.addAttribute("branches", branches);
		
		List<Queue> queues = null;
		
		if (branchId != null) {
			queueService.findAllQueuesByBranch_Id(branchId);
			model.addAttribute("selectedBranch", branchId);
		} else {
			queueService.findAllQueuesInBranches(branches);
		}
		
		List<QueuePosition> queuePositions = queuePositionService.findAllQueuePositionsInQueues(queues);

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
		
		return "branch-admin/dashboard";
	}
	
	@PostMapping("/filter")
	public String filterDashboardByBranch(Model model, @RequestParam String branchId) {
		if(branchId == null) {
			return "branch-admin/dashboard";
		}
		
		Long lBranchId = Long.parseLong(branchId);
		List<QueuePosition> queuePositions = queuePositionService.findAllQueuePositionsByBranchId(lBranchId);
		
		Map<String, Integer> queueCountData = queuePositionService.generateQueueCountData(queuePositions);
		Map<String, Long> estWaitingTimeData = queuePositionService.generateEstimatedWaitingTimeData(queuePositions);

		model.addAttribute("queueCountData", queueCountData);
		model.addAttribute("estWaitingTimeData", estWaitingTimeData);

		// To add in services for actual
		var forecastQcDataMonthly = queuePositionService.generateQueueCountForecast(queuePositions, 10);
		var forecastEWTDataMonthly = queuePositionService.generateEWTCountForecast(queuePositions, 10);
				
		model.addAttribute("forecastQcDataMonthly", forecastQcDataMonthly);
		model.addAttribute("forecastEWTDataMonthly", forecastEWTDataMonthly);
		
		
		return "branch-admin/dashboard";
		
	}
	
}