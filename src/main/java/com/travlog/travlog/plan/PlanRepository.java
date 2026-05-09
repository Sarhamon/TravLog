package com.travlog.travlog.plan;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {

    List<Plan> findAllByOrderByStartDateDesc();

    List<Plan> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(LocalDate from, LocalDate to);

    List<Plan> findByStartDateAfterOrderByStartDateAsc(LocalDate today, Limit limit);

    List<Plan> findByEndDateBeforeOrderByEndDateDesc(LocalDate today, Limit limit);

    @Query("""
            select extract(year from p.startDate),
                   extract(month from p.startDate),
                   count(p)
            from Plan p
            where p.startDate >= :since
            group by extract(year from p.startDate), extract(month from p.startDate)
            order by extract(year from p.startDate), extract(month from p.startDate)
            """)
    List<Object[]> countByMonthSince(@Param("since") LocalDate since);

    @Query("select p.destination, count(p) from Plan p group by p.destination order by count(p) desc")
    List<Object[]> countByDestination(Limit limit);
}
