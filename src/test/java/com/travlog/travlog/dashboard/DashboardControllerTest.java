package com.travlog.travlog.dashboard;

import com.travlog.travlog.history.HistoryRepository;
import com.travlog.travlog.history.HistoryStatus;
import com.travlog.travlog.plan.PlanRepository;
import com.travlog.travlog.schedule.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlanRepository planRepository;

    @MockitoBean
    ScheduleRepository scheduleRepository;

    @MockitoBean
    HistoryRepository historyRepository;

    @Test
    void renders_dashboard_with_aggregated_view() throws Exception {
        when(planRepository.count()).thenReturn(3L);
        when(scheduleRepository.count()).thenReturn(7L);
        when(historyRepository.count()).thenReturn(2L);
        when(historyRepository.countByStatus(HistoryStatus.DONE)).thenReturn(1L);
        when(historyRepository.countByStatus(HistoryStatus.SKIPPED)).thenReturn(1L);
        when(historyRepository.countByStatus(HistoryStatus.CHANGED)).thenReturn(0L);
        when(planRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(any(), any()))
                .thenReturn(List.of());
        when(planRepository.findByStartDateAfterOrderByStartDateAsc(any(), any())).thenReturn(List.of());
        when(planRepository.findByEndDateBeforeOrderByEndDateDesc(any(), any())).thenReturn(List.of());

        var result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("view"))
                .andReturn();

        DashboardView view = (DashboardView) result.getModelAndView().getModel().get("view");
        assertThat(view.planCount()).isEqualTo(3L);
        assertThat(view.scheduleCount()).isEqualTo(7L);
        assertThat(view.historyCount()).isEqualTo(2L);
        assertThat(view.doneCount()).isEqualTo(1L);
        assertThat(view.skippedCount()).isEqualTo(1L);
        assertThat(view.changedCount()).isEqualTo(0L);
    }
}
