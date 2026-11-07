package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** /checkout (address review + order comment) and /payment (card form + confirmation). */
public class CheckoutPage extends BasePage {

    private static final By DELIVERY_ADDRESS_LINES = By.cssSelector("#address_delivery li");
    private static final By COMMENT = By.cssSelector("textarea[name='message']");
    private static final By PLACE_ORDER = By.cssSelector("a[href='/payment']");

    private static final By NAME_ON_CARD = By.cssSelector("input[data-qa='name-on-card']");
    private static final By CARD_NUMBER = By.cssSelector("input[data-qa='card-number']");
    private static final By CVC = By.cssSelector("input[data-qa='cvc']");
    private static final By EXPIRY_MONTH = By.cssSelector("input[data-qa='expiry-month']");
    private static final By EXPIRY_YEAR = By.cssSelector("input[data-qa='expiry-year']");
    private static final By PAY = By.cssSelector("button[data-qa='pay-button']");
    private static final By ORDER_PLACED = By.cssSelector("h2[data-qa='order-placed']");
    private static final By ORDER_CONFIRMED = By.xpath("//p[contains(.,'Your order has been confirmed')]");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /** All lines of the delivery address block, e.g. "Mr. Test Automation", "QA Labs", "221B Baker Street". */
    public List<String> deliveryAddress() {
        return allVisible(DELIVERY_ADDRESS_LINES).stream().map(WebElement::getText).map(String::trim)
                .filter(s -> !s.isEmpty()).toList();
    }

    @Step("Add order comment and place order")
    public CheckoutPage placeOrder(String comment) {
        type(COMMENT, comment);
        click(PLACE_ORDER);
        handlePopups();
        return this;
    }

    /** Uses dummy card data; the site is a test shop and never charges anything. */
    @Step("Pay with dummy card")
    public CheckoutPage pay(String nameOnCard) {
        type(NAME_ON_CARD, nameOnCard);
        type(CARD_NUMBER, "4111111111111111");
        type(CVC, "123");
        type(EXPIRY_MONTH, "12");
        type(EXPIRY_YEAR, "2030");
        click(PAY);
        handlePopups();
        return this;
    }

    public String orderPlacedHeading() {
        return text(ORDER_PLACED);
    }

    public boolean isOrderConfirmed() {
        return isDisplayed(ORDER_CONFIRMED);
    }
}
