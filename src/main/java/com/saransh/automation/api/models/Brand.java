package com.saransh.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Brand {
    public int id;
    public String brand;
}
