package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class CartPage extends BasePage {

    private static final By ROWS = By.cssSelector("#cart_info_table tbody tr[id^='product-']");
    private static final By EMPTY_CART = By.xpath("//span[@id='empty_cart']//b[normalize-space()='Cart is empty!']");
    private static final By PROCEED_TO_CHECKOUT = By.cssSelector("a.check_out");
    private static final By CHECKOUT_MODAL_LOGIN = By.cssSelector("#checkoutModal a[href='/login']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Cart page")
    public CartPage open() {
        open("/view_cart");
        return this;
    }

    public List<CartItem> items() {
        if (!isDisplayed(ROWS, Duration.ofSeconds(5))) {
            return List.of();
        }
        return driver.findElements(ROWS).stream().map(this::toItem).toList();
    }

    private CartItem toItem(WebElement row) {
        int id = Integer.parseInt(row.getAttribute("id").replace("product-", ""));
        return new CartItem(
                id,
                row.findElement(By.cssSelector(".cart_description h4 a")).getText().trim(),
                row.findElement(By.cssSelector(".cart_price p")).getText().trim(),
                Integer.parseInt(row.findElement(By.cssSelector(".cart_quantity button")).getText().trim()),
                row.findElement(By.cssSelector(".cart_total_price")).getText().trim());
    }

    @Step("Remove product #{productId} from cart")
    public CartPage remove(int productId) {
        By row = By.id("product-" + productId);
        click(By.cssSelector("#product-" + productId + " a.cart_quantity_delete"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(row));
        return this;
    }

    public boolean isEmpty() {
        return isDisplayed(EMPTY_CART, Duration.ofSeconds(10));
    }

    @Step("Proceed to checkout")
    public CheckoutPage proceedToCheckout() {
        click(PROCEED_TO_CHECKOUT);
        handlePopups();
        return new CheckoutPage(driver);
    }

    /** When logged out, checkout opens a modal asking the user to register/login. */
    @Step("Proceed to checkout while logged out")
    public boolean proceedToCheckoutAsksForLogin() {
        click(PROCEED_TO_CHECKOUT);
        return isDisplayed(CHECKOUT_MODAL_LOGIN, Duration.ofSeconds(5));
    }
}
