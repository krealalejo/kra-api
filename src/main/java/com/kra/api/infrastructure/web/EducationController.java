package com.kra.api.infrastructure.web;

import com.kra.api.application.EducationService;
import com.kra.api.domain.model.Education;
import com.kra.api.infrastructure.web.dto.CreateEducationRequest;
import com.kra.api.infrastructure.web.dto.EducationResponse;
import com.kra.api.infrastructure.web.dto.UpdateEducationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cv/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public ResponseEntity<List<EducationResponse>> list() {
        List<EducationResponse> items = educationService.findAll().stream()
                .map(EducationResponse::from)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<EducationResponse> create(@Valid @RequestBody CreateEducationRequest req) {
        Education edu = educationService.create(
                req.getTitle(), req.getInstitution(), req.getLocation(),
                req.getYears(), req.getDescription(), req.getSortOrder());
        return ResponseEntity.status(HttpStatus.CREATED).body(EducationResponse.from(edu));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> update(@PathVariable String id,
            @RequestBody UpdateEducationRequest req) {
        Education edu = educationService.update(
                id, req.getTitle(), req.getInstitution(), req.getLocation(),
                req.getYears(), req.getDescription(), req.getSortOrder());
        return ResponseEntity.ok(EducationResponse.from(edu));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        educationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
