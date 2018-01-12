package com.bonlai.socialdiningapp.models;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Bon Lai on 3/1/2018.
 */

public class Gathering {
    @SerializedName("name")
    private String name;
    @SerializedName("start_datetime")
    private String startDatetime;
    @SerializedName("is_start")
    private Boolean isStart;
    @SerializedName("created_by")
    private int createdBy;
    @SerializedName("restaurant")
    private int restaurant;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Boolean getIsStart() {
        return isStart;
    }

    public void setIsStart(Boolean isStart) {
        this.isStart = isStart;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Integer restaurant) {
        this.restaurant = restaurant;
    }
}
