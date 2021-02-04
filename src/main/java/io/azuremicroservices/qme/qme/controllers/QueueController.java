package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.services.QueueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/prototype/viewQueue")
    public String viewQueuePrototype(Model model) {
        /* Will shift code to service layer */
        List<QueuePosition> queuePositions = queueService.findActiveQueuePositionsForPrototype(1L);
        Map<QueuePosition, Duration> queuePositionDurationMap = new LinkedHashMap<>();
        for (QueuePosition queuePosition : queuePositions) {
            queuePositionDurationMap.put(queuePosition,
                    Duration.between(queuePosition.getQueueStartTime(), LocalDateTime.now()));
        }
        Queue queue = queueService.findQueue(1L);
        model.addAttribute("queuePositionDurationMap", queuePositionDurationMap);
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
    
    @GetMapping("/manage-branchAdmin/manageQueue")
    public String createNewQueuePrototype() {
    	queueService.createNewQueue();
    	return "prototype/manageQueue";
    }

    @GetMapping("/prototype/sse")
    public SseEmitter registerClient() {
        return queueService.addEmitter();
    }
}
