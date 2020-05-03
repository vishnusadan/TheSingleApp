package com.thesingleapp.thesingleapp.model;

public class Profile_model {

    private String firstName;
    private String city;
    private String profilePic;

    public Profile_model(String firstName, String city, String profilePic) {
        this.firstName = firstName;
        this.city = city;
        this.profilePic = profilePic;
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



}
