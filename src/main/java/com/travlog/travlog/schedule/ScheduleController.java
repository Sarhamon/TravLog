package com.travlog.travlog.schedule;

import com.travlog.travlog.common.FormErrors;
import com.travlog.travlog.plan.Plan;
import com.travlog.travlog.plan.PlanService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plans/{planId}/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final PlanService planService;

    @PostMapping
    public String create(@PathVariable Long planId,
                         @Valid @ModelAttribute("scheduleForm") ScheduleForm form,
                         BindingResult bindingResult,
                         RedirectAttributes ra) {
        Plan plan = planService.findById(planId);
        rejectIfDateOutOfRange(form.getDate(), plan, bindingResult);

        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("scheduleErrors", FormErrors.of(bindingResult));
            ra.addFlashAttribute("scheduleForm", form);
            return "redirect:/plans/" + planId;
        }
        scheduleService.create(plan, form.getDate(), form.getPlace(), form.getStartTime(), form.getNote());
        return "redirect:/plans/" + planId;
    }

    @GetMapping("/{scheduleId}/edit")
    public String editForm(@PathVariable Long planId, @PathVariable Long scheduleId, Model model) {
        Schedule schedule = scheduleService.findById(scheduleId);
        model.addAttribute("planId", planId);
        model.addAttribute("schedule", schedule);
        model.addAttribute("form", ScheduleForm.from(schedule));
        return "schedule/edit";
    }

    @PostMapping("/{scheduleId}")
    public String update(@PathVariable Long planId,
                         @PathVariable Long scheduleId,
                         @Valid @ModelAttribute("form") ScheduleForm form,
                         BindingResult bindingResult,
                         Model model) {
        Plan plan = planService.findById(planId);
        rejectIfDateOutOfRange(form.getDate(), plan, bindingResult);

        if (bindingResult.hasErrors()) {
            Schedule schedule = scheduleService.findById(scheduleId);
            model.addAttribute("planId", planId);
            model.addAttribute("schedule", schedule);
            model.addAttribute("errors", FormErrors.of(bindingResult));
            return "schedule/edit";
        }
        scheduleService.update(scheduleId, form);
        return "redirect:/plans/" + planId;
    }

    private void rejectIfDateOutOfRange(LocalDate date, Plan plan, BindingResult bindingResult) {
        if (date == null) {
            return;
        }
        if (date.isBefore(plan.getStartDate()) || date.isAfter(plan.getEndDate())) {
            bindingResult.rejectValue("date", "date.outOfRange",
                    "계획 기간(" + plan.getStartDate() + " ~ " + plan.getEndDate() + ") 내의 날짜여야 합니다");
        }
    }

    @PostMapping("/{scheduleId}/delete")
    public String delete(@PathVariable Long planId, @PathVariable Long scheduleId) {
        scheduleService.delete(scheduleId);
        return "redirect:/plans/" + planId;
    }
}
