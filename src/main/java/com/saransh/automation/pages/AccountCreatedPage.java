package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AccountCreatedPage extends BasePage {

    private static final By HEADING = By.cssSelector("h2[data-qa='account-created']");
    private static final By CONTINUE = By.cssSelector("a[data-qa='continue-button']");

    public AccountCreatedPage(WebDriver driver) {
        super(driver);
    }

    public String heading() {
        return text(HEADING);
    }

    @Step("Continue after account creation")
    public HomePage continueToHome() {
        click(CONTINUE);
        handlePopups();
        return new HomePage(driver);
    }
}
