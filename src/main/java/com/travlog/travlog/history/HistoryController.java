package com.travlog.travlog.history;

import com.travlog.travlog.common.FormErrors;
import com.travlog.travlog.schedule.Schedule;
import com.travlog.travlog.schedule.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plans/{planId}/schedules/{scheduleId}/history")
public class HistoryController {

    private final HistoryService historyService;
    private final ScheduleService scheduleService;

    @GetMapping
    public String form(@PathVariable Long planId, @PathVariable Long scheduleId, Model model) {
        Schedule schedule = scheduleService.findById(scheduleId);
        History history = historyService.findByScheduleId(scheduleId).orElse(null);
        model.addAttribute("planId", planId);
        model.addAttribute("schedule", schedule);
        model.addAttribute("form", HistoryForm.from(history));
        return "history/form";
    }

    @PostMapping
    public String upsert(@PathVariable Long planId,
                         @PathVariable Long scheduleId,
                         @Valid @ModelAttribute("form") HistoryForm form,
                         BindingResult bindingResult,
                         Model model) {
        Schedule schedule = scheduleService.findById(scheduleId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("planId", planId);
            model.addAttribute("schedule", schedule);
            model.addAttribute("errors", FormErrors.of(bindingResult));
            return "history/form";
        }
        historyService.upsert(schedule, form.getStatus(), form.getActualPlace(), form.getActualTime(), form.getActualCost(), form.getReview());
        return "redirect:/plans/" + planId;
    }
}
