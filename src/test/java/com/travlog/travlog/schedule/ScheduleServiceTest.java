package com.travlog.travlog.schedule;

import com.travlog.travlog.common.JpaAuditingConfig;
import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({ScheduleService.class, JpaAuditingConfig.class})
class ScheduleServiceTest {

    @Autowired
    ScheduleService service;

    @Autowired
    PlanRepository planRepository;

    @Test
    void findByPlanId_orders_by_date_then_startTime() {
        Plan plan = planRepository.save(new Plan(
                "오사카", "오사카",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 3),
                null, null));

        service.create(plan, LocalDate.of(2026, 7, 2), "B-오후", LocalTime.of(15, 0), null);
        service.create(plan, LocalDate.of(2026, 7, 1), "A-오전", LocalTime.of(9, 0), null);
        service.create(plan, LocalDate.of(2026, 7, 1), "A-오후", LocalTime.of(14, 0), null);
        service.create(plan, LocalDate.of(2026, 7, 2), "B-오전", LocalTime.of(10, 0), null);

        List<Schedule> ordered = service.findByPlanId(plan.getId());

        assertThat(ordered).extracting(Schedule::getPlace)
                .containsExactly("A-오전", "A-오후", "B-오전", "B-오후");
    }

    @Test
    void findById_throws_when_missing() {
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

    @Test
    void update_changes_fields() {
        Plan plan = planRepository.save(new Plan(
                "교토", "교토",
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5),
                null, null));
        Schedule saved = service.create(plan, LocalDate.of(2026, 8, 2), "기요미즈데라", LocalTime.of(10, 0), "초안");

        ScheduleForm form = new ScheduleForm();
        form.setDate(LocalDate.of(2026, 8, 3));
        form.setPlace("후시미이나리");
        form.setStartTime(LocalTime.of(13, 0));
        form.setNote("변경됨");
        service.update(saved.getId(), form);

        Schedule updated = service.findById(saved.getId());
        assertThat(updated.getPlace()).isEqualTo("후시미이나리");
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2026, 8, 3));
        assertThat(updated.getStartTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(updated.getNote()).isEqualTo("변경됨");
    }
}
