package com.kra.api.infrastructure.cloudwatch;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudWatchMetricsService {

    private final CloudWatchClient cloudWatchClient;
    private static final String PROJECT_NAME = "kra";

    public CloudWatchMetricsService() {
        this.cloudWatchClient = CloudWatchClient.builder()
                .region(Region.EU_WEST_1)
                .build();
    }

    public Map<String, Object> getSystemMetrics() {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(24, ChronoUnit.HOURS);

        List<MetricDataQuery> queries = new ArrayList<>();
        
        // Lambdas
        queries.add(createQuery("thumb_invocations", "AWS/Lambda", "Invocations", "FunctionName", PROJECT_NAME + "-thumbnail-generator", "Sum"));
        queries.add(createQuery("thumb_errors", "AWS/Lambda", "Errors", "FunctionName", PROJECT_NAME + "-thumbnail-generator", "Sum"));
        queries.add(createQuery("thumb_duration", "AWS/Lambda", "Duration", "FunctionName", PROJECT_NAME + "-thumbnail-generator", "Average"));
        
        queries.add(createQuery("email_invocations", "AWS/Lambda", "Invocations", "FunctionName", PROJECT_NAME + "-email-notifier", "Sum"));
        queries.add(createQuery("email_errors", "AWS/Lambda", "Errors", "FunctionName", PROJECT_NAME + "-email-notifier", "Sum"));
        queries.add(createQuery("email_duration", "AWS/Lambda", "Duration", "FunctionName", PROJECT_NAME + "-email-notifier", "Average"));
        
        // DynamoDB
        queries.add(createQuery("db_errors", "AWS/DynamoDB", "SystemErrors", "TableName", PROJECT_NAME + "-table", "Sum"));
        queries.add(createQuery("db_throttles", "AWS/DynamoDB", "ThrottledRequests", "TableName", PROJECT_NAME + "-table", "Sum"));

        GetMetricDataRequest request = GetMetricDataRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .metricDataQueries(queries)
                .build();

        GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

        Map<String, Object> results = new HashMap<>();
        for (MetricDataResult result : response.metricDataResults()) {
            Map<String, Object> data = new HashMap<>();
            data.put("timestamps", result.timestamps().stream().map(Instant::toString).collect(Collectors.toList()));
            data.put("values", result.values());
            results.put(result.id(), data);
        }

        return results;
    }

    private MetricDataQuery createQuery(String id, String namespace, String metricName, String dimName, String dimValue, String stat) {
        return MetricDataQuery.builder()
                .id(id)
                .metricStat(MetricStat.builder()
                        .metric(Metric.builder()
                                .namespace(namespace)
                                .metricName(metricName)
                                .dimensions(Dimension.builder().name(dimName).value(dimValue).build())
                                .build())
                        .period(3600)
                        .stat(stat)
                        .build())
                .returnData(true)
                .build();
    }
}
