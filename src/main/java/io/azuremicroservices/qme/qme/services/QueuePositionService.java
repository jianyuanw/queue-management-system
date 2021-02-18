package io.azuremicroservices.qme.qme.services;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.azuremicroservices.qme.qme.models.Counter;
import io.azuremicroservices.qme.qme.models.Queue;
import io.azuremicroservices.qme.qme.models.QueuePosition;
import io.azuremicroservices.qme.qme.models.QueuePosition.State;
import io.azuremicroservices.qme.qme.models.ScorecardDto;
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

    @Transactional
    public boolean reassignPosition(Long counterId, Long queuePositionId, Long userId, Integer position) {
    	var counter = counterRepo.findById(counterId);
    	var queuePosition = queuePositionRepo.findById(queuePositionId);
    	
    	if (counter.isEmpty() || queuePosition.isEmpty() || 
    			counter.get().getQueue() != queuePosition.get().getQueue() ||
    			counter.get().getUser().getId() != userId) {
    		return false;
    	}
    	
    	QueuePosition qp = queuePosition.get();
    	List<State> activeStates = QueuePosition.getQueuingStates();
    	Integer newPriority = 0;
    	
    	QueuePosition targetPosition = queuePositionRepo.findTopByQueue_IdAndStateInAndPositionAndQueueStartTimeGreaterThanEqualOrderByPriorityDesc(qp.getQueue().getId(), activeStates, position, LocalDate.now().atStartOfDay());
    	
    	if (targetPosition != null) {
    		newPriority = targetPosition.getPriority() + 1;
    	}

    	qp.setPosition(position);
    	qp.setPriority(newPriority);
    	queuePositionRepo.save(qp);
    	return true;
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

	public Map<String, String> findQueuePositionForNoShowListDisplaying(Long queueId) {
    	List<QueuePosition> noshow = queuePositionRepo.findAllByQueue_IdAndState(queueId, State.INACTIVE_NO_SHOW);
    	noshow.sort(new Comparator<QueuePosition>() {
			@Override
			public int compare(QueuePosition o1, QueuePosition o2) {
				if(o1.getStateChangeTime().isBefore(o2.getStateChangeTime()))
					return 1;
				else
					return -1;
			}
		});
    	Map<String, String> noshowNumberAndTime = new LinkedHashMap<>();
    	for(QueuePosition qp: noshow) {
    		noshowNumberAndTime.put(qp.getQueueNumber(),qp.getStateChangeTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")));
		}
    	return noshowNumberAndTime;
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
				long diff = ChronoUnit.MINUTES.between(qp.getQueueStartTime(), qp.getQueueEndTime());
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
		
		for (int i = 1; i <= monthBoundary; i++) {
			LocalDate checkMonth = currentDate.minusMonths(i);
			String compareMonth = checkMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01'T'00:00:00'+0800'";
			if (!forecastQueueCountData.containsKey(compareMonth)) {
				forecastQueueCountData.put(compareMonth, 0L);
			}
		}
		
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
								qp -> (ChronoUnit.MINUTES.between(qp.getQueueStartTime(), qp.getQueueEndTime())))));
		
		for (int i = 1; i <= monthBoundary; i++) {
			LocalDate checkMonth = currentDate.minusMonths(i);
			String compareMonth = checkMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")) + "-01'T'00:00:00'+0800'";
			if (!forecastEWTDataMonthly.containsKey(compareMonth)) {
				forecastEWTDataMonthly.put(compareMonth, 0.0);
			}
		}		
		
		return forecastEWTDataMonthly;
	}
	
	public Map<String, Long> generateDailyQueueCountForecast(List<QueuePosition> queuePositions, Integer dayBoundary) {
		var currentDate = LocalDateTime.now();
		
		Map<String, Long> forecastDailyQueueCountData = queuePositions.stream()
			.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.minusDays(dayBoundary).toLocalDate().atStartOfDay()) &&
						qp.getQueueStartTime().isBefore(currentDate.minusDays(1).toLocalDate().atTime(LocalTime.MAX)))
			.collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'T'00:00:00'+0800'",
							Collectors.counting()));
		
		for (int i = 1; i <= dayBoundary; i++) {
			LocalDateTime checkDay = currentDate.minusDays(i);
			String compareDay = checkDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'T'00:00:00'+0800'";
			if (!forecastDailyQueueCountData.containsKey(compareDay)) {
				forecastDailyQueueCountData.put(compareDay, 0L);
			}
		}			
		
		return forecastDailyQueueCountData;
	}
	
	public Map<String, Double> generateDailyEWTCountForecast(List<QueuePosition> queuePositions, Integer dayBoundary) {
		var currentDate = LocalDateTime.now();
		
		var forecastEWTDataDaily = queuePositions.stream()
				.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.minusDays(dayBoundary).toLocalDate().atStartOfDay()) &&
							qp.getQueueStartTime().isBefore(currentDate.minusDays(1).toLocalDate().atTime(LocalTime.MAX)))
				.collect(Collectors.groupingBy(
						qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
								+ "'T'00:00:00'+0800'",
						Collectors.averagingLong(
								qp -> (ChronoUnit.MINUTES.between(qp.getQueueStartTime(), qp.getQueueEndTime())))));
		
		for (int i = 1; i <= dayBoundary; i++) {
			LocalDateTime checkDay = currentDate.minusDays(i);
			String compareDay = checkDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'T'00:00:00'+0800'";
			if (!forecastEWTDataDaily.containsKey(compareDay)) {
				forecastEWTDataDaily.put(compareDay, 0.0);
			}
		}		
		
		return forecastEWTDataDaily;
	}
	
	public Map<String, Long> generateHourlyQueueCountForecast(List<QueuePosition> queuePositions, Integer hourBoundary) {
		var currentDateTime = LocalDateTime.now();
		var currentDate = LocalDate.now();		
		
		Map<String, Long> forecastHourlyQueueCountData = queuePositions.stream()
			.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.atTime(currentDateTime.minusHours(hourBoundary).getHour(), 0, 0)) &&
						qp.getQueueStartTime().isBefore(currentDate.atTime(currentDateTime.minusHours(1).getHour(), 59, 59)))
			.collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'''T'''HH")) + ":00:00'+0800'",
							Collectors.counting()));
		
		for (int i = 1; i <= hourBoundary; i++) {
			LocalDateTime checkHour = currentDateTime.minusHours(i);
			String compareHour = checkHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'''T'''HH")) + ":00:00'+0800'";
			if (!forecastHourlyQueueCountData.containsKey(compareHour)) {
				forecastHourlyQueueCountData.put(compareHour, 0L);
			}
		}		
		
		return forecastHourlyQueueCountData;
	}
	
	public Map<String, Double> generateHourlyEWTCountForecast(List<QueuePosition> queuePositions, Integer hourBoundary) {
		var currentDateTime = LocalDateTime.now();
		var currentDate = LocalDate.now();
		
		var forecastEWTDataHourly = queuePositions.stream()
				.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.atTime(currentDateTime.minusHours(50000).getHour(), 0, 0)) &&
						qp.getQueueStartTime().isBefore(currentDate.atTime(currentDateTime.minusHours(1).getHour(), 59, 59)))
				.collect(Collectors.groupingBy(
						qp -> ((QueuePosition) qp).getQueueStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'''T'''HH"))
								+ ":00:00'+0800'",
						Collectors.averagingLong(
								qp -> (ChronoUnit.MINUTES.between(qp.getQueueStartTime(), qp.getQueueEndTime())))));

		for (int i = 1; i <= hourBoundary; i++) {
			
			LocalDateTime checkHour = currentDateTime.minusHours(i);
			String compareHour = checkHour.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'''T'''HH")) + ":00:00'+0800'";
			if (!forecastEWTDataHourly.containsKey(compareHour)) {
				forecastEWTDataHourly.put(compareHour, 0.0);
			}
		}		
		
		return forecastEWTDataHourly;
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
	
	public List<QueuePosition> findAllQueuePositionsByBranchId(Long branchId) {
		return queuePositionRepo.findAllByQueue_Branch_Id(branchId);
	}

	public List<ScorecardDto> generateScoreCardData(List<QueuePosition> queuePositions) {
		final int FIXED_MONTH = 2;
		
		LocalDate currentDate = LocalDate.now();
		List<String> months = new ArrayList<>();
		List<ScorecardDto> scorecardList = new ArrayList<>();
		
		for (int i = FIXED_MONTH; i >= 1; i--) {
			months.add(currentDate.minusMonths(i).format(DateTimeFormatter.ofPattern("MMM yyyy")));
		}
		
		List<QueuePosition> timeFilteredPositions = queuePositions.stream()
				.filter(qp -> qp.getQueueStartTime().isAfter(currentDate.minusMonths(FIXED_MONTH).with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay()) &&
						qp.getQueueStartTime().isBefore(currentDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX)))
				.collect(Collectors.toList());				
		
		Map<String, Long> totalQueues = timeFilteredPositions.stream()
				.collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime()
						.format(DateTimeFormatter.ofPattern("MMM yyyy")),
								Collectors.counting()
						));
				
		
		Map<String, Long> totalNoShows = timeFilteredPositions.stream()
				.filter(qp -> qp.getState() == State.INACTIVE_NO_SHOW)
				.collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime()
						.format(DateTimeFormatter.ofPattern("MMM yyyy")),
								Collectors.counting()
						));
		
		Map<String, Long> totalLeft = timeFilteredPositions.stream()
				.filter(qp -> qp.getState() == State.INACTIVE_LEFT)
				.collect(Collectors.groupingBy(qp -> ((QueuePosition) qp).getQueueStartTime()
						.format(DateTimeFormatter.ofPattern("MMM yyyy")),
								Collectors.counting()
						));			
		
		Map<String, Long> maxWaitingTime = new HashMap<>();
		
		for (QueuePosition qp : timeFilteredPositions) {
			String group = qp.getQueueStartTime().format(DateTimeFormatter.ofPattern("MMM yyyy"));
			Long secondsDuration = Duration.between(qp.getQueueStartTime(), qp.getQueueEndTime()).getSeconds();
			if (maxWaitingTime.containsKey(group) && maxWaitingTime.get(group) < secondsDuration) {
				maxWaitingTime.put(group, secondsDuration);
			} else if (!maxWaitingTime.containsKey(group)) {
				maxWaitingTime.put(group, secondsDuration);
			}
		}
		
		for (String month : months) {
			ScorecardDto scorecard = new ScorecardDto();
			scorecard.setCustomerCount(totalQueues.containsKey(month) ? totalQueues.get(month) : 0l);
			scorecard.setNoShowCount(totalNoShows.containsKey(month) ? totalNoShows.get(month) : 0L);
			scorecard.setLeftQueueCount(totalLeft.containsKey(month) ? totalLeft.get(month) : 0L);
			scorecard.setMaxWaitingTime(maxWaitingTime.containsKey(month) ? maxWaitingTime.get(month) : 0L);
			scorecardList.add(scorecard);
		}
		
		return scorecardList;
		
	}
}
