package com.thesingleapp.thesingleapp.model;

public class Blocked_model {

    private String userid;
    private String firstName;
    private String status;
    private String profilePic;
    private String premium;


    public Blocked_model(String userid, String firstName, String status, String profilePic,String premium) {
        this.userid = userid;
        this.firstName = firstName;
        this.status = status;
        this.profilePic = profilePic;
        this.premium = premium;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }





}
