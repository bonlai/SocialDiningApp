package com.bonlai.socialdiningapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 13/1/2018.
 */

public class User {
        @SerializedName("pk")
        private Integer pk;
        @SerializedName("username")
        private String username;
        @SerializedName("email")
        private String email;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("profile")
        private Profile profile;
        @SerializedName("id")
        private Integer id;

        public Integer getPk() {
            return pk;
        }

        public void setPk(Integer pk) {
            this.pk = pk;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
}
