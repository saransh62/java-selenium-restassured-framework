package com.saransh.automation.api;

import com.saransh.automation.api.models.ProductsResponse;
import com.saransh.automation.base.BaseApiTest;
import com.saransh.automation.utils.JsonDataReader;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

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

    @Test(groups = {"api", "regression"}, description = "productsList response matches its JSON schema")
    public void productsListMatchesSchema() {
        assertMatchesSchema(products.getAllProducts(), "products-list.schema.json");
    }

    @Test(groups = {"api", "regression"}, description = "POST to productsList is rejected with 405")
    public void postToProductsListNotSupported() {
        ApiResponse res = products.postToProductsList();

        assertThat(res.responseCode()).isEqualTo(405);
        assertThat(res.message()).isEqualTo("This request method is not supported.");
        assertMatchesSchema(res, "error-message.schema.json");
    }

    @DataProvider(name = "searchTerms", parallel = true)
    public Object[][] searchTerms() {
        return JsonDataReader.asDataProvider("testdata/search-terms.json");
    }

    @Test(groups = {"api", "regression"}, dataProvider = "searchTerms",
            description = "searchProduct returns only matching products")
    public void searchProductReturnsMatches(Map<String, String> row) {
        String term = row.get("term");
        ApiResponse res = products.searchProduct(term);

        assertThat(res.responseCode()).isEqualTo(200);
        assertMatchesSchema(res, "products-list.schema.json");
        ProductsResponse body = res.as(ProductsResponse.class);
        assertThat(body.products).hasSizeGreaterThanOrEqualTo(Integer.parseInt(row.get("minResults")));
        assertThat(body.products).allSatisfy(p ->
                assertThat((p.name + " " + p.category.category).toLowerCase())
                        .as("product %s matches '%s'", p.name, term)
                        .contains(term));
    }

    @Test(groups = {"api", "regression"}, description = "searchProduct with an unknown term returns an empty list")
    public void searchProductNoMatches() {
        ApiResponse res = products.searchProduct("zzz-no-such-product");

        assertThat(res.responseCode()).isEqualTo(200);
        assertThat(res.as(ProductsResponse.class).products).isEmpty();
    }

    @Test(groups = {"api", "regression"}, description = "searchProduct without the parameter returns 400")
    public void searchProductMissingParam() {
        ApiResponse res = products.searchProductWithoutParam();

        assertThat(res.responseCode()).isEqualTo(400);
        assertThat(res.message()).isEqualTo("Bad request, search_product parameter is missing in POST request.");
    }
}
