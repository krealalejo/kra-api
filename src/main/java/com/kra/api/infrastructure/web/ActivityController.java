package com.kra.api.infrastructure.web;

import com.kra.api.application.ActivityCardService;
import com.kra.api.infrastructure.web.dto.ActivityCardResponse;
import com.kra.api.infrastructure.web.dto.UpdateActivityCardRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    private static final List<String> VALID_TYPES = List.of("SHIPPING", "READING", "PLAYING");

    private final ActivityCardService activityCardService;

    public ActivityController(ActivityCardService activityCardService) {
        this.activityCardService = activityCardService;
    }

    @GetMapping
    public ResponseEntity<List<ActivityCardResponse>> getAll() {
        return ResponseEntity.ok(activityCardService.getAll());
    }

    @PutMapping("/{type}")
    public ResponseEntity<ActivityCardResponse> update(
            @PathVariable String type,
            @Valid @RequestBody UpdateActivityCardRequest request) {
        // T-25-02: Guard against arbitrary SK injection — only accept known card types
        if (!VALID_TYPES.contains(type.toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid activity card type. Must be one of: SHIPPING, READING, PLAYING");
        }
        ActivityCardResponse updated = activityCardService.update(
                type,
                request.getTitle(),
                request.getDescription(),
                request.getTags()
        );
        return ResponseEntity.ok(updated);
    }
}
