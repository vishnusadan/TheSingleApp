package com.thesingleapp.thesingleapp.model;

public class SearchData {

    private String userid;
    private String firstName;
    private String city;
    private String profilePic;
    private String premium;
    private String status;

    public SearchData(String userid,String firstName, String city,String status,String premium, String profilePic) {
        this.userid = userid;
        this.firstName = firstName;
        this.status = status;
        this.city = city;
        this.premium = premium;
        this.profilePic = profilePic;
    }


    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
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


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
