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
		
		if (branchId == null || branchId == -1L) {
			queues = queueService.findAllQueuesInBranches(branches);
		} else {
			queues = queueService.findAllQueuesByBranch_Id(branchId);
			model.addAttribute("selectedBranch", branchId);			
		}
		
		List<QueuePosition> queuePositions = queuePositionService.findAllQueuePositionsInQueues(queues);

		Map<String, Integer> queueCountData = queuePositionService.generateQueueCountData(queuePositions);
		Map<String, Long> estWaitingTimeData = queuePositionService.generateEstimatedWaitingTimeData(queuePositions);

		model.addAttribute("queueCountData", queueCountData);
		model.addAttribute("estWaitingTimeData", estWaitingTimeData);

		
		var forecastQcDataMonthly = queuePositionService.generateQueueCountForecast(queuePositions, 10);
		var forecastEWTDataMonthly = queuePositionService.generateEWTCountForecast(queuePositions, 10);
		var forecastQcDataDaily = queuePositionService.generateDailyQueueCountForecast(queuePositions, 8);
		var forecastEWTDataDaily = queuePositionService.generateDailyEWTCountForecast(queuePositions, 10);
		var forecastQcDataHourly = queuePositionService.generateHourlyQueueCountForecast(queuePositions, 10);
		var forecastEWTDataHourly = queuePositionService.generateHourlyEWTCountForecast(queuePositions, 10);
				
		model.addAttribute("forecastQcDataMonthly", forecastQcDataMonthly);
		model.addAttribute("forecastEWTDataMonthly", forecastEWTDataMonthly);
		model.addAttribute("forecastQcDataDaily", forecastQcDataDaily);
		model.addAttribute("forecastEWTDataDaily", forecastEWTDataDaily);
		model.addAttribute("forecastQcDataHourly", forecastQcDataHourly);
		model.addAttribute("forecastEWTDataHourly", forecastEWTDataHourly);

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
	public String filterDashboardByBranch(Model model, Authentication authentication, @RequestParam String branchId) {
		if(branchId == "-1") {
			return "redirect:/dashboard";
		}
		
		MyUserDetails user = (MyUserDetails) authentication.getPrincipal();
		
		List<Branch> branches = permissionService.getBranchPermissions(user.getId());
		model.addAttribute("branches", branches);
		
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
		
		var intervalMap = queuePositionService.generateDateIntervals(2, 2, 2);
		
		model.addAttribute("intervalMap", intervalMap);
		
		
		return "branch-admin/dashboard";
		
	}
	
}