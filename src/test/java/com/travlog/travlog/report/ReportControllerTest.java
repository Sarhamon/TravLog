package com.travlog.travlog.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ReportService reportService;

    @Test
    void index_renders_report_view_with_view_attribute() throws Exception {
        ReportView fake = new ReportView(0, 0, 0, 0, 0, 0, 0, 0, 0, List.of(), List.of(), List.of());
        when(reportService.buildReport(any(LocalDate.class))).thenReturn(fake);

        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("report/index"))
                .andExpect(model().attributeExists("view"));

        verify(reportService, times(1)).buildReport(any(LocalDate.class));
    }
}
