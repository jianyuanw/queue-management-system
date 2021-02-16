package io.azuremicroservices.qme.qme.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.repositories.CounterRepository;
import io.azuremicroservices.qme.qme.repositories.QueuePositionRepository;

@Service
public class QueuePositionService {
    private final QueuePositionRepository queuePositionRepo;
    private final CounterRepository counterRepo;

    public QueuePositionService(QueuePositionRepository queuePositionRepo, CounterRepository counterRepo) {
        this.queuePositionRepo = queuePositionRepo;
        this.counterRepo = counterRepo;
    }

    public void updateReassignedIdPriority(Long reassignedId) {
        QueuePosition qp = queuePositionRepo.findById(reassignedId).get();
        Queue q = qp.getQueue();

        List<QueuePosition> qps = q.getQueuePositions();
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
    	return queuePositionRepo.findAllByQueue_IdOrderByPositionAscPriorityDesc(queueId);
    }

    public List<QueuePosition> getActiveSortedQueuePositions(Long queueId) {
    	return queuePositionRepo.findAllByQueue_IdAndStateInOrderByPositionAscPriorityDesc(queueId, QueuePosition.getQueuingStates());
    }

	public HashMap<Queue, Integer> countAllQueuePositionsInQueues(List<Queue> queuePermissions) {
		HashMap<Queue, Integer> queueCount = new HashMap<>();

		for (Queue queue : queuePermissions) {
			queueCount.put(queue, queuePositionRepo.countByQueue_IdAndStateIn(queue.getId(), QueuePosition.getQueuingStates()));
		}
		
		return queueCount;
	}

	public Counter findCounterByUserId(Long id) {
		return counterRepo.findByUser_Id(id);		
	}

	public Map<Long, List<Counter>> findAllCountersByQueueId(List<Queue> queues) {
		Map<Long, List<Counter>> queueCounters = new HashMap<>();
		
		for (Queue queue : queues) {
			queueCounters.put(queue.getId(), counterRepo.findAllByQueue_Id(queue.getId()));
		}
		
		return queueCounters;
	}



	public List<QueuePosition> findAllQueuePositionsByQueueIdAndActiveStates(Long queueId) {
		return queuePositionRepo.findAllByQueue_IdAndStateIn(queueId, QueuePosition.getQueuingStates());
	}

	public List<QueuePosition> findAllQueuePositionsByQueueIdAndState(Long queueId, State inactiveNoShow) {
		return queuePositionRepo.findAllByQueue_IdAndState(queueId, inactiveNoShow);
	}

	public List<QueuePosition> findAllQueuePositionsByBranchIdIn(List<Long> branchIds) {
		return queuePositionRepo.findAllByQueue_Branch_IdIn(branchIds);
	}

	public Map<String, Integer> generateQueueCountData(List<QueuePosition> queuePositions) {
		HashMap<String, Integer> queueCountData = new HashMap<>();
		
		for (QueuePosition qp : queuePositions) {
			String formattedDateTime = qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'"));
			if (queueCountData.containsKey(formattedDateTime)) {
				queueCountData.put(formattedDateTime, queueCountData.get(formattedDateTime) + 1);
			} else {
				queueCountData.put(formattedDateTime, 1);							
			}
		}
		
		return queueCountData;
	}

	public Map<String, Long> generateEstimatedWaitingTimeData(List<QueuePosition> queuePositions) {
		HashMap<String, Long> estWaitingTimeData=  new HashMap<>();
		
		for (QueuePosition qp : queuePositions) {
			if (qp.getQueueEndTime() != null) {
				long diff = ChronoUnit.MINUTES.between(qp.getQueueEndTime(), qp.getQueueStartTime());
				estWaitingTimeData.put(
						qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+0800'")),
						diff);
			}
		}
		
		return estWaitingTimeData;
	}

	public List<QueuePosition> findAllQueuePositionsInQueues(List<Queue> queues) {
		var queueIds = queues.stream()
				.map(Queue::getId)				
				.collect(Collectors.toList());
		
		return queuePositionRepo.findAllByQueue_IdIn(queueIds);
	}

	public Map<String, Long> generateQueueCountForecast(List<QueuePosition> queuePositions, Integer monthBoundary) {
		var currentDate = LocalDate.now();
		
		Map<String, Long> forecastQueueCountData = queuePositions.stream()
			.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.minusMonths(monthBoundary).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()) &&
						qp.getQueueStartTime().isBefore(currentDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX)))
			.collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime()
					.format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01'T'00:00:00'+0800'",
							Collectors.counting()));
		
		return forecastQueueCountData;
	}

	public Map<String, Double> generateEWTCountForecast(List<QueuePosition> queuePositions, Integer monthBoundary) {
		var currentDate = LocalDate.now();
		
		var forecastEWTDataMonthly = queuePositions.stream()
				.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.minusMonths(monthBoundary).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()) &&
						qp.getQueueStartTime().isBefore(currentDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX)))
				.collect(Collectors.groupingBy(
						qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM"))
								+ "-01'T'00:00:00'+0800'",
						Collectors.averagingLong(
								qp -> (ChronoUnit.MINUTES.between(qp.getQueueEndTime(), qp.getQueueStartTime())))));
		
		return forecastEWTDataMonthly;
	}

	public Map<String, List<LocalDateTime>> generateDateIntervals(Integer dayInterval, Integer monthInterval, Integer yearInterval) {
		Map<String, List<LocalDateTime>> dateBoundaryMapping = new HashMap<>();
		LocalDateTime currentTime = LocalDateTime.now();
		
		List<LocalDateTime> dayList = new ArrayList<>();
		
		for (int i = 0; i <= dayInterval; i++) {
			dayList.add(currentTime.minusDays(i));
		}
		
		dateBoundaryMapping.put("dayInterval", dayList);
		
		List<LocalDateTime> monthList = new ArrayList<>();
		
		for (int i = 0; i <= monthInterval; i++) {
			monthList.add(currentTime.minusMonths(i));
		}
		
		dateBoundaryMapping.put("monthInterval", monthList);
		
		List<LocalDateTime> yearList = new ArrayList<>();
		
		for (int i = 0; i <= yearInterval; i++) {
			yearList.add(currentTime.minusYears(i));
		}
		
		dateBoundaryMapping.put("yearInterval", yearList);
		
		return dateBoundaryMapping;
	}

	public String[] findCurrentDayMissedQueueNumbersByQueue(Queue queue) {
    	LocalDateTime startOfCurrentDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
    	return queuePositionRepo
				.findAllByQueueAndStateAndQueueStartTimeAfter(queue, State.INACTIVE_NO_SHOW, startOfCurrentDay)
				.stream()
				.map(QueuePosition::getQueueNumber)
				.toArray(String[]::new);
	}
}
