package com.saransh.automation.api;

import com.saransh.automation.api.models.BrandsResponse;
import com.saransh.automation.api.models.ProductsResponse;
import com.saransh.automation.base.BaseApiTest;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Brands API")
public class BrandsApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke", "regression"}, description = "GET brandsList returns all brands")
    public void getAllBrands() {
        ApiResponse res = brands.getAllBrands();

        assertThat(res.responseCode()).isEqualTo(200);
        BrandsResponse body = res.as(BrandsResponse.class);
        assertThat(body.brands).isNotEmpty();
        assertThat(body.brands).extracting(b -> b.brand).contains("Polo", "H&M", "Madame");
    }

    @Test(groups = {"api", "regression"}, description = "brandsList response matches its JSON schema")
    public void brandsListMatchesSchema() {
        assertMatchesSchema(brands.getAllBrands(), "brands-list.schema.json");
    }

    @Test(groups = {"api", "regression"}, description = "Every product brand exists in brandsList")
    public void productBrandsAreKnownBrands() {
        var knownBrands = brands.getAllBrands().as(BrandsResponse.class).brands.stream()
                .map(b -> b.brand).toList();
        var productBrands = products.getAllProducts()
                .as(ProductsResponse.class).products.stream()
                .map(p -> p.brand).distinct().toList();

        assertThat(knownBrands).containsAll(productBrands);
    }

    @Test(groups = {"api", "regression"}, description = "PUT to brandsList is rejected with 405")
    public void putToBrandsListNotSupported() {
        ApiResponse res = brands.putToBrandsList();

        assertThat(res.responseCode()).isEqualTo(405);
        assertThat(res.message()).isEqualTo("This request method is not supported.");
    }
}
