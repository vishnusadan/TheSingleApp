package com.thesingleapp.thesingleapp.model;

public class Request_model {

    private String userid;
    private String id;
    private String firstName;
    private String profilePic;
    private String premium;
    private String city;
    private String otheruserid;



    public Request_model(String useridvalue,String id ,String otheruserid,String first_name, String profileimage, String premium, String city) {
        this.userid = useridvalue;
        this.firstName = firstName;
        this.id= id;
        this.otheruserid = otheruserid;
        this.firstName = first_name;
        this.profilePic = profileimage;
        this.premium = premium;
        this.city = city;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public String getOtheruserid() {
        return otheruserid;
    }

    public void setOtheruserid(String otheruserid) {
        this.otheruserid = otheruserid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }
}
