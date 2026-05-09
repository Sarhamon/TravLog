package com.travlog.travlog.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleForm {

    @NotNull(message = "날짜를 입력해 주세요")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotBlank(message = "장소를 입력해 주세요")
    @Size(max = 100, message = "장소는 100자 이하로 입력해 주세요")
    private String place;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Size(max = 500, message = "메모는 500자 이하로 입력해 주세요")
    private String note;

    public static ScheduleForm from(Schedule schedule) {
        ScheduleForm form = new ScheduleForm();
        form.date = schedule.getDate();
        form.place = schedule.getPlace();
        form.startTime = schedule.getStartTime();
        form.note = schedule.getNote();
        return form;
    }
}
