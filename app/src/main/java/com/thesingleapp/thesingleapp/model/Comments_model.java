package com.thesingleapp.thesingleapp.model;

public class Comments_model {

    private String userid;
    private String msg;
    private String firstName;
    private String profilePic;

    public Comments_model(String userid, String msg, String firstName, String profilePic) {
        this.userid = userid;
        this.msg = msg;
        this.firstName = firstName;
        this.profilePic = profilePic;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
