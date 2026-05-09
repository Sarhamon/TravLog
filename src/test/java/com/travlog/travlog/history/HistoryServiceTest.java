package com.travlog.travlog.history;

import com.travlog.travlog.common.JpaAuditingConfig;
import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanRepository;
import com.travlog.travlog.schedule.Schedule;
import com.travlog.travlog.schedule.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({HistoryService.class, JpaAuditingConfig.class})
class HistoryServiceTest {

    @Autowired
    HistoryService service;

    @Autowired
    PlanRepository planRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    void upsert_inserts_when_no_existing_history() {
        Schedule schedule = newSchedule();

        History inserted = service.upsert(schedule, HistoryStatus.DONE, "기요미즈데라", LocalTime.of(10, 30), "좋았음");

        assertThat(inserted.getId()).isNotNull();
        assertThat(inserted.getStatus()).isEqualTo(HistoryStatus.DONE);
        assertThat(inserted.getReview()).isEqualTo("좋았음");

        Optional<History> found = service.findByScheduleId(schedule.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getActualPlace()).isEqualTo("기요미즈데라");
    }

    @Test
    void upsert_updates_when_existing_history() {
        Schedule schedule = newSchedule();
        History first = service.upsert(schedule, HistoryStatus.DONE, "원래장소", LocalTime.of(9, 0), "초안");

        History second = service.upsert(schedule, HistoryStatus.CHANGED, "다른장소", LocalTime.of(11, 0), "변경됨");

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(second.getStatus()).isEqualTo(HistoryStatus.CHANGED);
        assertThat(second.getActualPlace()).isEqualTo("다른장소");
        assertThat(second.getReview()).isEqualTo("변경됨");
    }

    private Schedule newSchedule() {
        Plan plan = planRepository.save(new Plan(
                "오사카", "오사카",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3),
                null, null));
        return scheduleRepository.save(new Schedule(
                plan, LocalDate.of(2026, 7, 1), "유니버설", LocalTime.of(10, 0), null));
    }
}
