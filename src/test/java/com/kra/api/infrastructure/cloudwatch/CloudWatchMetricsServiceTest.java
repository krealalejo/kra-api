package com.kra.api.infrastructure.cloudwatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.GetMetricDataResponse;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudWatchMetricsServiceTest {

    @Mock
    private CloudWatchClient cloudWatchClient;

    private CloudWatchMetricsService service;

    @BeforeEach
    void setUp() {
        service = new CloudWatchMetricsService(cloudWatchClient);
    }

    @Test
    void getSystemMetrics_returnsResultsMappedById() {
        Instant ts = Instant.parse("2024-01-01T00:00:00Z");
        GetMetricDataResponse response = GetMetricDataResponse.builder()
                .metricDataResults(
                        MetricDataResult.builder()
                                .id("thumb_invocations")
                                .timestamps(ts)
                                .values(42.0)
                                .build(),
                        MetricDataResult.builder()
                                .id("email_errors")
                                .timestamps(List.of())
                                .values(List.of())
                                .build()
                )
                .build();
        when(cloudWatchClient.getMetricData(any(GetMetricDataRequest.class))).thenReturn(response);

        Map<String, Object> result = service.getSystemMetrics();

        assertThat(result).containsKey("thumb_invocations").containsKey("email_errors");

        @SuppressWarnings("unchecked")
        Map<String, Object> thumbData = (Map<String, Object>) result.get("thumb_invocations");
        assertThat(thumbData).containsKey("timestamps").containsKey("values");
    }

    @Test
    void getSystemMetrics_emptyResults_returnsEmptyMap() {
        GetMetricDataResponse response = GetMetricDataResponse.builder()
                .metricDataResults(List.of())
                .build();
        when(cloudWatchClient.getMetricData(any(GetMetricDataRequest.class))).thenReturn(response);

        Map<String, Object> result = service.getSystemMetrics();

        assertThat(result).isEmpty();
    }
}
