package com.travlog.travlog.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("view", service.buildReport(LocalDate.now()));
        return "report/index";
    }
}
