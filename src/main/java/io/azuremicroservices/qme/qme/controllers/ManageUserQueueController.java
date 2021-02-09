package io.azuremicroservices.qme.qme.controllers;

import io.azuremicroservices.qme.qme.models.UserQueueNumberDto;
import io.azuremicroservices.qme.qme.services.ManageUserQueueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping ("manage/queue")
public class ManageUserQueueController {

    private ManageUserQueueService queueService;

    public ManageUserQueueController(ManageUserQueueService queueService) {
        this.queueService = queueService;
    }

    @GetMapping("/start")
    public String startQueue() {
        return "manage-userQueue/EnterQueue";
    }

    //return pages after user enter the queue
    @GetMapping("/join")
    public String enterQueue(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "queueId") String queueId,
            Model model
    )
    {
        UserQueueNumberDto userQueueNumberDto = queueService.enterQueue(userId, queueId);
        model.addAttribute("queueInfo", userQueueNumberDto);
        return "manage-userQueue/EnterQueue";
    }

    @RequestMapping("/leave")
    public String leaveQueue(
            @RequestParam(name = "userId") String userId,
            @RequestParam(name = "queueId") String queueId,
            Model model
    )
    {
        UserQueueNumberDto userQueueNumberDto = queueService.leaveQueue(userId, queueId);
        model.addAttribute("queueInfo", userQueueNumberDto);
        return "manage-userQueue/leaveQueue";
    }

}
