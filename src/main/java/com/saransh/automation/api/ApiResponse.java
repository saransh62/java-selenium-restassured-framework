package com.saransh.automation.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

/**
 * Wraps a REST Assured response for this API's quirks: the HTTP status is always 200, the real
 * status lives in the JSON field "responseCode", and the body is served as text/html. So we parse
 * the raw body as JSON ourselves instead of trusting the Content-Type header.
 */
public final class ApiResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final Response raw;
    private final String body;
    private JsonNode json;

    public ApiResponse(Response raw) {
        this.raw = raw;
        this.body = raw.asString();
    }

    public int httpStatus() {
        return raw.getStatusCode();
    }

    public String body() {
        return body;
    }

    public JsonNode json() {
        if (json == null) {
            try {
                json = MAPPER.readTree(body);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Response body is not valid JSON: " + body, e);
            }
        }
        return json;
    }

    public int responseCode() {
        return json().path("responseCode").asInt(-1);
    }

    public String message() {
        return json().path("message").asText(null);
    }

    public <T> T as(Class<T> type) {
        try {
            return MAPPER.readValue(body, type);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot map body to " + type.getSimpleName() + ": " + body, e);
        }
    }

    public Response raw() {
        return raw;
    }
}
