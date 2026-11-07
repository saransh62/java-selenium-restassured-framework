package com.saransh.automation.hybrid;

import com.saransh.automation.api.ApiResponse;
import com.saransh.automation.api.ProductsClient;
import com.saransh.automation.api.models.Product;
import com.saransh.automation.api.models.ProductsResponse;
import com.saransh.automation.api.models.UserAccount;
import com.saransh.automation.api.models.UserDetailResponse;
import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.CartItem;
import com.saransh.automation.pages.CartPage;
import com.saransh.automation.pages.CheckoutPage;
import com.saransh.automation.pages.HomePage;
import com.saransh.automation.pages.ProductsPage;
import com.saransh.automation.pages.SignupLoginPage;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The showcase: each test uses the API for what it is good at (fast setup, source of truth,
 * cleanup) and the browser only for the behaviour under test.
 */
@Feature("Hybrid UI + API")
public class HybridTest extends BaseUiTest {

    private final ProductsClient productsApi = new ProductsClient();

    @Test(groups = {"hybrid", "smoke", "regression"},
            description = "Account created via API can log in via UI, then is deleted via API")
    @Description("API create -> UI login -> header shows name -> API delete -> API confirms login no longer works")
    public void apiCreatedUserLogsInViaUi() {
        UserAccount user = TestDataFactory.newUser();
        assertThat(accountApi.createAccount(user).responseCode()).isEqualTo(201);
        try {
            HomePage home = new SignupLoginPage(driver()).open().login(user.email, user.password);
            assertThat(home.loggedInUsername()).isEqualTo(user.name);
        } finally {
            ApiResponse deleted = accountApi.deleteAccount(user.email, user.password);
            assertThat(deleted.responseCode()).isEqualTo(200);
        }
        assertThat(accountApi.verifyLogin(user.email, user.password).responseCode()).isEqualTo(404);
    }

    @Test(groups = {"hybrid", "regression"}, description = "User registered via UI is visible via API")
    public void uiRegisteredUserVisibleViaApi() {
        UserAccount user = TestDataFactory.newUser();
        SignupLoginPage page = new SignupLoginPage(driver()).open().startSignup(user.name, user.email);
        page.fillAccountInformation(user).continueToHome();
        deleteAfterTest(user);

        ApiResponse res = accountApi.getUserByEmail(user.email);

        assertThat(res.responseCode()).isEqualTo(200);
        UserDetailResponse.User details = res.as(UserDetailResponse.class).user;
        assertThat(details.name).isEqualTo(user.name);
        assertThat(details.first_name).isEqualTo(user.firstname);
        assertThat(details.address1).isEqualTo(user.address1);
        assertThat(details.zipcode).isEqualTo(user.zipcode);
    }

    @Test(groups = {"hybrid", "regression"},
            description = "Products added via UI appear in the cart with the catalog data from the API")
    public void cartMatchesApiCatalog() {
        List<Product> catalog = productsApi.getAllProducts().as(ProductsResponse.class).products;
        Product first = catalog.get(0);
        Product second = catalog.get(catalog.size() / 2);

        CartPage cart = new ProductsPage(driver()).open()
                .addToCartAndContinue(first.id)
                .addToCartAndViewCart(second.id);

        assertThat(cart.items()).containsExactly(
                new CartItem(first.id, first.name, first.price, 1, first.price),
                new CartItem(second.id, second.name, second.price, 1, second.price));
    }

    @Test(groups = {"hybrid", "regression"}, description = "UI search results match API search results")
    public void uiSearchMatchesApiSearch() {
        String term = "jeans";
        List<String> apiNames = productsApi.searchProduct(term).as(ProductsResponse.class).products.stream()
                .map(p -> p.name).toList();

        List<String> uiNames = new ProductsPage(driver()).open().search(term).productNames();

        assertThat(uiNames).containsExactlyInAnyOrderElementsOf(apiNames);
    }

    @Test(groups = {"hybrid", "regression"},
            description = "Address updated via API is shown at UI checkout")
    public void apiUpdatedAddressShownAtCheckout() {
        UserAccount user = createUserViaApi();
        user.address1 = "42 Updated Avenue";
        user.city = "Mysuru";
        assertThat(accountApi.updateAccount(user).responseCode()).isEqualTo(200);

        new SignupLoginPage(driver()).open().login(user.email, user.password);
        CheckoutPage checkout = new ProductsPage(driver()).open().addToCartAndViewCart(1).proceedToCheckout();

        assertThat(checkout.deliveryAddress())
                .contains("42 Updated Avenue")
                .anySatisfy(line -> assertThat(line).contains("Mysuru"));
    }
}
