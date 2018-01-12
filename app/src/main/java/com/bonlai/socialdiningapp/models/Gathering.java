package com.bonlai.socialdiningapp.models;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Bon Lai on 3/1/2018.
 */

public class Gathering {
    @SerializedName("name")
    public String name;
    @SerializedName("start_datetime")
    public String start_datetime;
    @SerializedName("is_start")
    public Boolean is_start;
    @SerializedName("created_by")
    public int created_by;
    @SerializedName("restaurant")
    public int restaurant;

    @Override
    public String toString() {
        return name;
    }
}
