package com.travlog.travlog.plan;

import com.travlog.travlog.common.FormErrors;
import com.travlog.travlog.history.History;
import com.travlog.travlog.history.HistoryService;
import com.travlog.travlog.schedule.Schedule;
import com.travlog.travlog.schedule.ScheduleForm;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/plans")
public class PlanController {

    private final PlanService service;
    private final ScheduleService scheduleService;
    private final HistoryService historyService;

    @GetMapping
    public String list(@ModelAttribute("condition") PlanSearchCondition condition, Model model) {
        model.addAttribute("plans", service.search(condition));
        model.addAttribute("statuses", PlanStatusFilter.values());
        return "plan/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new PlanForm());
        addCreateFormAttributes(model);
        return "plan/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") PlanForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.of(bindingResult));
            addCreateFormAttributes(model);
            return "plan/form";
        }
        service.create(form.toEntity());
        return "redirect:/plans";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Plan plan = service.findById(id);
        model.addAttribute("form", PlanForm.from(plan));
        addEditFormAttributes(model, id);
        return "plan/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") PlanForm form,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", FormErrors.of(bindingResult));
            addEditFormAttributes(model, id);
            return "plan/form";
        }
        service.update(id, form);
        return "redirect:/plans/" + id;
    }

    private void addCreateFormAttributes(Model model) {
        model.addAttribute("heading", "새 여행 계획");
        model.addAttribute("action", "/plans");
        model.addAttribute("cancelUrl", "/plans");
    }

    private void addEditFormAttributes(Model model, Long id) {
        model.addAttribute("heading", "여행 계획 수정");
        model.addAttribute("action", "/plans/" + id);
        model.addAttribute("cancelUrl", "/plans/" + id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/plans";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Plan plan = service.findById(id);
        List<Schedule> schedules = scheduleService.findByPlanId(id);
        List<History> histories = historyService.findByPlanId(id);
        Map<Long, History> historyByScheduleId = histories.stream()
                .collect(Collectors.toMap(h -> h.getSchedule().getId(), Function.identity()));
        List<ScheduleHistoryView> scheduleViews = schedules.stream()
                .map(s -> new ScheduleHistoryView(s, historyByScheduleId.get(s.getId())))
                .toList();

        model.addAttribute("plan", plan);
        model.addAttribute("scheduleViews", scheduleViews);
        model.addAttribute("budgetSummary", BudgetSummary.of(plan, histories, schedules.size()));
        if (!model.containsAttribute("scheduleForm")) {
            model.addAttribute("scheduleForm", new ScheduleForm());
        }
        return "plan/detail";
    }
}
