package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private static final By SLIDER = By.id("slider-carousel");
    private static final By NAV_PRODUCTS = By.cssSelector("a[href='/products']");
    private static final By NAV_CART = By.cssSelector(".shop-menu a[href='/view_cart']");
    private static final By NAV_LOGIN = By.cssSelector("a[href='/login']");
    private static final By NAV_CONTACT = By.cssSelector("a[href='/contact_us']");
    private static final By NAV_LOGOUT = By.cssSelector("a[href='/logout']");
    private static final By NAV_DELETE = By.cssSelector("a[href='/delete_account']");
    private static final By LOGGED_IN_AS = By.xpath("//a[contains(.,'Logged in as')]");
    private static final By SUBSCRIBE_EMAIL = By.id("susbscribe_email");
    private static final By SUBSCRIBE_BUTTON = By.id("subscribe");
    private static final By SUBSCRIBE_SUCCESS = By.cssSelector("#success-subscribe .alert-success");
    private static final By SUBSCRIPTION_HEADING = By.xpath("//h2[normalize-space()='Subscription']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open home page")
    public HomePage open() {
        open("/");
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(SLIDER, java.time.Duration.ofSeconds(15));
    }
}
