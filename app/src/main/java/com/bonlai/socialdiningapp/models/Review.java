package com.bonlai.socialdiningapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 23/1/2018.
 */

public class Review {
    @SerializedName("id")
    private Integer id;
    @SerializedName("user")
    private User user;
    @SerializedName("comment")
    private String comment;
    @SerializedName("rating")
    private Integer rating;
    @SerializedName("restaurant")
    private Integer restaurant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Integer restaurant) {
        this.restaurant = restaurant;
    }
}
