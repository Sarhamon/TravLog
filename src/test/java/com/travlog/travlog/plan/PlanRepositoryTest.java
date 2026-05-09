package com.travlog.travlog.plan;

import com.travlog.travlog.common.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Limit;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class PlanRepositoryTest {

    @Autowired
    PlanRepository repository;

    @Test
    void classifies_plans_by_reference_date() {
        LocalDate today = LocalDate.of(2026, 6, 15);
        repository.save(new Plan("진행중", "x", today.minusDays(1), today.plusDays(1), null, null));
        repository.save(new Plan("다가올1", "x", today.plusDays(2), today.plusDays(3), null, null));
        repository.save(new Plan("다가올2", "x", today.plusDays(7), today.plusDays(10), null, null));
        repository.save(new Plan("다녀온", "x", today.minusDays(10), today.minusDays(5), null, null));

        assertThat(repository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(today, today))
                .extracting(Plan::getTitle).containsExactly("진행중");

        assertThat(repository.findByStartDateAfterOrderByStartDateAsc(today, Limit.of(5)))
                .extracting(Plan::getTitle).containsExactly("다가올1", "다가올2");

        assertThat(repository.findByEndDateBeforeOrderByEndDateDesc(today, Limit.of(5)))
                .extracting(Plan::getTitle).containsExactly("다녀온");
    }
}
