package com.saransh.automation.api;

import com.saransh.automation.api.models.ProductsResponse;
import com.saransh.automation.base.BaseApiTest;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Products API")
public class ProductsApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke", "regression"}, description = "GET productsList returns all products")
    public void getAllProducts() {
        ApiResponse res = products.getAllProducts();

        assertThat(res.httpStatus()).isEqualTo(200);
        assertThat(res.responseCode()).isEqualTo(200);
        ProductsResponse body = res.as(ProductsResponse.class);
        assertThat(body.products).isNotEmpty();
        assertThat(body.products).allSatisfy(p -> {
            assertThat(p.id).isPositive();
            assertThat(p.name).isNotBlank();
            assertThat(p.price).startsWith("Rs.");
        });
    }
}
