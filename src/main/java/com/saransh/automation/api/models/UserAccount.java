package com.saransh.automation.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Payload for /createAccount and /updateAccount. The API takes form fields, so the POJO is
 * converted to a map whose keys are the API's snake_case field names.
 * Public fields keep the model a plain data holder; TestDataFactory builds valid instances.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccount {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String name;
    public String email;
    public String password;
    public String title;
    @JsonProperty("birth_date")
    public String birthDate;
    @JsonProperty("birth_month")
    public String birthMonth;
    @JsonProperty("birth_year")
    public String birthYear;
    public String firstname;
    public String lastname;
    public String company;
    public String address1;
    public String address2;
    public String country;
    public String zipcode;
    public String state;
    public String city;
    @JsonProperty("mobile_number")
    public String mobileNumber;

    public Map<String, Object> toFormParams() {
        return MAPPER.convertValue(this, new TypeReference<>() { });
    }

    @Override
    public String toString() {
        return "UserAccount{" + email + "}";
    }
}
