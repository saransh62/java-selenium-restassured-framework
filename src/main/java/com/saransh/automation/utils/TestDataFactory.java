package com.saransh.automation.utils;

import com.saransh.automation.api.models.UserAccount;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique, valid test data so parallel tests never collide on the same account.
 * Uniqueness = timestamp + per-JVM counter + random suffix (safe across threads and CI runners).
 */
public final class TestDataFactory {

    private static final AtomicInteger COUNTER = new AtomicInteger();
    public static final String DEFAULT_PASSWORD = "Passw0rd!";

    private TestDataFactory() {
    }

    public static String uniqueEmail() {
        return "qa_" + System.currentTimeMillis() + "_" + COUNTER.incrementAndGet() + "_"
                + UUID.randomUUID().toString().substring(0, 6) + "@example.com";
    }

    public static String uniqueName() {
        return "QA User " + UUID.randomUUID().toString().substring(0, 5);
    }

    public static UserAccount newUser() {
        UserAccount u = new UserAccount();
        u.name = uniqueName();
        u.email = uniqueEmail();
        u.password = DEFAULT_PASSWORD;
        u.title = "Mr";
        u.birthDate = "15";
        u.birthMonth = "6";
        u.birthYear = "1995";
        u.firstname = "Test";
        u.lastname = "Automation";
        u.company = "QA Labs";
        u.address1 = "221B Baker Street";
        u.address2 = "Suite 4";
        u.country = "India";
        u.state = "Karnataka";
        u.city = "Bengaluru";
        u.zipcode = "560001";
        u.mobileNumber = "9876543210";
        return u;
    }
}
