package com.saransh.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductsResponse {
    public int responseCode;
    public List<Product> products;
}
