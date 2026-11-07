package com.saransh.automation.ui;

import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.HomePage;
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
}
