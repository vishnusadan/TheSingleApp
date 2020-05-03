package com.thesingleapp.thesingleapp.userdata;

public class UserDataModel {

    private static UserDataModel instance = new UserDataModel();

    // Getter-Setters
    public static UserDataModel getInstance() {
        return instance;
    }

    public static void setInstance(UserDataModel instance) {
        UserDataModel.instance = instance;
    }

    private String userlogintype;
    private String username;
    private String phonenumber;
    private String emailid;
    private String profilepic;
    private String token;
    private String id;
    private String profileid;
    private String firebasetoken;
    private String premium;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUserlogintype() {
        return userlogintype;
    }

    public void setUserlogintype(String userlogintype) {
        this.userlogintype = userlogintype;
    }

    public String getFirebasetoken() {
        return firebasetoken;
    }

    public void setFirebasetoken(String firebasetoken) {
        this.firebasetoken = firebasetoken;
    }


    public String getProfileid() {
        return profileid;
    }

    public void setProfileid(String profileid) {
        this.profileid = profileid;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }
}
