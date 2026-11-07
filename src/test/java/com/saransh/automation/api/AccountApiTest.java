package com.saransh.automation.api;

import com.saransh.automation.api.models.UserAccount;
import com.saransh.automation.api.models.UserDetailResponse;
import com.saransh.automation.base.BaseApiTest;
import com.saransh.automation.utils.TestDataFactory;
import io.qameta.allure.Feature;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Account API")
public class AccountApiTest extends BaseApiTest {

    /** Every account a test creates is recorded here and removed after the class, even if the test failed. */
    private final Set<UserAccount> createdUsers = ConcurrentHashMap.newKeySet();

    private UserAccount registeredUser() {
        UserAccount user = TestDataFactory.newUser();
        ApiResponse res = accounts.createAccount(user);
        assertThat(res.responseCode()).as("precondition: create account").isEqualTo(201);
        createdUsers.add(user);
        return user;
    }

    @AfterClass(alwaysRun = true)
    public void cleanUp() {
        createdUsers.forEach(u -> accounts.deleteAccount(u.email, u.password));
    }

    @Test(groups = {"api", "smoke", "regression"}, description = "createAccount registers a new user")
    public void createAccount() {
        UserAccount user = TestDataFactory.newUser();
        ApiResponse res = accounts.createAccount(user);
        createdUsers.add(user);

        assertThat(res.responseCode()).isEqualTo(201);
        assertThat(res.message()).isEqualTo("User created!");
    }

    @Test(groups = {"api", "regression"}, description = "createAccount rejects an email that already exists")
    public void createAccountDuplicateEmail() {
        UserAccount user = registeredUser();

        ApiResponse res = accounts.createAccount(user);

        assertThat(res.responseCode()).isEqualTo(400);
        assertThat(res.message()).isEqualTo("Email already exists!");
    }

    @Test(groups = {"api", "regression"}, description = "getUserDetailByEmail returns the registered user's details")
    public void getUserByEmail() {
        UserAccount user = registeredUser();

        ApiResponse res = accounts.getUserByEmail(user.email);

        assertThat(res.responseCode()).isEqualTo(200);
        UserDetailResponse.User details = res.as(UserDetailResponse.class).user;
        assertThat(details.email).isEqualTo(user.email);
        assertThat(details.name).isEqualTo(user.name);
        assertThat(details.first_name).isEqualTo(user.firstname);
        assertThat(details.last_name).isEqualTo(user.lastname);
        assertThat(details.city).isEqualTo(user.city);
    }

    @Test(groups = {"api", "regression"}, description = "getUserDetailByEmail returns 404 for an unknown email")
    public void getUserByUnknownEmail() {
        ApiResponse res = accounts.getUserByEmail(TestDataFactory.uniqueEmail());

        assertThat(res.responseCode()).isEqualTo(404);
        assertThat(res.message()).isEqualTo("Account not found with this email, try another email!");
    }

    @Test(groups = {"api", "regression"}, description = "updateAccount changes the stored user details")
    public void updateAccount() {
        UserAccount user = registeredUser();
        user.firstname = "Updated";
        user.city = "Mysuru";

        ApiResponse res = accounts.updateAccount(user);

        assertThat(res.responseCode()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("User updated!");
        UserDetailResponse.User details = accounts.getUserByEmail(user.email).as(UserDetailResponse.class).user;
        assertThat(details.first_name).isEqualTo("Updated");
        assertThat(details.city).isEqualTo("Mysuru");
    }

    @Test(groups = {"api", "regression"}, description = "deleteAccount removes the user")
    public void deleteAccount() {
        UserAccount user = registeredUser();

        ApiResponse res = accounts.deleteAccount(user.email, user.password);

        assertThat(res.responseCode()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("Account deleted!");
        assertThat(accounts.getUserByEmail(user.email).responseCode()).isEqualTo(404);
        createdUsers.remove(user);
    }

    @Test(groups = {"api", "smoke", "regression"}, description = "verifyLogin succeeds with valid credentials")
    public void verifyLoginValid() {
        UserAccount user = registeredUser();

        ApiResponse res = accounts.verifyLogin(user.email, user.password);

        assertThat(res.responseCode()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("User exists!");
    }

    @Test(groups = {"api", "regression"}, description = "verifyLogin fails with invalid credentials")
    public void verifyLoginInvalid() {
        ApiResponse res = accounts.verifyLogin(TestDataFactory.uniqueEmail(), "wrong-password");

        assertThat(res.responseCode()).isEqualTo(404);
        assertThat(res.message()).isEqualTo("User not found!");
    }

    @Test(groups = {"api", "regression"}, description = "verifyLogin without email returns 400")
    public void verifyLoginMissingParam() {
        ApiResponse res = accounts.verifyLoginWithoutEmail("any-password");

        assertThat(res.responseCode()).isEqualTo(400);
        assertThat(res.message()).isEqualTo("Bad request, email or password parameter is missing in POST request.");
        assertMatchesSchema(res, "error-message.schema.json");
    }

    @Test(groups = {"api", "regression"}, description = "DELETE to verifyLogin is rejected with 405")
    public void deleteVerifyLoginNotSupported() {
        ApiResponse res = accounts.deleteVerifyLogin();

        assertThat(res.responseCode()).isEqualTo(405);
        assertThat(res.message()).isEqualTo("This request method is not supported.");
    }
}
