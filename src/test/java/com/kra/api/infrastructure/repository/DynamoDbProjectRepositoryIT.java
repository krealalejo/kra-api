package com.kra.api.infrastructure.repository;

import com.kra.api.domain.model.Project;
import com.kra.api.domain.model.ProjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class DynamoDbProjectRepositoryIT {

    private static final int DYNAMODB_PORT = 8000;
    private static final String TABLE_NAME = "kra-table";

    @Container
    static GenericContainer<?> dynamodbLocal =
            new GenericContainer<>("amazon/dynamodb-local:latest")
                    .withExposedPorts(DYNAMODB_PORT);

    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbProjectRepository repository;

    @BeforeAll
    static void setUpInfrastructure() {
        String endpoint = "http://" + dynamodbLocal.getHost()
                + ":" + dynamodbLocal.getMappedPort(DYNAMODB_PORT);

        dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.EU_WEST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fake", "fake")))
                .build();

        createTable(dynamoDbClient);

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        repository = new DynamoDbProjectRepository(enhancedClient, TABLE_NAME);
    }

    private static void createTable(DynamoDbClient client) {
        client.createTable(CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("PK")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("SK")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("GSI1PK")
                                .attributeType(ScalarAttributeType.S)
                                .build()
                )
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName("PK")
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName("SK")
                                .keyType(KeyType.RANGE)
                                .build()
                )
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName("GSI1")
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("GSI1PK")
                                .keyType(KeyType.HASH)
                                .build())
                        .projection(Projection.builder()
                                .projectionType(ProjectionType.ALL)
                                .build())
                        .build())
                .build());
    }

    @BeforeEach
    void cleanTable() {
        dynamoDbClient.scan(r -> r.tableName(TABLE_NAME)).items().forEach(item ->
                dynamoDbClient.deleteItem(d -> d
                        .tableName(TABLE_NAME)
                        .key(java.util.Map.of(
                                "PK", item.get("PK"),
                                "SK", item.get("SK")))));
    }

    @Test
    void save_andFindById_roundTrip() {
        Project project = new Project(
                ProjectId.of("test-01"),
                "Test Project",
                "A test description",
                "https://example.com",
                "Some content"
        );

        repository.save(project);

        Optional<Project> found = repository.findById(ProjectId.of("test-01"));
        assertTrue(found.isPresent(), "Project should be present after save");
        assertEquals("Test Project", found.get().getTitle());
        assertEquals("A test description", found.get().getDescription());
        assertEquals("https://example.com", found.get().getUrl());
        assertEquals("Some content", found.get().getContent());
        assertEquals("test-01", found.get().getId().getValue());
    }

    @Test
    void findAll_returnsAllSavedProjects() {
        repository.save(new Project(ProjectId.of("p1"), "Alpha", null, null, null));
        repository.save(new Project(ProjectId.of("p2"), "Beta", null, null, null));

        List<Project> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<Project> result = repository.findById(ProjectId.of("does-not-exist"));
        assertTrue(result.isEmpty(), "Non-existent project should return empty Optional");
    }

    @Test
    void deleteById_removesItem() {
        ProjectId id = ProjectId.of("to-delete");
        repository.save(new Project(id, "Delete Me", null, null, null));

        repository.deleteById(id);

        assertTrue(repository.findById(id).isEmpty(), "Deleted project should not be found");
    }
}
