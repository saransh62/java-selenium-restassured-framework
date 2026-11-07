package com.saransh.automation.pages;

import com.saransh.automation.config.ConfigManager;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Shared behaviour for every page object: explicit waits, "safe" click/type that survive the
 * site's ad overlays, and neutralising consent dialogs / Google vignette ads. No Thread.sleep anywhere.
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    private static final By CONSENT_BUTTON = By.cssSelector(
            "button.fc-cta-consent, button.fc-button.fc-cta-do-not-consent, button[aria-label='Consent']");

    /**
     * Removes ad iframes/containers and the consent overlay from the DOM, and restores scrolling.
     * Google vignette ads are rendered in an ins/iframe over the page and append "#google_vignette".
     */
    private static final String STRIP_ADS_JS =
            "document.querySelectorAll(\"ins.adsbygoogle, iframe[id^='aswift'], iframe[id^='google_ads'],"
                    + " div[id^='google_ads'], .fc-consent-root, #ad_position_box, div.grippy-host\")"
                    + ".forEach(e => e.remove());"
                    + "document.body.style.overflow='auto'; document.documentElement.style.overflow='auto';";

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, ConfigManager.explicitWait());
    }

    protected void open(String path) {
        driver.get(ConfigManager.baseUrl() + path);
        handlePopups();
    }

    /** Best-effort: accept consent if shown, drop vignette hash, strip ad nodes. Never fails a test. */
    public void handlePopups() {
        try {
            List<WebElement> consent = driver.findElements(CONSENT_BUTTON);
            if (!consent.isEmpty() && consent.get(0).isDisplayed()) {
                consent.get(0).click();
            }
        } catch (RuntimeException e) {
            log.debug("Consent dialog not clickable: {}", e.getMessage());
        }
        try {
            String url = driver.getCurrentUrl();
            if (url != null && url.contains("#google_vignette")) {
                log.debug("Google vignette detected, reloading clean URL");
                driver.navigate().to(url.replace("#google_vignette", ""));
            }
            ((JavascriptExecutor) driver).executeScript(STRIP_ADS_JS);
        } catch (RuntimeException e) {
            log.debug("Ad cleanup skipped: {}", e.getMessage());
        }
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement present(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected boolean isDisplayed(By locator) {
        return isDisplayed(locator, Duration.ofSeconds(3));
    }

    protected boolean isDisplayed(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits for clickability, scrolls into view and clicks. If an ad overlay intercepts the click,
     * strips ads and falls back to a JS click so flaky third-party content can't fail the test.
     */
    protected void click(By locator) {
        new WebDriverWait(driver, ConfigManager.explicitWait())
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class)
                .until(d -> {
                    WebElement el = d.findElement(locator);
                    scrollIntoView(el);
                    if (ExpectedConditions.elementToBeClickable(el).apply(d) == null) {
                        return false; // not visible/enabled yet: keep waiting
                    }
                    try {
                        el.click();
                    } catch (ElementClickInterceptedException e) {
                        log.debug("Click intercepted on {}, retrying via JS", locator);
                        handlePopups();
                        ((JavascriptExecutor) d).executeScript("arguments[0].click();", d.findElement(locator));
                    }
                    return true;
                });
    }

    protected void type(By locator, String text) {
        WebElement el = visible(locator);
        scrollIntoView(el);
        el.clear();
        el.sendKeys(text);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected void scrollIntoView(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String title() {
        return driver.getTitle();
    }
}
