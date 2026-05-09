package com.travlog.travlog.schedule;

import com.travlog.travlog.common.BaseEntity;
import com.travlog.travlog.plan.Plan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Plan plan;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 100)
    private String place;

    private LocalTime startTime;

    @Column(length = 500)
    private String note;

    public Schedule(Plan plan, LocalDate date, String place, LocalTime startTime, String note) {
        this.plan = plan;
        this.date = date;
        this.place = place;
        this.startTime = startTime;
        this.note = note;
    }

    public void update(LocalDate date, String place, LocalTime startTime, String note) {
        this.date = date;
        this.place = place;
        this.startTime = startTime;
        this.note = note;
    }
}
