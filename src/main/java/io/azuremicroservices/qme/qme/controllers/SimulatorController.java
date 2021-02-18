package io.azuremicroservices.qme.qme.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.User;
import io.azuremicroservices.qme.qme.models.User.Role;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.UserRepository;
import io.azuremicroservices.qme.qme.services.AlertService;
import io.azuremicroservices.qme.qme.services.AlertService.AlertColour;
import io.azuremicroservices.qme.qme.services.QueueService;

@Controller
@RequestMapping("/simulator")
public class SimulatorController {
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	QueueService queueService;
	
	@Autowired
	AlertService alertService;
	
	@Autowired
	QueuePositionRepository queuePositionRepo;
	
	@GetMapping("/queue/{queueId}")
	public String simulateQueueActivity(@PathVariable("queueId") String queueId, RedirectAttributes redirAttr) {
		List<User> users = userRepo.findAllByRole(Role.CLIENT);
		
		for (User user : users) {
			if (user.getUsername() == "client") {
				users.remove(user);
				break;
			}
		}
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		
		final int WANTED_ITERATIONS = 20;
		final int SECONDS_TICKS = 5;
		final LocalDateTime current = LocalDateTime.now();
		
		executorService.scheduleAtFixedRate(new Runnable() {
			int iterations = 0;
			
			
			public void run() {
				if (iterations < WANTED_ITERATIONS) {				
					User user = users.get(ThreadLocalRandom.current().nextInt(users.size()));
					int random = ThreadLocalRandom.current().nextInt(3);
					
					if (random < 1) {
						queueService.enterQueue(user.getId().toString(), queueId);

						QueuePosition qp = queuePositionRepo.findByUser_IdAndQueue_IdAndQueueStartTimeGreaterThanEqual(user.getId(), Long.parseLong(queueId), current);
						qp.setState(State.ACTIVE_REQUEUE);
						queuePositionRepo.save(qp);
					} else {
						queueService.enterQueue(user.getId().toString(), queueId);
					}
					
					users.remove(user);
					iterations++;
				} else {
					executorService.shutdown();
					return;
				}
			}
			
		}, 0, SECONDS_TICKS, TimeUnit.SECONDS);
		
		alertService.createAlert(AlertColour.GREEN, "Queue simulation started", redirAttr);
		return "redirect:/";
	}
}
