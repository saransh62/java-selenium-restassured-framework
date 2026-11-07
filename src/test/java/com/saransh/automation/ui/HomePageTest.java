package com.saransh.automation.ui;

import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.HomePage;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Home")
public class HomePageTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke", "regression"}, description = "Home page loads with the carousel")
    public void homePageLoads() {
        HomePage home = new HomePage(driver()).open();
        assertThat(home.isLoaded()).as("carousel visible").isTrue();
        assertThat(home.title()).contains("Automation Exercise");
    }

    @Test(groups = {"ui", "regression"}, description = "Subscribe from the home page footer")
    public void subscribeFromHomePage() {
        HomePage home = new HomePage(driver()).open();
        assertThat(home.isSubscriptionHeadingVisible()).isTrue();

        home.subscribe(TestDataFactory.uniqueEmail());

        assertThat(home.subscriptionSuccessMessage()).isEqualTo("You have been successfully subscribed!");
    }
}
