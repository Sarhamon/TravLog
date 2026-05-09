package com.travlog.travlog.plan;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Getter
@Setter
public class PlanSearchCondition {

    private String keyword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startTo;

    private PlanStatusFilter status;

    public boolean isEmpty() {
        return !StringUtils.hasText(keyword)
                && startFrom == null
                && startTo == null
                && status == null;
    }

    public boolean isOngoing() {
        return status == PlanStatusFilter.ONGOING;
    }

    public boolean isUpcoming() {
        return status == PlanStatusFilter.UPCOMING;
    }

    public boolean isPast() {
        return status == PlanStatusFilter.PAST;
    }
}
