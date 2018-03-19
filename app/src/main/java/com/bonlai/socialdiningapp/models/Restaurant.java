package com.bonlai.socialdiningapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bon Lai on 20/1/2018.
 */

public class Restaurant {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("category")
    private String category;
    @SerializedName("average_rate")
    private Double averageRate;
    @SerializedName("review_count")
    private Integer reviewCount;
    @SerializedName("image")
    private List<RestaurantImg> image = null;
    @SerializedName("phone")
    private String phone;
    @SerializedName("price_range")
    private Integer priceRange;

    public List<RestaurantImg> getImage() {
        return image;
    }

    public void setImage(List<RestaurantImg> image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int url) {
        this.id = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(Double averageRate) {
        this.averageRate = averageRate;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(Integer priceRange) {
        this.priceRange = priceRange;
    }

}