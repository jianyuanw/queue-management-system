package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;
import io.azuremicroservices.qme.qme.repositories.QueueRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class QueuePositionService {
    private final QueuePositionRepository queuePositionRepo;
    private final QueueRepository queueRepo;
    private final QueueService queueService;

    public QueuePositionService(QueuePositionRepository queuePositionRepo, QueueRepository queueRepo, QueueService queueService) {
        this.queuePositionRepo = queuePositionRepo;
        this.queueRepo = queueRepo;
        this.queueService = queueService;
    }

    public void updateReassignedIdPriority(Long reassignedId) {
        QueuePosition qp = queuePositionRepo.findById(reassignedId).get();
        Queue q = qp.getQueue();
        // List<QueuePosition> qps = queueService.findActiveQueuePositionsForPrototype(q.getId());
        List<QueuePosition> qps = queueService.findAllQueuePositions(q.getId());
        int p = 0;
        for (int i = 0; i<qps.size(); i++) {
            if(p < qps.get(i).getPriority())
                p = qps.get(i).getPriority();
        }
        qp.setPriority(p+1);
        queuePositionRepo.save(qp);
    }

    public void setDefaultPriorityFor(Long queuePositionId) {
        QueuePosition qp = queuePositionRepo.findById(queuePositionId).get();
        qp.setPriority(0);
        queuePositionRepo.save(qp);
    }

    public List<QueuePosition> getAllSortedQueuePositions(Long queueId) {
        List<QueuePosition> qps = queueService.findAllQueuePositions(queueId);
        sortQueuePositionsByPriorityAndPosition(qps);
        return qps;
    }

    public List<QueuePosition> getActiveSortedQueuePositions(Long queueId) {
        List<QueuePosition> qps = queueService.findActiveQueuePositionsForPrototype(queueId);
        sortQueuePositionsByPriorityAndPosition(qps);
        return qps;
    }

    public void sortQueuePositionsByPriorityAndPosition(List<QueuePosition> qps) {
        qps.sort(new Comparator<QueuePosition>() {
            @Override
            public int compare(QueuePosition o1, QueuePosition o2) {
                if(o1.getPriority() > o2.getPriority())
                    return -1;
                else if (o1.getPriority() < o2.getPriority())
                    return 1;
                else {
                    if(o1.getPosition() < o2.getPosition())
                        return -1;
                    else if (o1.getPosition() > o2.getPosition())
                        return 1;
                    else
                        return 0;
                }
            }
        });
    }
}
