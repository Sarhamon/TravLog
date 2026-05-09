package com.travlog.travlog.dashboard;

import com.travlog.travlog.plan.Plan;

import java.util.List;

public record DashboardView(
        long planCount,
        long scheduleCount,
        long historyCount,
        long doneCount,
        long skippedCount,
        long changedCount,
        List<Plan> ongoing,
        List<Plan> upcoming,
        List<Plan> past
) {
}
