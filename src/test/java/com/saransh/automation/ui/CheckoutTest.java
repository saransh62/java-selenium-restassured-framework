package com.saransh.automation.ui;

import com.saransh.automation.api.models.UserAccount;
import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.CheckoutPage;
import com.saransh.automation.pages.ProductsPage;
import com.saransh.automation.pages.SignupLoginPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Checkout")
public class CheckoutTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke", "regression"}, description = "Logged-in user places an order end to end")
    public void placeOrder() {
        UserAccount user = createUserViaApi();
        new SignupLoginPage(driver()).open().login(user.email, user.password);

        CheckoutPage checkout = new ProductsPage(driver()).open()
                .addToCartAndViewCart(1)
                .proceedToCheckout();

        assertThat(checkout.deliveryAddress())
                .contains(user.company, user.address1, user.address2, user.country, user.mobileNumber)
                .anySatisfy(line -> assertThat(line).contains(user.firstname).contains(user.lastname));

        checkout.placeOrder("Please deliver between 9am and 5pm").pay(user.firstname + " " + user.lastname);

        assertThat(checkout.orderPlacedHeading()).isEqualToIgnoringCase("Order Placed!");
        assertThat(checkout.isOrderConfirmed()).isTrue();
    }
}
