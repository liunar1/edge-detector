package com.example.myapplication;

import java.util.ArrayList;

public class User {

    private String mUsername;
    private String mPassword;
    private ArrayList<String> photos;

    public User(){

    }

    public User(String mUsername, String mPassword){
        this.mUsername = mUsername;
        this.mPassword = mPassword;
        this.photos = new ArrayList<String>();
//        tracedPhotos.add("Hello World!");
    }

    public String getmUsername(){
        return mUsername;
    }

    public void setmUsername(String mUsername){
        this.mUsername = mUsername;
    }

    public String getmPassword(){
        return mPassword;
    }

    public void setmPassword(String mPassword){
        this.mPassword = mPassword;
    }

    public ArrayList<String> getPhotos(){
        return photos;
    }

    public void setPhotos(ArrayList<String> photos){
        this.photos = photos;
    }

}
