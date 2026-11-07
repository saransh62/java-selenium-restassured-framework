package com.saransh.automation.api;

import com.saransh.automation.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;

/**
 * Base for all resource clients. Holds one immutable RequestSpecification (base URI, Allure filter,
 * SLF4J logging) that every request starts from. RequestSpecifications built from it per call are
 * thread-confined, so clients are safe to use from parallel tests.
 */
public abstract class ApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(ApiClient.class);

    /** Logs one line per call; the full request/response goes into the Allure report instead. */
    private static final Filter LOGGING = (req, res, ctx) -> {
        var response = ctx.next(req, res);
        LOG.info("{} {} -> HTTP {} | {}", req.getMethod(), req.getURI(), response.getStatusCode(),
                abbreviate(response.asString()));
        return response;
    };

    private static final RequestSpecification BASE_SPEC = new RequestSpecBuilder()
            .setBaseUri(ConfigManager.apiBaseUrl())
            .setContentType(ContentType.URLENC.withCharset("UTF-8"))
            .addFilter(new AllureRestAssured())
            .addFilter(LOGGING)
            .build();

    protected RequestSpecification request() {
        return given().spec(BASE_SPEC);
    }

    private static String abbreviate(String body) {
        String oneLine = body == null ? "" : body.replaceAll("\\s+", " ");
        return oneLine.length() > 160 ? oneLine.substring(0, 160) + "..." : oneLine;
    }
}
