package com.travlog.travlog.report;

import com.travlog.travlog.history.HistoryRepository;
import com.travlog.travlog.history.HistoryStatus;
import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanRepository;
import com.travlog.travlog.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final int MONTHLY_WINDOW = 12;
    private static final int DESTINATION_TOP_N = 5;

    private final PlanRepository planRepository;
    private final ScheduleRepository scheduleRepository;
    private final HistoryRepository historyRepository;

    public ReportView buildReport(LocalDate today) {
        long planCount = planRepository.count();
        long scheduleCount = scheduleRepository.count();
        long historyCount = historyRepository.count();
        long doneCount = historyRepository.countByStatus(HistoryStatus.DONE);
        long skippedCount = historyRepository.countByStatus(HistoryStatus.SKIPPED);
        long changedCount = historyRepository.countByStatus(HistoryStatus.CHANGED);

        return new ReportView(
                planCount,
                scheduleCount,
                historyCount,
                doneCount,
                skippedCount,
                changedCount,
                percent(doneCount, historyCount),
                percent(skippedCount, historyCount),
                percent(changedCount, historyCount),
                buildMonthly(today),
                buildBudgets(),
                buildDestinations()
        );
    }

    private List<MonthlyTripStat> buildMonthly(LocalDate today) {
        YearMonth start = YearMonth.from(today).minusMonths(MONTHLY_WINDOW - 1L);
        LocalDate since = start.atDay(1);

        Map<YearMonth, Long> counts = new HashMap<>();
        for (Object[] row : planRepository.countByMonthSince(since)) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long count = ((Number) row[2]).longValue();
            counts.put(YearMonth.of(year, month), count);
        }

        long max = counts.values().stream().mapToLong(Long::longValue).max().orElse(0);
        List<MonthlyTripStat> result = new ArrayList<>(MONTHLY_WINDOW);
        for (int i = 0; i < MONTHLY_WINDOW; i++) {
            YearMonth ym = start.plusMonths(i);
            long count = counts.getOrDefault(ym, 0L);
            int barWidth = max == 0 ? 0 : (int) Math.round(count * 100.0 / max);
            result.add(new MonthlyTripStat(ym.getYear(), ym.getMonthValue(), count, barWidth));
        }
        return result;
    }

    private List<PlanBudgetReport> buildBudgets() {
        Map<Long, Long> actualByPlanId = new HashMap<>();
        for (Object[] row : historyRepository.sumActualCostByPlanId()) {
            Long planId = ((Number) row[0]).longValue();
            long sum = ((Number) row[1]).longValue();
            actualByPlanId.put(planId, sum);
        }

        List<Plan> plans = planRepository.findAllByOrderByStartDateDesc();
        List<PlanBudgetReport> result = new ArrayList<>(plans.size());
        for (Plan p : plans) {
            long actual = actualByPlanId.getOrDefault(p.getId(), 0L);
            result.add(PlanBudgetReport.of(p.getId(), p.getTitle(), p.getDestination(), p.getBudget(), actual));
        }
        return result;
    }

    private List<DestinationStat> buildDestinations() {
        List<DestinationStat> result = new ArrayList<>();
        for (Object[] row : planRepository.countByDestination(Limit.of(DESTINATION_TOP_N))) {
            String destination = (String) row[0];
            long count = ((Number) row[1]).longValue();
            result.add(new DestinationStat(destination, count));
        }
        return result;
    }

    private static int percent(long part, long total) {
        return total == 0 ? 0 : (int) Math.round(part * 100.0 / total);
    }
}
