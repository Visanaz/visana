package com.visana.erp.core.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private static final String PROBLEM_DETAIL = "ProblemDetail";

    @Bean
    OpenAPI visanaOpenApi() {
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSchemas(PROBLEM_DETAIL, new ObjectSchema()
                        .addProperty("type", new StringSchema().format("uri"))
                        .addProperty("title", new StringSchema())
                        .addProperty("status", new io.swagger.v3.oas.models.media.IntegerSchema().format("int32"))
                        .addProperty("detail", new StringSchema())
                        .addProperty("instance", new StringSchema().format("uri"))
                        .addProperty("correlationId", new StringSchema()))
                .addResponses("UnauthorizedProblem", problemResponse("Authentication is required."))
                .addResponses("ForbiddenProblem", problemResponse("The authenticated actor is not allowed to perform this operation."))
                .addResponses("BadRequestProblem", problemResponse("The request is invalid."))
                .addResponses("NotFoundProblem", problemResponse("The requested resource was not found."))
                .addResponses("ConflictProblem", problemResponse("The request conflicts with the current resource state."))
                .addResponses("UnprocessableProblem", problemResponse("The request cannot be processed."))
                .addResponses("InternalServerProblem", problemResponse("An unexpected error occurred. Use the correlation ID for support."));

        return new OpenAPI()
                .info(new Info().title("VISANA Plan 3 API").version("v1"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    OpenApiCustomizer apiContractConventions() {
        return openApi -> {
            openApi.setServers(List.of());
            openApi.getComponents().addSchemas(PROBLEM_DETAIL, new ObjectSchema()
                    .addProperty("type", new StringSchema().format("uri"))
                    .addProperty("title", new StringSchema())
                    .addProperty("status", new io.swagger.v3.oas.models.media.IntegerSchema().format("int32"))
                    .addProperty("detail", new StringSchema())
                    .addProperty("instance", new StringSchema().format("uri"))
                    .addProperty("correlationId", new StringSchema()));
            openApi.getPaths().forEach((path, pathItem) ->
                    pathItem.readOperations().forEach(operation -> addConventions(path, operation)));
        };
    }

    private void addConventions(String path, Operation operation) {
        if (operation.getParameters() == null || operation.getParameters().stream()
                .noneMatch(parameter -> "X-Correlation-ID".equalsIgnoreCase(parameter.getName()))) {
            operation.addParametersItem(new Parameter()
                    .in("header")
                    .name("X-Correlation-ID")
                    .required(false)
                    .description("Optional caller correlation ID. The server generates one when absent or invalid.")
                    .schema(new StringSchema().maxLength(128)));
        }

        ApiResponses responses = operation.getResponses();
        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }
        addResponse(responses, "401", "#/components/responses/UnauthorizedProblem");
        addResponse(responses, "403", "#/components/responses/ForbiddenProblem");
        addResponse(responses, "500", "#/components/responses/InternalServerProblem");
        if (!"/api/v1/me".equals(path)) {
            addResponse(responses, "400", "#/components/responses/BadRequestProblem");
        }
        if (path.contains("{orderId}")) {
            addResponse(responses, "404", "#/components/responses/NotFoundProblem");
        }
        if (path.endsWith("/pay")) {
            addResponse(responses, "409", "#/components/responses/ConflictProblem");
        }
        responses.values().forEach(response -> {
            if (response.getHeaders() == null) {
                response.setHeaders(new java.util.LinkedHashMap<>());
            }
            response.getHeaders().putIfAbsent("X-Correlation-ID", new Header()
                    .description("Correlation ID generated or preserved by the server.")
                    .schema(new StringSchema()));
        });
    }

    private static ApiResponse problemResponse(String description) {
        return new ApiResponse().description(description)
                .content(new io.swagger.v3.oas.models.media.Content().addMediaType("application/problem+json",
                        new io.swagger.v3.oas.models.media.MediaType().schema(new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/" + PROBLEM_DETAIL))));
    }

    private static void addResponse(ApiResponses responses, String status, String reference) {
        responses.putIfAbsent(status, new ApiResponse().$ref(reference));
    }
}
