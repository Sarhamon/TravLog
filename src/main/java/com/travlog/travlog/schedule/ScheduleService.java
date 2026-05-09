package com.travlog.travlog.schedule;

import com.travlog.travlog.plan.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository repository;

    public List<Schedule> findByPlanId(Long planId) {
        return repository.findByPlanIdOrderByDateAscStartTimeAsc(planId);
    }

    public Schedule findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));
    }

    @Transactional
    public Schedule create(Plan plan, LocalDate date, String place, LocalTime startTime, String note) {
        return repository.save(new Schedule(plan, date, place, startTime, note));
    }

    @Transactional
    public void update(Long id, ScheduleForm form) {
        Schedule schedule = repository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));
        schedule.update(form.getDate(), form.getPlace(), form.getStartTime(), form.getNote());
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ScheduleNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
