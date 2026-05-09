package com.travlog.travlog.dashboard;

import com.travlog.travlog.history.HistoryRepository;
import com.travlog.travlog.history.HistoryStatus;
import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanRepository;
import com.travlog.travlog.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private static final int RECENT_LIMIT = 5;

    private final PlanRepository planRepository;
    private final ScheduleRepository scheduleRepository;
    private final HistoryRepository historyRepository;

    @GetMapping("/")
    public String dashboard(Model model) {
        LocalDate today = LocalDate.now();
        Limit limit = Limit.of(RECENT_LIMIT);

        List<Plan> ongoing = planRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(today, today);
        List<Plan> upcoming = planRepository.findByStartDateAfterOrderByStartDateAsc(today, limit);
        List<Plan> past = planRepository.findByEndDateBeforeOrderByEndDateDesc(today, limit);

        DashboardView view = new DashboardView(
                planRepository.count(),
                scheduleRepository.count(),
                historyRepository.count(),
                historyRepository.countByStatus(HistoryStatus.DONE),
                historyRepository.countByStatus(HistoryStatus.SKIPPED),
                historyRepository.countByStatus(HistoryStatus.CHANGED),
                ongoing,
                upcoming,
                past
        );

        model.addAttribute("view", view);
        return "dashboard";
    }
}
