package com.saransh.automation.api;

import io.qameta.allure.Step;

public class BrandsClient extends ApiClient {

    @Step("GET /brandsList")
    public ApiResponse getAllBrands() {
        return new ApiResponse(request().get("/brandsList"));
    }

    @Step("PUT /brandsList (unsupported method)")
    public ApiResponse putToBrandsList() {
        return new ApiResponse(request().put("/brandsList"));
    }
}
