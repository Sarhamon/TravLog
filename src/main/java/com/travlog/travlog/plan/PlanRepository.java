package com.travlog.travlog.plan;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    List<Plan> findAllByOrderByStartDateDesc();

    List<Plan> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(LocalDate from, LocalDate to);

    List<Plan> findByStartDateAfterOrderByStartDateAsc(LocalDate today, Limit limit);

    List<Plan> findByEndDateBeforeOrderByEndDateDesc(LocalDate today, Limit limit);
}
