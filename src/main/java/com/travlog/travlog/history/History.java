package com.travlog.travlog.history;

import com.travlog.travlog.common.BaseEntity;
import com.travlog.travlog.schedule.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

@Entity
@Table(name = "history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HistoryStatus status;

    @Column(length = 100)
    private String actualPlace;

    private LocalTime actualTime;

    private Long actualCost;

    @Column(length = 500)
    private String review;

    public History(Schedule schedule, HistoryStatus status, String actualPlace, LocalTime actualTime, Long actualCost, String review) {
        this.schedule = schedule;
        this.status = status;
        this.actualPlace = actualPlace;
        this.actualTime = actualTime;
        this.actualCost = actualCost;
        this.review = review;
    }

    public void update(HistoryStatus status, String actualPlace, LocalTime actualTime, Long actualCost, String review) {
        this.status = status;
        this.actualPlace = actualPlace;
        this.actualTime = actualTime;
        this.actualCost = actualCost;
        this.review = review;
    }
}
