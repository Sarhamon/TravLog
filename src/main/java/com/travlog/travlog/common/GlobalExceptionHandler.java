package com.travlog.travlog.common;

import com.travlog.travlog.plan.PlanNotFoundException;
import com.travlog.travlog.schedule.ScheduleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({PlanNotFoundException.class, ScheduleNotFoundException.class})
    public String handleNotFound(RuntimeException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }
}
