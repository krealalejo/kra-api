package com.kra.api.infrastructure.cloudwatch;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CloudWatchMetricsService {

    private static final String PROJECT_NAME = "kra";
    private static final String LAMBDA_NAMESPACE = "AWS/Lambda";
    private static final String FUNCTION_NAME_DIM = "FunctionName";
    private static final String THUMBNAIL_GENERATOR = PROJECT_NAME + "-thumbnail-generator";
    private static final String EMAIL_NOTIFIER = PROJECT_NAME + "-email-notifier";

    private final CloudWatchClient cloudWatchClient;

    public CloudWatchMetricsService() {
        this.cloudWatchClient = CloudWatchClient.builder()
                .region(Region.EU_WEST_1)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    public Map<String, Object> getSystemMetrics() {
        Instant endTime = Instant.now();
        Instant startTime = endTime.minus(24, ChronoUnit.HOURS);

        List<MetricDataQuery> queries = new ArrayList<>();

        queries.add(createQuery("thumb_invocations", LAMBDA_NAMESPACE, "Invocations", FUNCTION_NAME_DIM, THUMBNAIL_GENERATOR, "Sum"));
        queries.add(createQuery("thumb_errors", LAMBDA_NAMESPACE, "Errors", FUNCTION_NAME_DIM, THUMBNAIL_GENERATOR, "Sum"));
        queries.add(createQuery("thumb_duration", LAMBDA_NAMESPACE, "Duration", FUNCTION_NAME_DIM, THUMBNAIL_GENERATOR, "Average"));

        queries.add(createQuery("email_invocations", LAMBDA_NAMESPACE, "Invocations", FUNCTION_NAME_DIM, EMAIL_NOTIFIER, "Sum"));
        queries.add(createQuery("email_errors", LAMBDA_NAMESPACE, "Errors", FUNCTION_NAME_DIM, EMAIL_NOTIFIER, "Sum"));
        queries.add(createQuery("email_duration", LAMBDA_NAMESPACE, "Duration", FUNCTION_NAME_DIM, EMAIL_NOTIFIER, "Average"));

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
            data.put("timestamps", result.timestamps().stream().map(Instant::toString).toList());
            data.put("values", result.values());
            results.put(result.id(), data);
        }

        return results;
    }

    private MetricDataQuery createQuery(String id, String namespace, String metricName, String dimName, String dimValue, String stat) {
        return MetricDataQuery.builder()
                .id(id)
                .metricStat(ms -> ms
                        .metric(m -> m
                                .namespace(namespace)
                                .metricName(metricName)
                                .dimensions(Dimension.builder().name(dimName).value(dimValue).build()))
                        .period(3600)
                        .stat(stat))
                .returnData(true)
                .build();
    }
}
