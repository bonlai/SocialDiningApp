package com.bonlai.socialdiningapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bon Lai on 13/1/2018.
 */

public class Token {
    private static Token mToken;
    @SerializedName("key")
    private String key;


    public static Token getToken(){
        if(mToken==null){
            mToken=new Token();
        }
        return mToken;
    }

    public static void setToken(Token token){
        mToken=token;
    }

    public void setKey(String key) {
        mToken.key = key;
    }

    public String getKey() {
        return key;
    }
}
