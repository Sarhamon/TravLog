package com.travlog.travlog.common;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class FormErrors {

    private static final Set<String> GLOBAL_FIELDS = Set.of("validPeriod");

    private FormErrors() {
    }

    public static Map<String, String> of(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : bindingResult.getFieldErrors()) {
            String key = GLOBAL_FIELDS.contains(fe.getField()) ? "global" : fe.getField();
            errors.putIfAbsent(key, fe.getDefaultMessage());
        }
        for (ObjectError oe : bindingResult.getGlobalErrors()) {
            errors.putIfAbsent("global", oe.getDefaultMessage());
        }
        return errors;
    }
}
