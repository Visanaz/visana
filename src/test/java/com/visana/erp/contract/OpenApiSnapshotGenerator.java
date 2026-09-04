package com.visana.erp.contract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/** Generates the reviewed JSON snapshot from a locally running, ephemeral Spring Boot instance. */
public final class OpenApiSnapshotGenerator {
    private static final ObjectMapper JSON = new ObjectMapper();

    private OpenApiSnapshotGenerator() {
    }

    public static void main(String[] args) throws Exception {
        Path output = Path.of(args.length == 0 ? "openapi/visana-api-v1.json" : args[0]).toAbsolutePath().normalize();
        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(OpenApiRuntimeApplication.class)
                .properties(
                        "server.port=0",
                        "spring.main.banner-mode=off",
                        "logging.level.root=WARN")
                .run()) {
            int port = ((WebServerApplicationContext) context).getWebServer().getPort();
            HttpResponse<String> response = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()
                    .send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/v3/api-docs"))
                            .timeout(Duration.ofSeconds(20)).GET().build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Springdoc returned HTTP " + response.statusCode() + ": " + response.body());
            }
            JsonNode document = JSON.readTree(response.body());
            ((ObjectNode) document).remove("servers");
            Files.createDirectories(output.getParent());
            JSON.writerWithDefaultPrettyPrinter().writeValue(output.toFile(), sort(document));
            validate(document);
        }
    }

    private static void validate(JsonNode document) {
        requireText(document, "openapi");
        requireText(document.path("info"), "title");
        requireText(document.path("info"), "version");
        if (!document.path("components").path("schemas").has("ProblemDetail")) {
            throw new IllegalStateException("ProblemDetail schema is missing; schemas=" + document.path("components").path("schemas").fieldNames().next());
        }
        JsonNode security = document.path("components").path("securitySchemes").path("bearerAuth");
        if (!"http".equals(security.path("type").asText()) || !"bearer".equals(security.path("scheme").asText())) {
            throw new IllegalStateException("bearerAuth security scheme is missing or invalid");
        }
        Set<String> operationIds = new HashSet<>();
        Iterator<Map.Entry<String, JsonNode>> paths = document.path("paths").fields();
        while (paths.hasNext()) {
            Map.Entry<String, JsonNode> path = paths.next();
            Iterator<Map.Entry<String, JsonNode>> operations = path.getValue().fields();
            while (operations.hasNext()) {
                Map.Entry<String, JsonNode> operation = operations.next();
                if (!Set.of("get", "post", "put", "patch", "delete", "head", "options", "trace").contains(operation.getKey())) {
                    continue;
                }
                String operationId = requireText(operation.getValue(), "operationId");
                if (!operationIds.add(operationId)) {
                    throw new IllegalStateException("Duplicate operationId: " + operationId);
                }
            }
        }
    }

    private static String requireText(JsonNode node, String name) {
        String value = node.path(name).asText();
        if (value.isBlank()) {
            throw new IllegalStateException("Required OpenAPI value is missing: " + name);
        }
        return value;
    }

    private static JsonNode sort(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sorted = JSON.createObjectNode();
            TreeMap<String, JsonNode> fields = new TreeMap<>();
            node.fields().forEachRemaining(entry -> fields.put(entry.getKey(), entry.getValue()));
            fields.forEach((key, value) -> sorted.set(key, sort(value)));
            return sorted;
        }
        if (node.isArray()) {
            ArrayNode sorted = JSON.createArrayNode();
            node.forEach(value -> sorted.add(sort(value)));
            return sorted;
        }
        return node;
    }
}
