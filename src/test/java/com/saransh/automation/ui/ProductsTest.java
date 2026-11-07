package com.saransh.automation.ui;

import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.HomePage;
import com.saransh.automation.pages.ProductDetailPage;
import com.saransh.automation.pages.ProductsPage;
import com.saransh.automation.utils.JsonDataReader;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Products")
public class ProductsTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke", "regression"}, description = "All Products page lists products")
    public void allProductsListed() {
        ProductsPage page = new HomePage(driver()).open().goToProducts();

        assertThat(page.heading()).isEqualToIgnoringCase("All Products");
        assertThat(page.productNames()).hasSizeGreaterThan(10).contains("Blue Top", "Men Tshirt");
    }

    @DataProvider(name = "searchTerms", parallel = true)
    public Object[][] searchTerms() {
        return JsonDataReader.asDataProvider("testdata/ui-search.json");
    }

    @Test(groups = {"ui", "regression"}, dataProvider = "searchTerms", description = "Search products")
    public void searchProducts(Map<String, String> row) {
        ProductsPage page = new ProductsPage(driver()).open().search(row.get("term"));

        assertThat(page.heading()).isEqualToIgnoringCase("Searched Products");
        assertThat(page.productNames()).isNotEmpty().contains(row.get("expectedProduct"));
    }

    @Test(groups = {"ui", "regression"}, description = "Product detail page shows full product information")
    public void productDetailShowsInformation() {
        ProductDetailPage detail = new ProductsPage(driver()).open().viewProduct(1);

        assertThat(detail.name()).isEqualTo("Blue Top");
        assertThat(detail.category()).isEqualTo("Category: Women > Tops");
        assertThat(detail.price()).isEqualTo("Rs. 500");
        assertThat(detail.availability()).contains("In Stock");
        assertThat(detail.condition()).contains("New");
        assertThat(detail.brand()).contains("Polo");
    }
}
