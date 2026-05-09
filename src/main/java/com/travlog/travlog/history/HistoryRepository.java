package com.travlog.travlog.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Optional<History> findByScheduleId(Long scheduleId);

    @Query("SELECT h FROM History h JOIN FETCH h.schedule WHERE h.schedule.plan.id = :planId")
    List<History> findByPlanId(@Param("planId") Long planId);

    long countByStatus(HistoryStatus status);

    @Query("""
            select h.schedule.plan.id, coalesce(sum(h.actualCost), 0)
            from History h
            group by h.schedule.plan.id
            """)
    List<Object[]> sumActualCostByPlanId();
}
