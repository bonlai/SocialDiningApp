package com.bonlai.socialdiningapp.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 20/1/2018.
 */

public class Profile {
    @SerializedName("user_id")

    private Integer userId;
    @SerializedName("dob")

    private String dob;
    @SerializedName("location")

    private String location;
    @SerializedName("gender")

    private String gender;
    @SerializedName("self_introduction")

    private String selfIntroduction;
    @SerializedName("image")

    private String image;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isCompleted(){
        if(TextUtils.isEmpty(dob)||TextUtils.isEmpty(location)||
            TextUtils.isEmpty(gender)||TextUtils.isEmpty(selfIntroduction)){
            return false;
        }
        return true;
    }
}
