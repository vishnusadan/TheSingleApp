package com.thesingleapp.thesingleapp.model;

public class Notification_model {

    private String userid;
    private String msg;
    private String profilePic;
    private String upremium;
    private String uonline;
    private String date;

    public Notification_model(String userid, String msg, String profilePic,String upremium,String uonline,String date) {
        this.userid = userid;
        this.msg = msg;
        this.profilePic = profilePic;
        this.upremium = upremium;
        this.uonline = uonline;
        this.date = date;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getUpremium() {
        return upremium;
    }

    public void setUpremium(String upremium) {
        this.upremium = upremium;
    }

    public String getUonline() {
        return uonline;
    }

    public void setUonline(String uonline) {
        this.uonline = uonline;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }
}
