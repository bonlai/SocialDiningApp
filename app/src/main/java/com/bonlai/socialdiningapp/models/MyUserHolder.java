package com.bonlai.socialdiningapp.models;

/**
 * Created by Bon Lai on 14/1/2018.
 */

public class MyUserHolder {

    private static MyUserHolder myUserHolder;
    private User myUser;

    public static MyUserHolder getInstance(){
        if(myUserHolder==null){
            myUserHolder=new MyUserHolder();
        }
        return myUserHolder;
    }

    public void setUser(User user){
        myUser=user;
    }

    public User getUser(){
        return myUser;
    }
}
