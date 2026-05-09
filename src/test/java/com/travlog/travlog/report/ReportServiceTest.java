package com.travlog.travlog.report;

import com.travlog.travlog.common.JpaAuditingConfig;
import com.travlog.travlog.history.History;
import com.travlog.travlog.history.HistoryRepository;
import com.travlog.travlog.history.HistoryStatus;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({ReportService.class, JpaAuditingConfig.class})
class ReportServiceTest {

    @Autowired
    PlanRepository planRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    ReportService service;

    private static final LocalDate TODAY = LocalDate.of(2026, 5, 9);

    @Test
    void empty_dataset_returns_zeros_and_twelve_empty_months() {
        ReportView view = service.buildReport(TODAY);

        assertThat(view.planCount()).isZero();
        assertThat(view.historyCount()).isZero();
        assertThat(view.donePercent()).isZero();
        assertThat(view.monthly()).hasSize(12);
        assertThat(view.monthly()).allMatch(m -> m.count() == 0 && m.barWidth() == 0);
        assertThat(view.budgets()).isEmpty();
        assertThat(view.destinations()).isEmpty();
    }

    @Test
    void monthly_window_covers_last_twelve_months_with_correct_counts() {
        savePlan("이번달 A", "X", TODAY.withDayOfMonth(5), 100_000L);
        savePlan("이번달 B", "X", TODAY.withDayOfMonth(15), 100_000L);
        savePlan("3개월 전", "X", TODAY.minusMonths(3).withDayOfMonth(1), 100_000L);
        savePlan("13개월 전 (윈도우 밖)", "X", TODAY.minusMonths(13).withDayOfMonth(1), 100_000L);

        ReportView view = service.buildReport(TODAY);

        assertThat(view.monthly()).hasSize(12);
        MonthlyTripStat current = view.monthly().get(11);
        assertThat(current.year()).isEqualTo(TODAY.getYear());
        assertThat(current.month()).isEqualTo(TODAY.getMonthValue());
        assertThat(current.count()).isEqualTo(2);
        assertThat(current.barWidth()).isEqualTo(100);

        MonthlyTripStat threeMonthsAgo = view.monthly().get(8);
        assertThat(threeMonthsAgo.count()).isEqualTo(1);
        assertThat(threeMonthsAgo.barWidth()).isEqualTo(50);

        long thirteenMonthsAgoCount = view.monthly().stream()
                .filter(m -> m.year() == TODAY.minusMonths(13).getYear()
                        && m.month() == TODAY.minusMonths(13).getMonthValue())
                .mapToLong(MonthlyTripStat::count).sum();
        assertThat(thirteenMonthsAgoCount).isZero();
    }

    @Test
    void status_breakdown_uses_percentages_summing_to_total() {
        Plan plan = savePlan("후쿠오카", "후쿠오카", TODAY.minusDays(10), 500_000L);
        Schedule s1 = saveSchedule(plan, TODAY.minusDays(10), "텐진");
        Schedule s2 = saveSchedule(plan, TODAY.minusDays(9), "하카타");
        Schedule s3 = saveSchedule(plan, TODAY.minusDays(8), "다자이후");
        Schedule s4 = saveSchedule(plan, TODAY.minusDays(7), "벳푸");
        saveHistory(s1, HistoryStatus.DONE, 100_000L);
        saveHistory(s2, HistoryStatus.DONE, 80_000L);
        saveHistory(s3, HistoryStatus.SKIPPED, null);
        saveHistory(s4, HistoryStatus.CHANGED, 50_000L);

        ReportView view = service.buildReport(TODAY);

        assertThat(view.historyCount()).isEqualTo(4);
        assertThat(view.doneCount()).isEqualTo(2);
        assertThat(view.skippedCount()).isEqualTo(1);
        assertThat(view.changedCount()).isEqualTo(1);
        assertThat(view.donePercent()).isEqualTo(50);
        assertThat(view.skippedPercent()).isEqualTo(25);
        assertThat(view.changedPercent()).isEqualTo(25);
    }

    @Test
    void budget_report_aggregates_actual_cost_per_plan_and_flags_overrun() {
        Plan a = savePlan("계획A", "도쿄", TODAY.minusDays(5), 200_000L);
        Plan b = savePlan("계획B (예산초과)", "오사카", TODAY.minusDays(3), 100_000L);
        Plan c = savePlan("계획C (예산없음)", "교토", TODAY.minusDays(1), null);
        saveHistory(saveSchedule(a, TODAY.minusDays(5), "p"), HistoryStatus.DONE, 100_000L);
        saveHistory(saveSchedule(a, TODAY.minusDays(4), "p"), HistoryStatus.DONE, 50_000L);
        saveHistory(saveSchedule(b, TODAY.minusDays(3), "p"), HistoryStatus.DONE, 150_000L);

        ReportView view = service.buildReport(TODAY);

        assertThat(view.budgets()).hasSize(3);
        PlanBudgetReport ra = view.budgets().stream().filter(r -> r.planId().equals(a.getId())).findFirst().orElseThrow();
        assertThat(ra.actualSum()).isEqualTo(150_000L);
        assertThat(ra.achievementRate()).isEqualTo(75);
        assertThat(ra.overBudget()).isFalse();
        assertThat(ra.remaining()).isEqualTo(50_000L);

        PlanBudgetReport rb = view.budgets().stream().filter(r -> r.planId().equals(b.getId())).findFirst().orElseThrow();
        assertThat(rb.overBudget()).isTrue();
        assertThat(rb.achievementRate()).isEqualTo(150);
        assertThat(rb.remaining()).isEqualTo(-50_000L);

        PlanBudgetReport rc = view.budgets().stream().filter(r -> r.planId().equals(c.getId())).findFirst().orElseThrow();
        assertThat(rc.hasBudget()).isFalse();
        assertThat(rc.achievementRate()).isNull();
        assertThat(rc.remaining()).isNull();
        assertThat(rc.actualSum()).isZero();
    }

    @Test
    void destinations_returns_top_five_descending() {
        for (int i = 0; i < 3; i++) savePlan("도쿄 " + i, "도쿄", TODAY.minusDays(30 + i), null);
        for (int i = 0; i < 2; i++) savePlan("부산 " + i, "부산", TODAY.minusDays(20 + i), null);
        savePlan("제주", "제주", TODAY.minusDays(10), null);
        savePlan("강릉", "강릉", TODAY.minusDays(8), null);
        savePlan("여수", "여수", TODAY.minusDays(6), null);
        savePlan("속초", "속초", TODAY.minusDays(4), null);

        ReportView view = service.buildReport(TODAY);

        assertThat(view.destinations()).hasSize(5);
        assertThat(view.destinations().get(0).destination()).isEqualTo("도쿄");
        assertThat(view.destinations().get(0).count()).isEqualTo(3);
        assertThat(view.destinations().get(1).destination()).isEqualTo("부산");
        assertThat(view.destinations().get(1).count()).isEqualTo(2);
    }

    private Plan savePlan(String title, String destination, LocalDate startDate, Long budget) {
        return planRepository.save(new Plan(title, destination, startDate, startDate.plusDays(1), budget, null));
    }

    private Schedule saveSchedule(Plan plan, LocalDate date, String place) {
        return scheduleRepository.save(new Schedule(plan, date, place, LocalTime.of(10, 0), null));
    }

    private void saveHistory(Schedule schedule, HistoryStatus status, Long actualCost) {
        historyRepository.save(new History(schedule, status, schedule.getPlace(), LocalTime.of(10, 0), actualCost, null));
    }
}
