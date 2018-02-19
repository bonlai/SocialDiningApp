package com.bonlai.socialdiningapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 8/2/2018.
 */

public class Interest {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

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

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Interest){
            Interest ptr = (Interest) v;
            retVal = ptr.id == this.id;
        }

        return retVal;
    }
}
