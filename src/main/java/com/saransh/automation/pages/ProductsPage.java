package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductsPage extends BasePage {

    private static final By HEADING = By.cssSelector(".features_items h2.title");
    private static final By SEARCH_INPUT = By.id("search_product");
    private static final By SEARCH_BUTTON = By.id("submit_search");
    private static final By PRODUCT_NAMES = By.cssSelector(".features_items .productinfo p");
    private static final By CART_MODAL = By.id("cartModal");
    private static final By MODAL_VIEW_CART = By.cssSelector("#cartModal a[href='/view_cart']");
    private static final By MODAL_CONTINUE = By.cssSelector("#cartModal button.close-modal");

    public ProductsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Products page")
    public ProductsPage open() {
        open("/products");
        return this;
    }

    public String heading() {
        return text(HEADING);
    }

    public List<String> productNames() {
        return allVisible(PRODUCT_NAMES).stream().map(WebElement::getText).map(String::trim).toList();
    }

    @Step("Search for '{term}'")
    public ProductsPage search(String term) {
        type(SEARCH_INPUT, term);
        click(SEARCH_BUTTON);
        handlePopups();
        waitForUrlContains("search=");
        return this;
    }

    @Step("Open product #{productId}")
    public ProductDetailPage viewProduct(int productId) {
        click(By.cssSelector("a[href='/product_details/" + productId + "']"));
        handlePopups();
        return new ProductDetailPage(driver);
    }

    @Step("Add product #{productId} to cart and continue shopping")
    public ProductsPage addToCartAndContinue(int productId) {
        addToCart(productId);
        click(MODAL_CONTINUE);
        wait.until(d -> !d.findElement(CART_MODAL).isDisplayed());
        return this;
    }

    @Step("Add product #{productId} to cart and view cart")
    public CartPage addToCartAndViewCart(int productId) {
        addToCart(productId);
        click(MODAL_VIEW_CART);
        handlePopups();
        return new CartPage(driver);
    }

    private void addToCart(int productId) {
        click(By.cssSelector(".productinfo a.add-to-cart[data-product-id='" + productId + "']"));
        visible(CART_MODAL);
    }
}
