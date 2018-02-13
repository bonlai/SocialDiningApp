package com.bonlai.socialdiningapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 10/2/2018.
 */


    public class MapMarker {

        @SerializedName("id")

        private Integer id;
        @SerializedName("name")

        private String name;
        @SerializedName("restaurant")

        private Restaurant restaurant;

        private LatLng latLng;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Restaurant getRestaurant() {
            return restaurant;
        }

        public void setRestaurant(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public void setLatLng(LatLng latLng) {
            this.latLng = latLng;
        }

    }
