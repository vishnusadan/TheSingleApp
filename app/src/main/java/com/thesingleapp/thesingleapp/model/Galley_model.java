package com.thesingleapp.thesingleapp.model;

public class Galley_model {

    private String id;
    private String profilePic;
    private String likescount;
    private String commentscount;
    private String otheruserid;


    public Galley_model(String otheruserid, String id, String profilePic, String likescount, String commentscount) {
        this.id = id;
        this.profilePic = profilePic;
        this.likescount = likescount;
        this.commentscount = commentscount;
        this.otheruserid = otheruserid;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLikescount() {
        return likescount;
    }

    public void setLikescount(String likescount) {
        this.likescount = likescount;
    }

    public String getCommentscount() {
        return commentscount;
    }

    public void setCommentscount(String commentscount) {
        this.commentscount = commentscount;
    }


}
