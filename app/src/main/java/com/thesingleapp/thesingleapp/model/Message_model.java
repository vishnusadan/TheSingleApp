package com.thesingleapp.thesingleapp.model;

public class Message_model {

    private String userid;
    private String firstName;
    private String msg;
    private String status;
    private String date;
    private String profilePic;
    private String countvalue;
    private String premium;
    private String friendvalue;
    private String friendonlyvalue;

    public Message_model(String userid, String firstName, String msg, String status,String date,String profilePic,String countvalue,String premium,String friendvalue,String friendonlyvalue) {
        this.userid = userid;
        this.firstName = firstName;
        this.msg = msg;
        this.status = status;
        this.date = date;
        this.profilePic = profilePic;
        this.countvalue = countvalue;
        this.premium = premium;
        this.friendvalue = friendvalue;
        this.friendonlyvalue = friendonlyvalue;
    }


    public String getCountvalue() {
        return countvalue;
    }

    public void setCountvalue(String countvalue) {
        this.countvalue = countvalue;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getpremium() {
        return premium;
    }

    public void setpremium(String premium) {
        this.premium = premium;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getFriendvalue() {
        return friendvalue;
    }

    public void setFriendvalue(String friendvalue) {
        this.friendvalue = friendvalue;
    }

    public String getFriendonlyvalue() {
        return friendonlyvalue;
    }

    public void setFriendonlyvalue(String friendonlyvalue) {
        this.friendonlyvalue = friendonlyvalue;
    }

}
