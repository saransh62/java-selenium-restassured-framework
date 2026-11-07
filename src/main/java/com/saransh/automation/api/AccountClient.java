package com.saransh.automation.api;

import com.saransh.automation.api.models.UserAccount;
import io.qameta.allure.Step;

public class AccountClient extends ApiClient {

    @Step("POST /createAccount for {account.email}")
    public ApiResponse createAccount(UserAccount account) {
        return new ApiResponse(request().formParams(account.toFormParams()).post("/createAccount"));
    }

    @Step("PUT /updateAccount for {account.email}")
    public ApiResponse updateAccount(UserAccount account) {
        return new ApiResponse(request().formParams(account.toFormParams()).put("/updateAccount"));
    }

    @Step("DELETE /deleteAccount for {email}")
    public ApiResponse deleteAccount(String email, String password) {
        return new ApiResponse(request().formParam("email", email).formParam("password", password)
                .delete("/deleteAccount"));
    }

    @Step("GET /getUserDetailByEmail?email={email}")
    public ApiResponse getUserByEmail(String email) {
        return new ApiResponse(request().queryParam("email", email).get("/getUserDetailByEmail"));
    }

    @Step("POST /verifyLogin for {email}")
    public ApiResponse verifyLogin(String email, String password) {
        return new ApiResponse(request().formParam("email", email).formParam("password", password)
                .post("/verifyLogin"));
    }

    @Step("POST /verifyLogin with only password")
    public ApiResponse verifyLoginWithoutEmail(String password) {
        return new ApiResponse(request().formParam("password", password).post("/verifyLogin"));
    }

    @Step("DELETE /verifyLogin (unsupported method)")
    public ApiResponse deleteVerifyLogin() {
        return new ApiResponse(request().delete("/verifyLogin"));
    }
}
