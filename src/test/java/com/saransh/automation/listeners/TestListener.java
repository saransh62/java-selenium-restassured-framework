package com.saransh.automation.listeners;

import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.driver.DriverFactory;
import com.saransh.automation.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * 1) On failure, attaches a screenshot + page source to Allure (runs before @AfterMethod quits the driver).
 * 2) As an annotation transformer, wires RetryAnalyzer onto UI tests only (classes extending BaseUiTest),
 *    so API tests are never retried and a failing API assertion is always reported straight away.
 */
public class TestListener implements ITestListener, IAnnotationTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("START {}.{}", result.getTestClass().getRealClass().getSimpleName(), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASS  {}", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAIL  {}: {}", result.getName(),
                result.getThrowable() == null ? "" : result.getThrowable().getMessage());
        attachBrowserState();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIP  {}", result.getName());
        // A retried attempt is reported as skipped; keep its evidence too.
        if (result.wasRetried()) {
            attachBrowserState();
        }
    }

    private void attachBrowserState() {
        if (!DriverFactory.hasDriver()) {
            return;
        }
        try {
            WebDriver driver = DriverFactory.getDriver();
            Allure.addAttachment("Screenshot", "image/png",
                    new ByteArrayInputStream(ScreenshotUtil.capture(driver)), ".png");
            Allure.addAttachment("Page source", "text/html",
                    new ByteArrayInputStream(driver.getPageSource().getBytes(StandardCharsets.UTF_8)), ".html");
            Allure.addAttachment("URL", driver.getCurrentUrl());
        } catch (RuntimeException e) {
            LOG.warn("Could not capture browser state: {}", e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        Class<?> owner = testMethod != null ? testMethod.getDeclaringClass() : testClass;
        if (owner != null && BaseUiTest.class.isAssignableFrom(owner)) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
