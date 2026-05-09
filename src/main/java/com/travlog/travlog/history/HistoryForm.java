package com.travlog.travlog.history;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Setter
public class HistoryForm {

    @NotNull(message = "상태를 선택해 주세요")
    private HistoryStatus status = HistoryStatus.DONE;

    @Size(max = 100, message = "장소는 100자 이하로 입력해 주세요")
    private String actualPlace;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime actualTime;

    @Size(max = 1000, message = "후기는 1000자 이하로 입력해 주세요")
    private String review;

    public static HistoryForm from(History history) {
        HistoryForm form = new HistoryForm();
        if (history != null) {
            form.status = history.getStatus();
            form.actualPlace = history.getActualPlace();
            form.actualTime = history.getActualTime();
            form.review = history.getReview();
        }
        return form;
    }

    public boolean isDone() {
        return status == HistoryStatus.DONE;
    }

    public boolean isSkipped() {
        return status == HistoryStatus.SKIPPED;
    }

    public boolean isChanged() {
        return status == HistoryStatus.CHANGED;
    }
}
