package io.azuremicroservices.qme.qme.testRefreshBrowser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class TestQueueController {

    private final List<SseEmitter> emitters = new ArrayList<>();

    @Autowired
    private TestQueuePositionRepository repo;

    @GetMapping("/test")
    public String displayQueuePositions(Model model) {
        List<TestQueuePosition> queuePositions = (List<TestQueuePosition>) repo.findAll();
        model.addAttribute("queuePositions", queuePositions);
        return "testRefreshBrowser/displayQueuePositions";
    }

    @GetMapping("/test/addQueuePosition")
    public String addQueuePosition() {
        return "testRefreshBrowser/addQueuePosition";
    }

    @PostMapping("/test/addQueuePosition")
    public String addRandomQueuePosition() {
        List<TestQueuePosition> queuePositions = (List<TestQueuePosition>) repo.findAll();
        int lastPosition = queuePositions.stream()
                .mapToInt(x -> x.getPosition())
                .max()
                .getAsInt();
        Random rnd = new Random();
        char randomAlphabet = (char) ('A' + rnd.nextInt(26));
        String queueNumber = String.valueOf(randomAlphabet) + rnd.nextInt(100);
        repo.save(new TestQueuePosition(queueNumber, lastPosition + 1));
        refreshBrowsers();
        return "testRefreshBrowser/addQueuePosition";
    }

    @GetMapping("/test/sse")
    public SseEmitter registerClient() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitters.forEach(System.out::println); // For debugging
        try {
            emitter.send(LocalDateTime.now().toString());
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        emitter.onCompletion(() -> emitters.remove(emitter));
        return emitter;
    }

    public void refreshBrowsers() {
        synchronized (emitters) {
            emitters.forEach(emitter -> {
                try {
                    emitter.send("refresh");
                } catch (Exception e) {
                    emitter.completeWithError(e);
                    emitters.remove(emitter);
                }
            });
        }
    }
}
