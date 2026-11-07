package com.saransh.automation.ui;

import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.CartItem;
import com.saransh.automation.pages.CartPage;
import com.saransh.automation.pages.HomePage;
import com.saransh.automation.pages.ProductDetailPage;
import com.saransh.automation.pages.ProductsPage;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Cart")
public class CartTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke", "regression"}, description = "Add two products to the cart")
    public void addProductsToCart() {
        CartPage cart = new ProductsPage(driver()).open()
                .addToCartAndContinue(1)
                .addToCartAndViewCart(2);

        List<CartItem> items = cart.items();
        assertThat(items).extracting(CartItem::productId).containsExactly(1, 2);
        assertThat(items.get(0)).isEqualTo(new CartItem(1, "Blue Top", "Rs. 500", 1, "Rs. 500"));
        assertThat(items.get(1)).isEqualTo(new CartItem(2, "Men Tshirt", "Rs. 400", 1, "Rs. 400"));
    }

    @Test(groups = {"ui", "regression"}, description = "Remove a product from the cart")
    public void removeProductFromCart() {
        CartPage cart = new ProductsPage(driver()).open().addToCartAndViewCart(3);
        assertThat(cart.items()).extracting(CartItem::productId).containsExactly(3);

        cart.remove(3);

        assertThat(cart.isEmpty()).isTrue();
    }

    @Test(groups = {"ui", "regression"}, description = "Quantity chosen on product detail is kept in the cart")
    public void cartKeepsChosenQuantity() {
        CartPage cart = new ProductDetailPage(driver()).open(1).addToCartWithQuantity(4);

        List<CartItem> items = cart.items();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).quantity()).isEqualTo(4);
        assertThat(items.get(0).total()).isEqualTo("Rs. 2000");
    }

    @Test(groups = {"ui", "regression"}, description = "Subscribe from the cart page footer")
    public void subscribeFromCartPage() {
        new CartPage(driver()).open();
        HomePage footer = new HomePage(driver());

        footer.subscribe(TestDataFactory.uniqueEmail());

        assertThat(footer.subscriptionSuccessMessage()).isEqualTo("You have been successfully subscribed!");
    }

    @Test(groups = {"ui", "regression"}, description = "Checkout while logged out asks the user to log in")
    public void checkoutRequiresLogin() {
        CartPage cart = new ProductsPage(driver()).open().addToCartAndViewCart(1);

        assertThat(cart.proceedToCheckoutAsksForLogin()).isTrue();
    }
}
