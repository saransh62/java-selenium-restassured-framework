package com.saransh.automation.base;

import com.saransh.automation.api.AccountClient;
import com.saransh.automation.api.models.UserAccount;
import com.saransh.automation.driver.DriverFactory;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fresh browser per test method: slower than sharing, but it is what lets tests run in any order
 * and in parallel without leaking cookies, carts or logins between them.
 * Users a UI test needs are created through the API (fast, no browser), tracked per thread,
 * and deleted through the API after the test.
 */
public abstract class BaseUiTest {

    protected final AccountClient accountApi = new AccountClient();
    private final ThreadLocal<List<UserAccount>> usersToDelete = ThreadLocal.withInitial(ArrayList::new);

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        usersToDelete.get().forEach(u -> accountApi.deleteAccount(u.email, u.password));
        usersToDelete.remove();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

    /** Arrange step: registers a brand-new user via the API in ~1s instead of ~20s through the UI. */
    @Step("Create test user via API")
    protected UserAccount createUserViaApi() {
        UserAccount user = TestDataFactory.newUser();
        assertThat(accountApi.createAccount(user).responseCode()).as("API user setup").isEqualTo(201);
        usersToDelete.get().add(user);
        return user;
    }

    /** Registers a user created some other way (e.g. through the UI) for API cleanup. */
    protected void deleteAfterTest(UserAccount user) {
        usersToDelete.get().add(user);
    }
}
