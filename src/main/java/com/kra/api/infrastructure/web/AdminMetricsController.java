package com.kra.api.infrastructure.web;

import com.kra.api.infrastructure.cloudwatch.CloudWatchMetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminMetricsController {

    private final CloudWatchMetricsService metricsService;

    public AdminMetricsController(CloudWatchMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/admin/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        return ResponseEntity.ok(metricsService.getSystemMetrics());
    }
}
