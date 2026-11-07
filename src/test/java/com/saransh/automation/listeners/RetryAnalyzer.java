package com.saransh.automation.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed UI test once. UI tests hit a live third-party site full of ads, so one retry
 * absorbs genuine environmental flakiness without hiding real, repeatable failures.
 * TestNG creates one analyzer instance per test method invocation, so the counter is not shared.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(RetryAnalyzer.class);
    static final int MAX_RETRIES = 1;
    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < MAX_RETRIES) {
            attempts++;
            LOG.warn("Retrying {} (attempt {}/{})", result.getName(), attempts, MAX_RETRIES);
            return true;
        }
        return false;
    }
}
