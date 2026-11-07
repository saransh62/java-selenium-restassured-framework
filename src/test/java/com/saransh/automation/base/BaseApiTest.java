package com.saransh.automation.base;

import com.saransh.automation.api.AccountClient;
import com.saransh.automation.api.BrandsClient;
import com.saransh.automation.api.ProductsClient;

/** Clients are stateless (every call builds its own request from a shared immutable spec). */
public abstract class BaseApiTest {

    protected final ProductsClient products = new ProductsClient();
    protected final BrandsClient brands = new BrandsClient();
    protected final AccountClient accounts = new AccountClient();
}
