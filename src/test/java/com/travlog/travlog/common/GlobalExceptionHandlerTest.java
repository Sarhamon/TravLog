package com.travlog.travlog.common;

import com.travlog.travlog.history.HistoryService;
import com.travlog.travlog.plan.PlanController;
import com.travlog.travlog.plan.PlanNotFoundException;
import com.travlog.travlog.plan.PlanService;
import com.travlog.travlog.schedule.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PlanController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PlanService planService;

    @MockitoBean
    ScheduleService scheduleService;

    @MockitoBean
    HistoryService historyService;

    @Test
    void plan_not_found_returns_404_view_with_message() throws Exception {
        when(planService.findById(999L)).thenThrow(new PlanNotFoundException(999L));

        var result = mockMvc.perform(get("/plans/999/edit"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"))
                .andExpect(model().attributeExists("message"))
                .andReturn();

        assertThat(result.getModelAndView().getModel().get("message").toString()).contains("999");
    }
}
