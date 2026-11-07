package com.saransh.automation.ui;

import com.saransh.automation.api.models.UserAccount;
import com.saransh.automation.base.BaseUiTest;
import com.saransh.automation.pages.AccountCreatedPage;
import com.saransh.automation.pages.HomePage;
import com.saransh.automation.pages.SignupLoginPage;
import com.saransh.automation.utils.JsonDataReader;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Feature;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Authentication")
public class AuthTest extends BaseUiTest {

    @Test(groups = {"ui", "smoke", "regression"}, description = "Register a new user through the UI")
    public void registerUser() {
        UserAccount user = TestDataFactory.newUser();
        SignupLoginPage page = new SignupLoginPage(driver()).open();
        assertThat(page.signupHeading()).isEqualTo("New User Signup!");

        page.startSignup(user.name, user.email);
        assertThat(page.isAccountInfoFormVisible()).isTrue();
        AccountCreatedPage created = page.fillAccountInformation(user);
        deleteAfterTest(user);

        assertThat(created.heading()).isEqualToIgnoringCase("Account Created!");
        HomePage home = created.continueToHome();
        assertThat(home.loggedInUsername()).isEqualTo(user.name);
        assertThat(home.deleteAccount()).as("account deleted via UI").isTrue();
    }

    @Test(groups = {"ui", "regression"}, description = "Signup with an already registered email is rejected")
    public void registerWithExistingEmail() {
        UserAccount existing = createUserViaApi();

        SignupLoginPage page = new SignupLoginPage(driver()).open().startSignup("Someone", existing.email);

        assertThat(page.signupError()).isEqualTo("Email Address already exist!");
    }

    @Test(groups = {"ui", "smoke", "regression"}, description = "Login with valid credentials")
    public void loginWithValidCredentials() {
        UserAccount user = createUserViaApi();
        SignupLoginPage page = new SignupLoginPage(driver()).open();
        assertThat(page.loginHeading()).isEqualTo("Login to your account");

        HomePage home = page.login(user.email, user.password);

        assertThat(home.loggedInUsername()).isEqualTo(user.name);
    }

    @DataProvider(name = "invalidLogins", parallel = true)
    public Object[][] invalidLogins() {
        return JsonDataReader.asDataProvider("testdata/invalid-logins.json");
    }

    @Test(groups = {"ui", "regression"}, dataProvider = "invalidLogins",
            description = "Login with invalid credentials shows an error")
    public void loginWithInvalidCredentials(Map<String, String> row) {
        SignupLoginPage page = new SignupLoginPage(driver()).open()
                .loginExpectingError(row.get("email"), row.get("password"));

        assertThat(page.loginError()).as(row.get("case")).isEqualTo("Your email or password is incorrect!");
    }

    @Test(groups = {"ui", "regression"}, description = "Logout returns the user to the login page")
    public void logout() {
        UserAccount user = createUserViaApi();
        HomePage home = new SignupLoginPage(driver()).open().login(user.email, user.password);
        assertThat(home.isLoggedIn()).isTrue();

        SignupLoginPage loginPage = home.logout();

        assertThat(loginPage.currentUrl()).endsWith("/login");
        assertThat(loginPage.loginHeading()).isEqualTo("Login to your account");
    }
}
