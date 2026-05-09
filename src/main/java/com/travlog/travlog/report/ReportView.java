package com.travlog.travlog.report;

import java.util.List;

public record ReportView(
        long planCount,
        long scheduleCount,
        long historyCount,
        long doneCount,
        long skippedCount,
        long changedCount,
        int donePercent,
        int skippedPercent,
        int changedPercent,
        List<MonthlyTripStat> monthly,
        List<PlanBudgetReport> budgets,
        List<DestinationStat> destinations
) {
}
