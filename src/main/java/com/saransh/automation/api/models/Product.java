package com.saransh.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    public int id;
    public String name;
    public String price;
    public String brand;
    public Category category;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Category {
        public String category;
        public UserType usertype;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserType {
        public String usertype;
    }
}
