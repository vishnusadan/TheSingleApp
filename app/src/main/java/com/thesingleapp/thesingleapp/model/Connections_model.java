package com.thesingleapp.thesingleapp.model;

public class Connections_model {

    private String userid;
    private String firstName;
    private String city;
    private String profilePic;
    private String date;
    private String upremium;
    private String online;

    public Connections_model(String userid, String firstName, String city,String date, String profilePic,String upremium,String online) {
        this.userid = userid;
        this.firstName = firstName;
        this.city = city;
        this.date = date;
        this.profilePic = profilePic;
        this.upremium = upremium;
        this.online = online;
    }


    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
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

    public String getUpremium(){ return upremium; }

    public void setUpremium(String upremium) { this.firstName = firstName; }

    public String getOnline(){ return online; }

    public void setOnline(String upremium) { this.online = online; }
}
