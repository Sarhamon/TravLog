package com.travlog.travlog.plan;

import com.travlog.travlog.common.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({PlanService.class, JpaAuditingConfig.class})
class PlanServiceTest {

    @Autowired
    PlanService service;

    @Test
    void create_then_findById_returns_saved_plan() {
        Plan saved = service.create(new Plan(
                "도쿄여행", "도쿄",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5),
                1_000_000L, "메모"));

        Plan found = service.findById(saved.getId());

        assertThat(found.getTitle()).isEqualTo("도쿄여행");
        assertThat(found.getDestination()).isEqualTo("도쿄");
        assertThat(found.getBudget()).isEqualTo(1_000_000L);
        assertThat(found.getCreatedAt()).isNotNull();
    }

    @Test
    void findById_throws_when_missing() {
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(PlanNotFoundException.class);
    }

    @Test
    void update_changes_fields() {
        Plan saved = service.create(new Plan(
                "초안", "도쿄",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5),
                null, null));

        PlanForm form = new PlanForm();
        form.setTitle("도쿄여행 확정");
        form.setDestination("도쿄/요코하마");
        form.setStartDate(LocalDate.of(2026, 6, 2));
        form.setEndDate(LocalDate.of(2026, 6, 6));
        form.setBudget(2_000_000L);
        form.setMemo("호텔 예약 완료");
        service.update(saved.getId(), form);

        Plan updated = service.findById(saved.getId());
        assertThat(updated.getTitle()).isEqualTo("도쿄여행 확정");
        assertThat(updated.getDestination()).isEqualTo("도쿄/요코하마");
        assertThat(updated.getBudget()).isEqualTo(2_000_000L);
        assertThat(updated.getMemo()).isEqualTo("호텔 예약 완료");
    }

    @Test
    void delete_removes_plan() {
        Plan saved = service.create(new Plan(
                "삭제예정", "x",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 1),
                null, null));

        service.delete(saved.getId());

        assertThatThrownBy(() -> service.findById(saved.getId()))
                .isInstanceOf(PlanNotFoundException.class);
    }

    @Test
    void findAll_orders_by_startDate_desc() {
        service.create(new Plan("A", "x", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), null, null));
        service.create(new Plan("B", "x", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 2), null, null));
        service.create(new Plan("C", "x", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 2), null, null));

        List<Plan> plans = service.findAll();

        assertThat(plans).extracting(Plan::getTitle).containsExactly("B", "C", "A");
    }

    @Test
    void search_with_empty_condition_returns_all() {
        service.create(new Plan("A", "x", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2), null, null));
        service.create(new Plan("B", "y", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 2), null, null));

        assertThat(service.search(new PlanSearchCondition()))
                .extracting(Plan::getTitle).containsExactly("B", "A");
    }

    @Test
    void search_keyword_matches_title_or_destination_case_insensitive() {
        service.create(new Plan("도쿄여행", "Tokyo", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5), null, null));
        service.create(new Plan("부산여행", "Busan", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), null, null));
        service.create(new Plan("교토 일정", "Kyoto", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5), null, null));

        PlanSearchCondition condition = new PlanSearchCondition();
        condition.setKeyword("tokyo");

        assertThat(service.search(condition))
                .extracting(Plan::getTitle).containsExactly("도쿄여행");
    }

    @Test
    void search_filters_by_start_date_range() {
        service.create(new Plan("1월", "x", LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), null, null));
        service.create(new Plan("3월", "x", LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 12), null, null));
        service.create(new Plan("5월", "x", LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12), null, null));

        PlanSearchCondition condition = new PlanSearchCondition();
        condition.setStartFrom(LocalDate.of(2026, 2, 1));
        condition.setStartTo(LocalDate.of(2026, 4, 30));

        assertThat(service.search(condition))
                .extracting(Plan::getTitle).containsExactly("3월");
    }
}
