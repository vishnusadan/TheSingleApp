package com.thesingleapp.thesingleapp.apilist;

public class Api {

    // local IP http://192.168.1.12:8000/
    // cloud IP http://104.196.26.239:8003/
    //New Server   http://zunamelt.com/
    // New Server http://54.82.163.197:8003/
    // new one http://54.82.163.197:8003/

    public static String IP ="http://thesingle.zunamelt.com/";

    public static String LOGIN = IP+"accounts/login/";
    public static String SIGNUP = IP+"accounts/signup/";
    public static String PROFILEVIEW = IP+"accounts/profile/?";
    public static String SEARCH = IP+"accounts/profile-search/";
    public static String PROFILEADD = IP+"accounts/profile/update/";
    public static String USER = IP+"accounts/user/";
    public static String MESSAGE = IP+"accounts/usermsg/";
    public static String FRIENDS = IP+"accounts/friend/";
    public static String FORGETPASSWORD = IP+"accounts/rest-auth/password/reset/";
    public static String VERIFICATION = IP+"accounts/user-validate/";
    public static String NOTIFICATION = IP+"services/notification/";
    public static String ABOUTUS = IP+"extras/about/";
    public static String TERMSANDCONDITION = IP+"extras/terms/";
    public static String PRIVACYPOLICY = IP+"extras/terms/";
    public static String HELP = IP+"extras/help/";
    public static String CONTACTUS = IP+"extras/feedback/";
    public static String IMAGELIKE = IP+"accounts/imagelike/";
    public static String LOGOUT = IP+"accounts/logout/";
    public static String PREMIUM = IP+"services/premium/";
    public static String PREMIUMDETAILS = IP+"services/premiumdetail/";
    public static String ORDERS = IP+"services/payment/";
    public static String CHANGEPASSWORD = IP+"accounts/chpasswd/";
    public static String FAV = IP+"accounts/favourite/";
    public static String USERLIKE = IP+"accounts/userlike/";
    public static String SETTINGS = IP+"accounts/permission/";
    public static String DEVICEID = IP+"services/devices/";
    public static String COMMENT = IP+"accounts/image/";
    public static String SENDCOMMENT = IP+"accounts/imagecomment/";
    public static String TRANSACTION = IP+"services/transaction/";
    public static String FLIRT = IP+"accounts/userflirt/";
    public static String BLOCK = IP+"accounts/userblocked/";
    public static String USERVIEWEDPROFILE = IP+"accounts/userviewed/";
    public static String ACCESSTOKEN = "https://www.googleapis.com/oauth2/v4/token";
    public static String REQUEST = IP+"accounts/friend/?q=theyreq";
}
