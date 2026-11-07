package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductDetailPage extends BasePage {

    private static final By NAME = By.cssSelector(".product-information h2");
    private static final By CATEGORY = By.xpath("//div[@class='product-information']/p[contains(.,'Category')]");
    private static final By PRICE = By.cssSelector(".product-information > span > span");
    private static final By AVAILABILITY = By.xpath("//div[@class='product-information']/p[b[contains(.,'Availability')]]");
    private static final By CONDITION = By.xpath("//div[@class='product-information']/p[b[contains(.,'Condition')]]");
    private static final By BRAND = By.xpath("//div[@class='product-information']/p[b[contains(.,'Brand')]]");
    private static final By QUANTITY = By.id("quantity");
    private static final By ADD_TO_CART = By.cssSelector(".product-information button.cart");
    private static final By MODAL_VIEW_CART = By.cssSelector("#cartModal a[href='/view_cart']");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open product details #{productId}")
    public ProductDetailPage open(int productId) {
        open("/product_details/" + productId);
        return this;
    }

    public String name() {
        return text(NAME);
    }

    public String category() {
        return text(CATEGORY);
    }

    public String price() {
        return text(PRICE);
    }

    public String availability() {
        return text(AVAILABILITY);
    }

    public String condition() {
        return text(CONDITION);
    }

    public String brand() {
        return text(BRAND);
    }

    @Step("Set quantity to {quantity}, add to cart and view cart")
    public CartPage addToCartWithQuantity(int quantity) {
        type(QUANTITY, String.valueOf(quantity));
        click(ADD_TO_CART);
        click(MODAL_VIEW_CART);
        handlePopups();
        return new CartPage(driver);
    }
}
