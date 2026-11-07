package com.saransh.automation.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Shape of /getUserDetailByEmail. Note the API returns first_name/last_name here, not firstname. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailResponse {
    public int responseCode;
    public User user;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        public int id;
        public String name;
        public String email;
        public String title;
        public String birth_day;
        public String birth_month;
        public String birth_year;
        public String first_name;
        public String last_name;
        public String company;
        public String address1;
        public String address2;
        public String country;
        public String state;
        public String city;
        public String zipcode;
    }
}
