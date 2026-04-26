package com.kra.api.infrastructure.web;

import com.kra.api.application.SkillCategoryService;
import com.kra.api.domain.model.SkillCategory;
import com.kra.api.infrastructure.web.dto.CreateSkillCategoryRequest;
import com.kra.api.infrastructure.web.dto.SkillCategoryResponse;
import com.kra.api.infrastructure.web.dto.UpdateSkillCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cv/skills/categories")
public class SkillCategoryController {

    private final SkillCategoryService skillCategoryService;

    public SkillCategoryController(SkillCategoryService skillCategoryService) {
        this.skillCategoryService = skillCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<SkillCategoryResponse>> list() {
        List<SkillCategoryResponse> items = skillCategoryService.findAll().stream()
                .map(SkillCategoryResponse::from)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<SkillCategoryResponse> create(@Valid @RequestBody CreateSkillCategoryRequest req) {
        SkillCategory cat = skillCategoryService.create(
                req.getName(), req.getSkills(), req.getSortOrder());
        return ResponseEntity.status(HttpStatus.CREATED).body(SkillCategoryResponse.from(cat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillCategoryResponse> update(@PathVariable String id,
            @RequestBody UpdateSkillCategoryRequest req) {
        SkillCategory cat = skillCategoryService.update(
                id, req.getName(), req.getSkills(), req.getSortOrder());
        return ResponseEntity.ok(SkillCategoryResponse.from(cat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        skillCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
