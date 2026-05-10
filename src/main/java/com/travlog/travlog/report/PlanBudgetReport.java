package com.travlog.travlog.report;

import com.travlog.travlog.common.Money;

public record PlanBudgetReport(
        Long planId,
        String title,
        String destination,
        Long budget,
        long actualSum,
        Integer achievementRate,
        boolean overBudget
) {

    public boolean hasBudget() {
        return budget != null;
    }

    public Long remaining() {
        return budget == null ? null : budget - actualSum;
    }

    public String budgetFormatted() {
        return Money.format(budget);
    }

    public String actualSumFormatted() {
        return Money.format(actualSum);
    }

    public String remainingFormatted() {
        return Money.format(remaining());
    }

    public static PlanBudgetReport of(Long planId, String title, String destination, Long budget, long actualSum) {
        Integer rate = null;
        boolean over = false;
        if (budget != null && budget > 0) {
            rate = (int) Math.round(actualSum * 100.0 / budget);
            over = actualSum > budget;
        }
        return new PlanBudgetReport(planId, title, destination, budget, actualSum, rate, over);
    }
}
