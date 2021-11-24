package com.codewithankit.ankitchats;

public class postmodel {
    String Username,Time,Status,Post_image,Profile_image;

    public postmodel() {
    }

    public postmodel(String username, String time, String status, String post_image, String profile_image) {
        Username = username;
        Time = time;
        Status = status;
        Post_image = post_image;
        Profile_image = profile_image;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getPost_image() {
        return Post_image;
    }

    public void setPost_image(String post_image) {
        Post_image = post_image;
    }

    public String getProfile_image() {
        return Profile_image;
    }

    public void setProfile_image(String profile_image) {
        Profile_image = profile_image;
    }
}
