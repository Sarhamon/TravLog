package com.travlog.travlog.plan;

import com.travlog.travlog.history.History;
import com.travlog.travlog.schedule.Schedule;

public record ScheduleHistoryView(Schedule schedule, History history) {
}
