package com.saransh.automation.base;

import com.saransh.automation.api.AccountClient;
import com.saransh.automation.api.ApiResponse;
import com.saransh.automation.api.BrandsClient;
import com.saransh.automation.api.ProductsClient;
import io.qameta.allure.Step;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

/** Clients are stateless (every call builds its own request from a shared immutable spec). */
public abstract class BaseApiTest {

    protected final ProductsClient products = new ProductsClient();
    protected final BrandsClient brands = new BrandsClient();
    protected final AccountClient accounts = new AccountClient();

    /** Validates the raw body against a schema in src/test/resources/schemas (ignores the text/html content type). */
    @Step("Validate response against schema {schemaFile}")
    protected void assertMatchesSchema(ApiResponse response, String schemaFile) {
        assertThat(response.body(), matchesJsonSchemaInClasspath("schemas/" + schemaFile));
    }
}
