package com.travlog.travlog.plan;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class PlanForm {

    @NotBlank(message = "제목을 입력해 주세요")
    @Size(max = 100, message = "제목은 100자 이하로 입력해 주세요")
    private String title;

    @NotBlank(message = "목적지를 입력해 주세요")
    @Size(max = 100, message = "목적지는 100자 이하로 입력해 주세요")
    private String destination;

    @NotNull(message = "출발일을 입력해 주세요")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "도착일을 입력해 주세요")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @PositiveOrZero(message = "예산은 0 이상이어야 합니다")
    private Long budget;

    @Size(max = 1000, message = "메모는 1000자 이하로 입력해 주세요")
    private String memo;

    @AssertTrue(message = "도착일은 출발일과 같거나 이후여야 합니다")
    public boolean isValidPeriod() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !endDate.isBefore(startDate);
    }

    public Plan toEntity() {
        return new Plan(title, destination, startDate, endDate, budget, memo);
    }

    public static PlanForm from(Plan plan) {
        PlanForm form = new PlanForm();
        form.title = plan.getTitle();
        form.destination = plan.getDestination();
        form.startDate = plan.getStartDate();
        form.endDate = plan.getEndDate();
        form.budget = plan.getBudget();
        form.memo = plan.getMemo();
        return form;
    }
}
