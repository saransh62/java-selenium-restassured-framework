package com.saransh.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandsResponse {
    public int responseCode;
    public List<Brand> brands;
}
