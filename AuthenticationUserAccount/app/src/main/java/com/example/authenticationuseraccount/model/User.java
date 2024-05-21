package com.example.authenticationuseraccount.model;

import java.util.List;

public class User {
    private String userID;
    private String username;
    private String email;
    private String signInMethod;
    private String imageURL;

    public User(String userID, String username, String email, String signInMethod, String imageUrl) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.signInMethod = signInMethod;
        this.imageURL = imageUrl;
    }

    public User()
    {}


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSignInMethod() {
        return signInMethod;
    }

    public void setSignInMethod(String signInMethod) {
        this.signInMethod = signInMethod;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}