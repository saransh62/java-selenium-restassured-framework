package com.saransh.automation.api;

import io.qameta.allure.Step;

public class ProductsClient extends ApiClient {

    @Step("GET /productsList")
    public ApiResponse getAllProducts() {
        return new ApiResponse(request().get("/productsList"));
    }

    @Step("POST /productsList (unsupported method)")
    public ApiResponse postToProductsList() {
        return new ApiResponse(request().post("/productsList"));
    }

    @Step("POST /searchProduct search_product={term}")
    public ApiResponse searchProduct(String term) {
        return new ApiResponse(request().formParam("search_product", term).post("/searchProduct"));
    }

    @Step("POST /searchProduct without search_product")
    public ApiResponse searchProductWithoutParam() {
        return new ApiResponse(request().post("/searchProduct"));
    }
}
