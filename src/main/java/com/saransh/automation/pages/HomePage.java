package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/** Home page plus the header/footer shared by every page (nav links, login state, subscription). */
public class HomePage extends BasePage {

    private static final By SLIDER = By.id("slider-carousel");
    private static final By NAV_PRODUCTS = By.cssSelector(".shop-menu a[href='/products']");
    private static final By NAV_CART = By.cssSelector(".shop-menu a[href='/view_cart']");
    private static final By NAV_LOGIN = By.cssSelector(".shop-menu a[href='/login']");
    private static final By NAV_CONTACT = By.cssSelector(".shop-menu a[href='/contact_us']");
    private static final By NAV_LOGOUT = By.cssSelector(".shop-menu a[href='/logout']");
    private static final By NAV_DELETE = By.cssSelector(".shop-menu a[href='/delete_account']");
    private static final By LOGGED_IN_AS = By.xpath("//a[contains(normalize-space(.),'Logged in as')]/b");
    private static final By SUBSCRIBE_EMAIL = By.id("susbscribe_email");
    private static final By SUBSCRIBE_BUTTON = By.id("subscribe");
    private static final By SUBSCRIBE_SUCCESS = By.cssSelector("#success-subscribe .alert-success");
    private static final By SUBSCRIPTION_HEADING = By.xpath("//div[@class='single-widget']/h2[normalize-space()='Subscription']");
    private static final By ACCOUNT_DELETED = By.cssSelector("h2[data-qa='account-deleted']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open home page")
    public HomePage open() {
        open("/");
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(SLIDER, Duration.ofSeconds(15));
    }

    @Step("Go to Signup / Login")
    public SignupLoginPage goToLogin() {
        click(NAV_LOGIN);
        waitForUrlContains("/login");
        handlePopups();
        return new SignupLoginPage(driver);
    }

    @Step("Go to Products")
    public ProductsPage goToProducts() {
        click(NAV_PRODUCTS);
        waitForUrlContains("/products");
        handlePopups();
        return new ProductsPage(driver);
    }

    @Step("Go to Cart")
    public CartPage goToCart() {
        click(NAV_CART);
        waitForUrlContains("/view_cart");
        handlePopups();
        return new CartPage(driver);
    }

    @Step("Go to Contact Us")
    public ContactUsPage goToContactUs() {
        click(NAV_CONTACT);
        waitForUrlContains("/contact_us");
        handlePopups();
        return new ContactUsPage(driver);
    }

    public boolean isLoggedIn() {
        return isDisplayed(LOGGED_IN_AS, Duration.ofSeconds(10));
    }

    public String loggedInUsername() {
        return text(LOGGED_IN_AS);
    }

    @Step("Log out")
    public SignupLoginPage logout() {
        click(NAV_LOGOUT);
        waitForUrlContains("/login");
        handlePopups();
        return new SignupLoginPage(driver);
    }

    @Step("Delete account from the header")
    public boolean deleteAccount() {
        click(NAV_DELETE);
        handlePopups();
        return isDisplayed(ACCOUNT_DELETED, Duration.ofSeconds(10));
    }

    @Step("Subscribe with {email}")
    public HomePage subscribe(String email) {
        scrollIntoView(present(SUBSCRIPTION_HEADING));
        type(SUBSCRIBE_EMAIL, email);
        click(SUBSCRIBE_BUTTON);
        return this;
    }

    public boolean isSubscriptionHeadingVisible() {
        scrollIntoView(present(SUBSCRIPTION_HEADING));
        return isDisplayed(SUBSCRIPTION_HEADING);
    }

    public String subscriptionSuccessMessage() {
        return text(SUBSCRIBE_SUCCESS);
    }
}
