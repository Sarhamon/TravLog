package com.travlog.travlog.plan;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository repository;

    public List<Plan> findAll() {
        return repository.findAllByOrderByStartDateDesc();
    }

    public Page<Plan> search(PlanSearchCondition condition, Pageable pageable) {
        if (condition == null || condition.isEmpty()) {
            return repository.findAll(pageable);
        }
        Specification<Plan> spec = Specification.allOf(
                PlanSpecs.keywordContains(condition.getKeyword()),
                PlanSpecs.startDateBetween(condition.getStartFrom(), condition.getStartTo()),
                PlanSpecs.hasStatus(condition.getStatus(), LocalDate.now())
        );
        return repository.findAll(spec, pageable);
    }

    public Plan findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PlanNotFoundException(id));
    }

    @Transactional
    public Plan create(Plan plan) {
        return repository.save(plan);
    }

    @Transactional
    public void update(Long id, PlanForm form) {
        Plan plan = findById(id);
        plan.update(form.getTitle(), form.getDestination(),
                form.getStartDate(), form.getEndDate(),
                form.getBudget(), form.getMemo());
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }
}
