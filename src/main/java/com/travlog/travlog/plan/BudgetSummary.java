package com.travlog.travlog.plan;

import com.travlog.travlog.common.Money;
import com.travlog.travlog.history.History;

import java.util.List;

public record BudgetSummary(Long budget, long actualSum, int actualCount, int totalSchedules) {

    public static BudgetSummary of(Plan plan, List<History> histories, int totalSchedules) {
        long sum = 0L;
        int count = 0;
        for (History h : histories) {
            if (h.getActualCost() != null) {
                sum += h.getActualCost();
                count++;
            }
        }
        return new BudgetSummary(plan.getBudget(), sum, count, totalSchedules);
    }

    public boolean hasBudget() {
        return budget != null;
    }

    public Long remaining() {
        return budget == null ? null : budget - actualSum;
    }

    public boolean overBudget() {
        return budget != null && actualSum > budget;
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
}
