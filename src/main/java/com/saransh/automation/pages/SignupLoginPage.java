package com.saransh.automation.pages;

import com.saransh.automation.api.models.UserAccount;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/** /login: the login form, the "New User Signup!" form, and the account-information form it leads to. */
public class SignupLoginPage extends BasePage {

    private static final By LOGIN_HEADING = By.xpath("//div[@class='login-form']/h2");
    private static final By LOGIN_EMAIL = By.cssSelector("input[data-qa='login-email']");
    private static final By LOGIN_PASSWORD = By.cssSelector("input[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-qa='login-button']");
    private static final By LOGIN_ERROR = By.xpath("//form[@action='/login']/p");

    private static final By SIGNUP_HEADING = By.xpath("//div[@class='signup-form']/h2");
    private static final By SIGNUP_NAME = By.cssSelector("input[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL = By.cssSelector("input[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("button[data-qa='signup-button']");
    private static final By SIGNUP_ERROR = By.xpath("//form[@action='/signup']/p");

    // Account information form (/signup)
    private static final By ENTER_ACCOUNT_INFO = By.xpath("//b[normalize-space()='Enter Account Information']");
    private static final By TITLE_MR = By.id("id_gender1");
    private static final By TITLE_MRS = By.id("id_gender2");
    private static final By PASSWORD = By.cssSelector("input[data-qa='password']");
    private static final By DAYS = By.cssSelector("select[data-qa='days']");
    private static final By MONTHS = By.cssSelector("select[data-qa='months']");
    private static final By YEARS = By.cssSelector("select[data-qa='years']");
    private static final By NEWSLETTER = By.id("newsletter");
    private static final By FIRST_NAME = By.cssSelector("input[data-qa='first_name']");
    private static final By LAST_NAME = By.cssSelector("input[data-qa='last_name']");
    private static final By COMPANY = By.cssSelector("input[data-qa='company']");
    private static final By ADDRESS1 = By.cssSelector("input[data-qa='address']");
    private static final By ADDRESS2 = By.cssSelector("input[data-qa='address2']");
    private static final By COUNTRY = By.cssSelector("select[data-qa='country']");
    private static final By STATE = By.cssSelector("input[data-qa='state']");
    private static final By CITY = By.cssSelector("input[data-qa='city']");
    private static final By ZIPCODE = By.cssSelector("input[data-qa='zipcode']");
    private static final By MOBILE = By.cssSelector("input[data-qa='mobile_number']");
    private static final By CREATE_ACCOUNT = By.cssSelector("button[data-qa='create-account']");

    public SignupLoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open Signup / Login page")
    public SignupLoginPage open() {
        open("/login");
        return this;
    }

    public String loginHeading() {
        return text(LOGIN_HEADING);
    }

    public String signupHeading() {
        return text(SIGNUP_HEADING);
    }

    @Step("Log in as {email}")
    public HomePage login(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
        handlePopups();
        return new HomePage(driver);
    }

    @Step("Attempt login as {email} (expected to fail)")
    public SignupLoginPage loginExpectingError(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
        return this;
    }

    public String loginError() {
        return text(LOGIN_ERROR);
    }

    @Step("Start signup for {name} / {email}")
    public SignupLoginPage startSignup(String name, String email) {
        type(SIGNUP_NAME, name);
        type(SIGNUP_EMAIL, email);
        click(SIGNUP_BUTTON);
        handlePopups();
        return this;
    }

    public boolean isAccountInfoFormVisible() {
        return isDisplayed(ENTER_ACCOUNT_INFO, java.time.Duration.ofSeconds(10));
    }

    public String signupError() {
        return text(SIGNUP_ERROR);
    }

    @Step("Fill account information and create account")
    public AccountCreatedPage fillAccountInformation(UserAccount user) {
        click("Mrs".equalsIgnoreCase(user.title) ? TITLE_MRS : TITLE_MR);
        type(PASSWORD, user.password);
        new Select(visible(DAYS)).selectByValue(user.birthDate);
        new Select(visible(MONTHS)).selectByValue(user.birthMonth);
        new Select(visible(YEARS)).selectByValue(user.birthYear);
        click(NEWSLETTER);
        type(FIRST_NAME, user.firstname);
        type(LAST_NAME, user.lastname);
        type(COMPANY, user.company);
        type(ADDRESS1, user.address1);
        type(ADDRESS2, user.address2);
        new Select(visible(COUNTRY)).selectByVisibleText(user.country);
        type(STATE, user.state);
        type(CITY, user.city);
        type(ZIPCODE, user.zipcode);
        type(MOBILE, user.mobileNumber);
        click(CREATE_ACCOUNT);
        handlePopups();
        return new AccountCreatedPage(driver);
    }
}
