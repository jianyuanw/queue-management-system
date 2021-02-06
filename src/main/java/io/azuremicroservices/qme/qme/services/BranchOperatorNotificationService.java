package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.BranchOperatorNotification;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.repositories.BranchOperatorNotificationRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BranchOperatorNotificationService {

    private final BranchOperatorNotificationRepository BoNotifyRepo;



    public BranchOperatorNotificationService(BranchOperatorNotificationRepository BoNotifyRepo) {
        this.BoNotifyRepo = BoNotifyRepo;
    }
    
    public List<BranchOperatorNotification> findAllNotification(){
    	  return BoNotifyRepo.findAll();
    }
    
    public void saveNotification(String message, LocalTime localtime) {
    	BranchOperatorNotification BoNotify=new BranchOperatorNotification();
    	BoNotify.setMessage(message);
    	BoNotify.setTime(localtime);
    	BoNotifyRepo.save(BoNotify);
    }
}
