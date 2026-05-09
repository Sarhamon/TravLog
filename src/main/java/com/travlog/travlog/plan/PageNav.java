package com.travlog.travlog.plan;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record PageNav(int current,
                      int totalPages,
                      long totalElements,
                      boolean hasPrevious,
                      boolean hasNext,
                      int previousPage,
                      int nextPage,
                      List<PageNumber> numbers) {

    private static final int WINDOW = 2;

    public record PageNumber(int number, int displayNumber, boolean active) {}

    public static PageNav of(Page<?> page) {
        int total = Math.max(page.getTotalPages(), 1);
        int current = page.getNumber();
        int start = Math.max(0, current - WINDOW);
        int end = Math.min(total - 1, current + WINDOW);
        List<PageNumber> numbers = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            numbers.add(new PageNumber(i, i + 1, i == current));
        }
        return new PageNav(
                current,
                total,
                page.getTotalElements(),
                page.hasPrevious(),
                page.hasNext(),
                Math.max(0, current - 1),
                Math.min(total - 1, current + 1),
                numbers
        );
    }

    public boolean isMultiPage() {
        return totalPages > 1;
    }
}
