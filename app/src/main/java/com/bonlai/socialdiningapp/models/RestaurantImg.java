package com.bonlai.socialdiningapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 21/1/2018.
 */

public class RestaurantImg {
    @SerializedName("id")

    private Integer id;
    @SerializedName("created")

    private String created;
    @SerializedName("image")

    private String image;
    @SerializedName("restaurant")

    private Integer restaurant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Integer restaurant) {
        this.restaurant = restaurant;
    }
}
