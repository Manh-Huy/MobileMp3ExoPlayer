package com.example.authenticationuseraccount.model.business;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {
    private List<String> listSong;
    private String playlistName;
    private String userID;
    private String username;

    public Playlist(List<String> listSong, String playlistName, String userID, String username) {
        this.listSong = listSong;
        this.playlistName = playlistName;
        this.userID = userID;
        this.username = username;
    }

    public List<String> getListSong() {
        return listSong;
    }

    public void setListSong(List<String> listSong) {
        this.listSong = listSong;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

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
}
