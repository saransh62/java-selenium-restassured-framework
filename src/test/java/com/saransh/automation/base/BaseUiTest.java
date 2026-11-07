package com.saransh.automation.base;

import com.saransh.automation.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Fresh browser per test method: slower than sharing, but it is what lets tests run in any order
 * and in parallel without leaking cookies, carts or logins between them.
 */
public abstract class BaseUiTest {

    @BeforeMethod(alwaysRun = true)
    public void startBrowser() {
        DriverFactory.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser() {
        DriverFactory.quitDriver();
    }

    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }
}
