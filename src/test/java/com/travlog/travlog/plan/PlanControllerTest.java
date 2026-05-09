package com.travlog.travlog.plan;

import com.travlog.travlog.history.HistoryService;
import com.travlog.travlog.schedule.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlanService planService;

    @MockitoBean
    ScheduleService scheduleService;

    @MockitoBean
    HistoryService historyService;

    @Test
    void create_with_blank_title_rerenders_form_with_field_error() throws Exception {
        var result = mockMvc.perform(post("/plans")
                        .param("title", "")
                        .param("destination", "도쿄")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/form"))
                .andExpect(model().attributeExists("errors"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getModelAndView().getModel().get("errors");
        assertThat(errors).containsKey("title");
        verify(planService, times(0)).create(any());
    }

    @Test
    void create_with_endDate_before_startDate_shows_global_error() throws Exception {
        var result = mockMvc.perform(post("/plans")
                        .param("title", "도쿄여행")
                        .param("destination", "도쿄")
                        .param("startDate", "2026-06-10")
                        .param("endDate", "2026-06-05"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/form"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.getModelAndView().getModel().get("errors");
        assertThat(errors).containsKey("global");
        verify(planService, times(0)).create(any());
    }

    @Test
    void create_with_valid_input_redirects_to_list() throws Exception {
        mockMvc.perform(post("/plans")
                        .param("title", "도쿄여행")
                        .param("destination", "도쿄")
                        .param("startDate", "2026-06-01")
                        .param("endDate", "2026-06-05")
                        .param("budget", "1000000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plans"));

        verify(planService, times(1)).create(any());
    }
}
