package com.saransh.automation.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.nio.file.Path;

public class ContactUsPage extends BasePage {

    private static final By HEADING = By.xpath("//div[@class='contact-form']/h2");
    private static final By NAME = By.cssSelector("input[data-qa='name']");
    private static final By EMAIL = By.cssSelector("input[data-qa='email']");
    private static final By SUBJECT = By.cssSelector("input[data-qa='subject']");
    private static final By MESSAGE = By.cssSelector("textarea[data-qa='message']");
    private static final By UPLOAD = By.name("upload_file");
    private static final By SUBMIT = By.cssSelector("input[data-qa='submit-button']");
    private static final By SUCCESS = By.cssSelector(".contact-form .status.alert-success");

    public ContactUsPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Contact Us page")
    public ContactUsPage open() {
        open("/contact_us");
        return this;
    }

    public String heading() {
        return text(HEADING);
    }

    /** sendKeys on a file input uploads the file; on Grid, DriverFactory installs a LocalFileDetector. */
    @Step("Submit contact form with attachment {file}")
    public ContactUsPage submit(String name, String email, String subject, String message, Path file) {
        type(NAME, name);
        type(EMAIL, email);
        type(SUBJECT, subject);
        type(MESSAGE, message);
        present(UPLOAD).sendKeys(file.toAbsolutePath().toString());
        click(SUBMIT);
        wait.until(ExpectedConditions.alertIsPresent()).accept();
        return this;
    }

    public String successMessage() {
        return text(SUCCESS);
    }
}
