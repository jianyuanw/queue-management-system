package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.services.QueueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/prototype/viewQueue")
    public String viewQueuePrototype(Model model) {
        List<QueuePosition> queuePositions = queueService.findActiveQueuePositionsForPrototype(1L);
        Queue queue = queueService.findQueue(1L);
        model.addAttribute("queuePositions", queuePositions);
        model.addAttribute("queue", queue);
        return "prototype/viewQueue";
    }

    @GetMapping("/prototype/addQueueNumber")
    public String addQueueNumberPrototype() {
        return "prototype/addQueueNumber";
    }

    @PostMapping("/prototype/addQueueNumber")
    public String addRandomQueueNumberPrototype() {
        queueService.addRandomQueueNumber(1L);
        queueService.refreshBrowsers();
        return "prototype/addQueueNumber";
    }

    @GetMapping("/prototype/sse")
    public SseEmitter registerClient() {
        SseEmitter emitter = queueService.addEmitter();
        return emitter;
    }
}
