package com.kra.api.infrastructure.web;

import com.kra.api.application.ExperienceService;
import com.kra.api.domain.model.Experience;
import com.kra.api.infrastructure.web.dto.CreateExperienceRequest;
import com.kra.api.infrastructure.web.dto.ExperienceResponse;
import com.kra.api.infrastructure.web.dto.UpdateExperienceRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cv/experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> list() {
        List<ExperienceResponse> items = experienceService.findAll().stream()
                .map(ExperienceResponse::from)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<ExperienceResponse> create(@Valid @RequestBody CreateExperienceRequest req) {
        Experience exp = experienceService.create(
                req.getTitle(), req.getCompany(), req.getLocation(),
                req.getYears(), req.getDescription(), req.getSortOrder());
        return ResponseEntity.status(HttpStatus.CREATED).body(ExperienceResponse.from(exp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExperienceResponse> update(@PathVariable String id,
            @RequestBody UpdateExperienceRequest req) {
        Experience exp = experienceService.update(
                id, req.getTitle(), req.getCompany(), req.getLocation(),
                req.getYears(), req.getDescription(), req.getSortOrder());
        return ResponseEntity.ok(ExperienceResponse.from(exp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        experienceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
