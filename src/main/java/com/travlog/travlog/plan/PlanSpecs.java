package com.travlog.travlog.plan;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public final class PlanSpecs {

    private PlanSpecs() {
    }

    public static Specification<Plan> keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return alwaysTrue();
        }
        String pattern = "%" + keyword.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("destination")), pattern)
        );
    }

    public static Specification<Plan> startDateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return alwaysTrue();
        }
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), from));
            }
            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("startDate"), to));
            }
            return predicate;
        };
    }

    public static Specification<Plan> hasStatus(PlanStatusFilter status, LocalDate today) {
        if (status == null) {
            return alwaysTrue();
        }
        return switch (status) {
            case ONGOING -> (root, query, cb) -> cb.and(
                    cb.lessThanOrEqualTo(root.get("startDate"), today),
                    cb.greaterThanOrEqualTo(root.get("endDate"), today)
            );
            case UPCOMING -> (root, query, cb) -> cb.greaterThan(root.get("startDate"), today);
            case PAST -> (root, query, cb) -> cb.lessThan(root.get("endDate"), today);
        };
    }

    private static Specification<Plan> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }
}
