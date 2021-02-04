package io.azuremicroservices.qme.qme.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.azuremicroservices.qme.qme.repositories.BranchRepository;
import io.azuremicroservices.qme.qme.services.BranchService;

@Controller
@RequestMapping("manage/branch")
public class ManageBranchController {
	private final BranchRepository branchRepo;
	private final BranchService branchService;
	
	@Autowired
	public ManageBranchController(BranchRepository branchRepo, BranchService branchService) {
		this.branchRepo = branchRepo;
		this.branchService = branchService;
	}
	
	@GetMapping("/list")
	public String initManageBranchList(Model model) {
		model.addAttribute("branches", branchRepo.findAll());
		return "manage/branch/list";
	}
}
