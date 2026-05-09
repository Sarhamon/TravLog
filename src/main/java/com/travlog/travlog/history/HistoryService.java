package com.travlog.travlog.history;

import com.travlog.travlog.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository repository;

    public Optional<History> findByScheduleId(Long scheduleId) {
        return repository.findByScheduleId(scheduleId);
    }

    public List<History> findByPlanId(Long planId) {
        return repository.findByPlanId(planId);
    }

    @Transactional
    public History upsert(Schedule schedule, HistoryStatus status, String actualPlace, LocalTime actualTime, String review) {
        return repository.findByScheduleId(schedule.getId())
                .map(existing -> {
                    existing.update(status, actualPlace, actualTime, review);
                    return existing;
                })
                .orElseGet(() -> repository.save(new History(schedule, status, actualPlace, actualTime, review)));
    }
}
