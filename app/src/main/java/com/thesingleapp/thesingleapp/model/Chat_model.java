package com.thesingleapp.thesingleapp.model;

public class Chat_model {


    private String msgid;
    private String userid;
    private String firstName;
    private String message;
    private String date;
    private String profilePic;

    public Chat_model(String userid, String firstName, String message,String date, String profilePic,String msgid) {
        this.msgid = msgid;
        this.userid = userid;
        this.firstName = firstName;
        this.message = message;
        this.date = date;
        this.profilePic = profilePic;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
