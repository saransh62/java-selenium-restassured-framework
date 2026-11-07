package com.saransh.automation.driver;

import com.saransh.automation.config.ConfigManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

/**
 * Creates one WebDriver per test thread. ThreadLocal is what makes parallel="methods" safe:
 * each TestNG worker thread only ever sees its own browser.
 * Local drivers are resolved by Selenium Manager (bundled with Selenium 4), so no WebDriverManager.
 */
public final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No WebDriver for this thread. Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static WebDriver initDriver() {
        String browser = ConfigManager.browser();
        boolean headless = ConfigManager.headless();
        String gridUrl = ConfigManager.gridUrl();
        MutableCapabilities options = buildOptions(browser, headless);

        WebDriver driver;
        if (gridUrl.isBlank()) {
            driver = "firefox".equals(browser)
                    ? new FirefoxDriver((FirefoxOptions) options)
                    : new ChromeDriver((ChromeOptions) options);
        } else {
            try {
                RemoteWebDriver remote = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
                // Lets file uploads (sendKeys on <input type=file>) ship the local file to the Grid node.
                remote.setFileDetector(new LocalFileDetector());
                driver = remote;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid gridUrl: " + gridUrl, e);
            }
        }
        driver.manage().timeouts().pageLoadTimeout(ConfigManager.pageLoadTimeout());
        LOG.info("Started {} (headless={}, grid={}) on thread {}", browser, headless,
                gridUrl.isBlank() ? "local" : gridUrl, Thread.currentThread().getName());
        DRIVER.set(driver);
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    private static MutableCapabilities buildOptions(String browser, boolean headless) {
        switch (browser) {
            case "firefox": {
                FirefoxOptions ff = new FirefoxOptions();
                ff.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    ff.addArguments("-headless");
                }
                ff.addArguments("--width=1920", "--height=1080");
                return ff;
            }
            case "chrome": {
                ChromeOptions ch = new ChromeOptions();
                // EAGER: don't wait for every third-party ad script to finish loading.
                ch.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    ch.addArguments("--headless=new");
                }
                ch.addArguments("--window-size=1920,1080", "--disable-notifications",
                        "--no-sandbox", "--disable-dev-shm-usage", "--disable-search-engine-choice-screen");
                ch.setExperimentalOption("prefs", Map.of("profile.default_content_setting_values.notifications", 2));
                return ch;
            }
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser + " (use chrome|firefox)");
        }
    }
}
