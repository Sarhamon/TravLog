package com.travlog.travlog.plan;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlanNotFoundException extends RuntimeException {

    public PlanNotFoundException(Long id) {
        super("Plan not found: " + id);
    }
}
