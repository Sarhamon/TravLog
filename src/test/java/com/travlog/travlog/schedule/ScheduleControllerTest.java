package com.travlog.travlog.schedule;

import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ScheduleService scheduleService;

    @MockitoBean
    PlanService planService;

    @Test
    void create_with_date_outside_plan_range_redirects_with_flash_error() throws Exception {
        Plan plan = new Plan(
                "도쿄여행", "도쿄",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5),
                null, null);
        when(planService.findById(1L)).thenReturn(plan);

        var result = mockMvc.perform(post("/plans/1/schedules")
                        .param("date", "2026-06-10")
                        .param("place", "디즈니랜드"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plans/1"))
                .andExpect(flash().attributeExists("scheduleErrors"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getFlashMap().get("scheduleErrors");
        assertThat(errors).containsKey("date");
        verify(scheduleService, times(0)).create(any(), any(), any(), any(), any());
    }

    @Test
    void create_with_blank_place_redirects_with_flash_error() throws Exception {
        Plan plan = new Plan(
                "도쿄여행", "도쿄",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5),
                null, null);
        when(planService.findById(1L)).thenReturn(plan);

        var result = mockMvc.perform(post("/plans/1/schedules")
                        .param("date", "2026-06-02")
                        .param("place", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plans/1"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getFlashMap().get("scheduleErrors");
        assertThat(errors).containsKey("place");
        verify(scheduleService, times(0)).create(any(), any(), any(), any(), any());
    }

    @Test
    void create_with_valid_input_calls_service_and_redirects() throws Exception {
        Plan plan = new Plan(
                "도쿄여행", "도쿄",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5),
                null, null);
        when(planService.findById(1L)).thenReturn(plan);

        mockMvc.perform(post("/plans/1/schedules")
                        .param("date", "2026-06-02")
                        .param("place", "디즈니랜드")
                        .param("startTime", "10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plans/1"));

        verify(scheduleService, times(1)).create(eq(plan), any(), eq("디즈니랜드"), any(), any());
    }
}
