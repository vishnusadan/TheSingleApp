package com.thesingleapp.thesingleapp.model;

public class MatchData {

    private String userid;
    private String firstName;
    private String city;
    private String status;
    private String premium;
    private String profilePic;

    public MatchData(String userid,String firstName, String city,String status,String premium, String profilePic) {
        this.userid = userid;
        this.firstName = firstName;
        this.city = city;
        this.status = status;
        this.premium = premium;
        this.profilePic = profilePic;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
