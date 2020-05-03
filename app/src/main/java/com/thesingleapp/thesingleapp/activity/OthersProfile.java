package com.thesingleapp.thesingleapp.activity;


import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class OthersProfile extends AppCompatActivity implements View.OnClickListener {

    private ImageView profilepic,backbtn,likes_img,fav_img,blockicon,friendicon,flirt_smile,active,premium,profileback;
    private TextView block_tv,cityname,request_tv,flirt_tv,aboutus,hobbycontent,agecount,favouritecount,likescount,name,favtext,liketext,lookingforvalue,religionvalue,occupationvalue,retiredvalue,martialstatusvalue,numberofchildrenvalue,livingathomevalue;
    private LinearLayout images,flirt,message,request,block;
    public RelativeLayout topcell;
    private StringRequest stringRequest;
    private String userid,token,Otheruserid,blockid,requestid,username,userpremiumtype;
    public static String API = "",UNFAPI="",ACTION="";
    public static String profilepic_s;
    private long mLastClickTime = 0;
    public  String flirtid,likeid,favid,friendid,favou_count,like_count,premiumuser,premiummembersonlymsg,onlyfriendcancomment,friendlyonlycanmsg,friendly_only_can_view_profile;
    public static String likes= "false",favs ="false" , block_s = "false", request_s = "notsend" , flirt_s = "false",accepted = "false",friend_s = "false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        cityname = findViewById(R.id.usercity);
        livingathomevalue = findViewById(R.id.livingathomevalue);
        numberofchildrenvalue = findViewById(R.id.numberofchildrenvalue);
        martialstatusvalue = findViewById(R.id.martialstatusvalue);
        retiredvalue = findViewById(R.id.retiredvalue);
        occupationvalue = findViewById(R.id.occupationvalue);
        religionvalue = findViewById(R.id.religionvalue);
        lookingforvalue = findViewById(R.id.lookingforvalue);
        favtext = findViewById(R.id.favtext);
        liketext = findViewById(R.id.liketext);
        premium = findViewById(R.id.premium);
        active = findViewById(R.id.active);
        profilepic = findViewById(R.id.profilepic);
        profileback = findViewById(R.id.profileback);
        name = findViewById(R.id.name);
        likescount = findViewById(R.id.likescount);
        favouritecount = findViewById(R.id.favouritecount);
        agecount = findViewById(R.id.agecount);
        topcell = findViewById(R.id.ivImage);
        aboutus = findViewById(R.id.aboutcontent);
        hobbycontent = findViewById(R.id.hobbycontent);
        images = findViewById(R.id.images);
        flirt = findViewById(R.id.flirt);
        message = findViewById(R.id.message);
        request = findViewById(R.id.request);
        block = findViewById(R.id.block);
        fav_img = findViewById(R.id.fav_img);
        likes_img = findViewById(R.id.likes_img);
        blockicon = findViewById(R.id.blockicon);
        flirt_smile = findViewById(R.id.flirt_smile);
        friendicon = findViewById(R.id.friend_icon);
        backbtn = findViewById(R.id.backbutton);
        block_tv = findViewById(R.id.block_tv);
        request_tv = findViewById(R.id.request_tv);
        flirt_tv = findViewById(R.id.flirt_tv);

        images.setOnClickListener(this);
        flirt.setOnClickListener(this);
        message.setOnClickListener(this);
        request.setOnClickListener(this);
        block.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        fav_img.setOnClickListener(this);
        likes_img.setOnClickListener(this);
        likescount.setOnClickListener(this);
        favouritecount.setOnClickListener(this);
        favtext.setOnClickListener(this);
        liketext.setOnClickListener(this);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {

            Otheruserid = mBundle.getString("userid");

        }

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();
        username = data.getUsername();
        userpremiumtype = data.getPremium();

        //Get Profile Data
        getProfileData();

        VIEWEDPROFILE();



    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.images:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                startActivity(new Intent(OthersProfile.this,GalleryScreen.class).putExtra("otheruserid",Otheruserid));

                break;

             case R.id.flirt:


                 if(flirt_s.equals("false")){


                     flirt.setEnabled(false);

                     flirt_s = "true";

                     flirt_smile.setImageResource(R.drawable.flirted_icon);

                     flirt_tv.setText("Flirted");

                     ACTION = "flirt";

                     API=Api.FLIRT;

                     //to hit the api
                     HITDATA();


                 }else {

                     flirt.setEnabled(false);

                     flirt_s = "false";

                     flirt_smile.setImageResource(R.drawable.flirt_white_icon);

                     flirt_tv.setText("Flirt");

                     UNFAPI=Api.FLIRT+flirtid+"/";

                     //to hit the api
                     UnFLF();

                 }

                break;

            case R.id.message:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                //premium users can only message in app
                if (userpremiumtype.equals("true")){

                    //for settings set all conditions
                if(friendlyonlycanmsg.equals("true")){

                    if(friend_s.equals("true")) {

               if(premiummembersonlymsg.equals("true")){

                   if (userpremiumtype.equals("true")){

                           startActivity(new Intent(OthersProfile.this, ChatScreen.class).putExtra("UserId", Otheruserid));

                   }else {

                       CommonMethod.showAlertDialog(OthersProfile.this, "Premium", "You Want To Be Premium User To Message",  "Be Premium",  "Cancel", new CommonMethod.DialogClickListener() {
                                   @Override
                                   public void dialogOkBtnClicked(String value) {

                                       startActivity(new Intent(OthersProfile.this,PremiumScreen.class));

                                   }
                                   @Override
                                   public void dialogNoBtnClicked(String value) {
                                       OthersProfile.this.finish();
                                   }
                               }
                       );

                   }

                }else {

                   startActivity(new Intent(OthersProfile.this, ChatScreen.class).putExtra("UserId", Otheruserid));

                  }
                    }else {

                        CommonMethod.showAlertDialog(OthersProfile.this, "Friends", "You Want To Be Friend with them To Message",  "",  "Ok", new CommonMethod.DialogClickListener() {
                                    @Override
                                    public void dialogOkBtnClicked(String value) {


                                    }
                                    @Override
                                    public void dialogNoBtnClicked(String value) {

                                    }
                                }
                        );

                    }
                }else {

                    startActivity(new Intent(OthersProfile.this, ChatScreen.class).putExtra("UserId", Otheruserid));

                }
                }else {

                    CommonMethod.showAlertDialog(OthersProfile.this, "Premium", "You Want To Be Premium User To Message",  "Be Premium",  "Cancel", new CommonMethod.DialogClickListener() {
                                @Override
                                public void dialogOkBtnClicked(String value) {

                                    startActivity(new Intent(OthersProfile.this,PremiumScreen.class));

                                }
                                @Override
                                public void dialogNoBtnClicked(String value) {

                                }
                            }
                    );

                }
                break;

            case R.id.request:


                if(request_s.equals("false")){

                    request.setEnabled(false);

                    request_s = "true";


                    friendicon.setImageResource(R.drawable.cancel_request);

                    request_tv.setText("Cancel Request");

                    //to hit the api
                    Friends();


                }else if (request_s.equals("true")){

                    request.setEnabled(false);

                    request_s = "false";

                    friendicon.setImageResource(R.drawable.add_friend);

                    request_tv.setText("Add Friend");

                    UNFAPI=Api.FRIENDS+requestid+"/";

                    //to hit the api
                    UnFLF();


                }else if (request_s.equals("other")){

                    request.setEnabled(false);

                    friend_s = "true";


                    friendicon.setImageResource(R.drawable.friends_white);

                    request_tv.setText("Friends");

                    //to hit the api
                    AcceptedFriends();

                }else if(friend_s.equals("true")) {

                    request.setEnabled(false);

                    friend_s = "false";

                    request_s = "false";

                    friendicon.setImageResource(R.drawable.add_friend);

                    request_tv.setText("Add Friend");

                    UNFAPI=Api.FRIENDS+friendid+"/";

                    //to hit the api
                    UnFLF();

                }


                break;

            case R.id.block:



                if(block_s.equals("false")){

                    block.setEnabled(false);

                    block_s = "true";

                    blockicon.setImageResource(R.drawable.un_block_icon);

                    block_tv.setText("Un Block");

                    //to block the api
                    BLOCK();


                }else {

                    block.setEnabled(false);

                    block_s = "false";

                    blockicon.setImageResource(R.drawable.block_icon);

                    block_tv.setText("Block");

                    //to block the api
                    UNFAPI=Api.BLOCK+blockid+"/";

                    //to hit the api
                    UnFLF();
                }


                break;

            case R.id.backbutton:

                finish();

                break;

            case R.id.fav_img:

                favOnclick();

                break;

            case R.id.likes_img:

                likeOnclick();

                break;

            case R.id.likescount:

                likeOnclick();

                break;

            case R.id.favouritecount:

                favOnclick();

                break;


            case R.id.liketext:

                likeOnclick();

                break;

            case R.id.favtext:

                favOnclick();

                break;

        }
    }


    public void HITDATA(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {
                            JSONObject json = new JSONObject(ServerResponse);

                            String status = json.getString("status");

                            if (status.equals("success")){

                                if (ACTION.equals("flirt")) {

                                    flirtid = json.getString("id");

                                }else if (ACTION.equals("fav")){

                                    favid = json.getString("id");

                                }else {

                                    likeid = json.getString("id");
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                               flirt.setEnabled(true);

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


//                        CommonMethod.showToast(OthersProfile.this, "");
                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("user", userid);
                params.put("other", Otheruserid);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(OthersProfile.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public void BLOCK(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.BLOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {
                            JSONObject json = new JSONObject(ServerResponse);

                            String status = json.getString("status");

                            if (status.equals("success")){

                                blockid = json.getString("id");

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        block.setEnabled(true);
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.

                        CommonMethod.showToast(OthersProfile.this,"Something Went Wrong");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.


                params.put("user", userid);
                params.put("other", Otheruserid);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(OthersProfile.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public void Friends(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.FRIENDS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {
                            JSONObject data = new JSONObject(ServerResponse);

                            String status = data.getString("status");

                            if (status.equals("success")){

                                String id = data.getString("id");

                                requestid = id;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        request.setEnabled(true);
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("user", userid);
                params.put("other", Otheruserid);


                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(OthersProfile.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

    public void getProfileData() {
        //Show Progress Dailog
        CommonMethod.showpDialog(this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.USER+Otheruserid+"/?other="+Otheruserid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {
                            JSONObject jsondata = new JSONObject(ServerResponse);

                            JSONObject settings = jsondata.getJSONObject("user_settings");

                            String friend = jsondata.getString("user_friend");

                            String[] splitedfriends = friend.split("-");

                            String friendsuser = splitedfriends[0];
                            friendid = splitedfriends[1];


                            String theyrequested = jsondata.getString("user_request");

                            String[] splitedtheyrequest = theyrequested.split("-");

                            String theyrequestuser = splitedtheyrequest[0];
                            requestid = splitedtheyrequest[1];


                             premiumuser = settings.getString("premium_members_only_view");
                             premiummembersonlymsg = settings.getString("premium_members_only_msg");
                             onlyfriendcancomment = settings.getString("only_friend_can_comment");
                             friendlyonlycanmsg = settings.getString("friendly_only_can_msg");
                             friendly_only_can_view_profile = settings.getString("friendly_only_can_view_profile");

                             //Show Progress Dailog
                            CommonMethod.hidepDialog();

                             if (onlyfriendcancomment.equals("true")){

                                 if (friendsuser.equals("no")) {

                                     // to get data in comment screen
                                     UserData.COMMENTPERMISSION = "no";

                                 }else {

                                     // to get data in comment screen
                                     UserData.COMMENTPERMISSION = "yes";

                                 }

                             }

                            if (friendly_only_can_view_profile.equals("true")) {

                                if (theyrequestuser.equals("other")) {

                                    CommonMethod.showAlertDialog(OthersProfile.this, "", " Accept their Friend Request To View Their Profile", "Accept", "Reject", new CommonMethod.DialogClickListener() {
                                                @Override
                                                public void dialogOkBtnClicked(String value) {

                                                    AcceptedFriends();
                                                    getProfileData();

                                                }

                                                @Override
                                                public void dialogNoBtnClicked(String value) {
                                                    OthersProfile.this.finish();
                                                }
                                            }
                                    );



                                }

                                else if (friendsuser.equals("no")) {

                                     CommonMethod.showAlertDialog(OthersProfile.this, "", "You Want To Be Friend To View Profile", "Ok", "Cancel", new CommonMethod.DialogClickListener() {
                                                 @Override
                                                 public void dialogOkBtnClicked(String value) {

                                                     OthersProfile.this.finish();
                                                 }
                                                 @Override
                                                 public void dialogNoBtnClicked(String value) {
                                                     OthersProfile.this.finish();
                                                 }
                                             }
                                     );

                                 }
                                 else {


                            if (userpremiumtype.equals("false")){

                            if (premiumuser.equals("true")){

                                CommonMethod.showAlertDialog(OthersProfile.this, "", "You Want To Be Premium To View Profile", "Be Premium","Cancel", new CommonMethod.DialogClickListener() {
                                            @Override
                                            public void dialogOkBtnClicked(String value) {

                                                startActivity(new Intent(OthersProfile.this,PremiumScreen.class));
                                            }
                                            @Override
                                            public void dialogNoBtnClicked(String value) {
                                                OthersProfile.this.finish();
                                            }
                                        }
                                );
                            }else {

                                getDatafromprofile(ServerResponse);


                            }
                            }else {

                                getDatafromprofile(ServerResponse);
                            }

                             }
                             }else {

                                 if (userpremiumtype.equals("false")){

                                     if (premiumuser.equals("true")){

                                         CommonMethod.showAlertDialog(OthersProfile.this, "", "You Want To Be Friend To View Profile",  "Be Premium",  "Cancel", new CommonMethod.DialogClickListener() {
                                                     @Override
                                                     public void dialogOkBtnClicked(String value) {

                                                         startActivity(new Intent(OthersProfile.this,PremiumScreen.class));
                                                     }
                                                     @Override
                                                     public void dialogNoBtnClicked(String value) {
                                                         OthersProfile.this.finish();
                                                     }
                                                 }
                                         );
                                     }else {

                                         getDatafromprofile(ServerResponse);

                                     }
                                 }else {

                                     getDatafromprofile(ServerResponse);
                                 }

                             }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Show Progress Dailog
                        CommonMethod.hidepDialog();

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    }
                }

        )


        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Token "+token);
                return params;
            }
        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }


    public void AcceptedFriends(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.PUT, Api.FRIENDS+requestid+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        request.setEnabled(true);


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Hide Progress  Dailog
//                        CommonMethod.hidepDialog();

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.

//                        CommonMethod.showToast(OthersProfile.this, "");
                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("accepted", "true");
                params.put("user", userid);
                params.put("other", Otheruserid);


                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {


                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(OthersProfile.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }



    public void UnFLF(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.DELETE, UNFAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        flirt.setEnabled(true);

                        request.setEnabled(true);

                        block.setEnabled(true);
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.


                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(OthersProfile.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }



    public void VIEWEDPROFILE(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.USERVIEWEDPROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("user", userid);
                params.put("other", Otheruserid);


                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(OthersProfile.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public static String likesCountConvet(String likescount){

        int likes_count = Integer.parseInt(likescount);

        if (likes_count>=1000){

            float count = likes_count/1000.0f;

            return String.format("%.1fK",count);


        }else {

            return likescount;

        }

    }
    public void likeOnclick(){

        if(likes.equals("true")){

            likes = "false";

            likes_img.setImageResource(R.drawable.dislike);

            Integer likescountv = Integer.parseInt(like_count)-1;


            like_count = String.valueOf(likescountv);


            if (likescountv == 0 || likescountv < 0){

                likescount.setText("0");

            }else {

                likescount.setText(likesCountConvet(String.valueOf(likescountv)));

            }

            UNFAPI= Api.USERLIKE+likeid+"/";

            //to hit the api
            UnFLF();



        }else {

            likes = "true";

            likes_img.setImageResource(R.drawable.like_pink);

            Integer likescountv = Integer.parseInt(like_count)+1;

            like_count = String.valueOf(likescountv);



            likescount.setText(likesCountConvet(String.valueOf(likescountv)));

            API=Api.USERLIKE;

            ACTION = "likeimage";

            //to hit the api
            HITDATA();

        }
    }

    public void favOnclick(){

        if(favs.equals("false")){

            favs = "true";

            fav_img.setImageResource(R.drawable.fav_pink_border);

            Integer favcountv = Integer.parseInt(favou_count)+1;

            favou_count= String.valueOf(favcountv);

            favouritecount.setText(likesCountConvet(String.valueOf(favcountv)));

            API=Api.FAV;

            ACTION = "fav";

            //to hit the api
            HITDATA();

        }else {

            favs = "false";

            fav_img.setImageResource(R.drawable.fav_white);


            Integer favcountv = Integer.parseInt(favou_count)-1;

            favou_count= String.valueOf(favcountv);

            if (favcountv == 0 || favcountv < 0){

                favouritecount.setText("0");

            }else {

                favouritecount.setText(likesCountConvet(String.valueOf(favcountv)));

            }

            UNFAPI=Api.FAV+favid+"/";

            //to hit the api
            UnFLF();

        }

    }
    //to get data from profile
    public void getDatafromprofile(String ServerResponse){


        try {

        JSONObject jsondata = new JSONObject(ServerResponse);



        favou_count = String.valueOf(jsondata.getInt("other_favou_count"));

        like_count = String.valueOf(jsondata.getInt("other_like_count"));


        favouritecount.setText(likesCountConvet(favou_count));

        likescount.setText(likesCountConvet(like_count));

            JSONObject data  = jsondata.getJSONObject("profile");
            String first_name = data.getString("first_name");
            String last_name = data.getString("last_name");
            String city = data.getString("city");
            String profile = data.getString("profile_img");
            String age = data.getString("age");
            String ispremium = data.getString("is_premium");
            String status = data.getString("online");
            name.setText(first_name+" "+last_name);
            cityname.setText(city);
            if (age.equals("null")){

                agecount.setText("0");

            }else {

                agecount.setText(age);

            }
            if (ispremium.equals("true")) {

                premium.setVisibility(View.VISIBLE);

            } else {

                premium.setVisibility(View.INVISIBLE);

            }
            if(status.equals("true"))
            {
                active.setImageResource(R.drawable.active_icon);
            }
            else
            {
                active.setImageResource(R.drawable.inactive_round);
            }


            String about = data.getString("about");
            String hobby = data.getString("hobby");

            String looking_for = data.getString("looking_for");
            String religion = data.getString("religion");
            String occuption = data.getString("occupation");
            String retired = data.getString("retired");
            String marital_status = data.getString("marital_status");
            String numberof_children = data.getString("number_of_children");
            String living_at_home = data.getString("living_at_home");


            lookingforvalue.setText(looking_for);
            religionvalue.setText(religion);
            occupationvalue.setText(occuption);
            retiredvalue.setText(retired);
            martialstatusvalue.setText(marital_status);
            numberofchildrenvalue.setText(numberof_children);
            livingathomevalue.setText(living_at_home);



            if (first_name.equals("null")){

                first_name="";

            }
            if (last_name.equals("null")){

                last_name="";
            }

            if (city.equals("null")){

                city="";
            }

            if (hobby.equals("null")) {

                hobbycontent.setText("");

            } else {

                hobbycontent.setText(hobby);

            }

            if (about.equals("null")) {

                aboutus.setText("");

            } else {

                aboutus.setText(about);

            }

            if (profile.equals("null")) {


            } else {

                JSONObject prof = data.getJSONObject("profile_img");

                profilepic_s = prof.getString("img");


                // Insert the profile image from the URL into the ImageView.
                Glide.with(OthersProfile.this)
                        .load(profilepic_s)
                        .placeholder(R.drawable.single_image_dummy)
                        .dontAnimate()
                        .centerCrop().into(profilepic);


                // Insert the profile image back of image the URL into the ImageView.
                Glide.with(OthersProfile.this)
                        .load(profilepic_s)
                        .placeholder(R.drawable.single_image_dummy)
                        .dontAnimate()
                        .centerCrop().into(profileback);


            }


        String fav = jsondata.getString("user_favou");
        String like = jsondata.getString("user_like");
        String flirt = jsondata.getString("user_flirt");
        String block = jsondata.getString("user_blocked");
        String friend = jsondata.getString("user_friend");
        String request = jsondata.getString("user_request");

        String[] splitedfav = fav.split("-");

        String favuser = splitedfav[0];
        favid = splitedfav[1];

        String[] splitedlike = like.split("-");

        String likeuser = splitedlike[0];
        likeid = splitedlike[1];

        String[] splitedflirt = flirt.split("-");

        String flirtuser = splitedflirt[0];
        flirtid = splitedflirt[1];

        String[] splitedblock = block.split("-");

        String blockuser = splitedblock[0];
        blockid = splitedblock[1];


        String[] splitedrequest = request.split("-");

        String requestuser = splitedrequest[0];
        requestid = splitedrequest[1];


        String[] splitedfriends = friend.split("-");

        String friendsuser = splitedfriends[0];
        friendid = splitedfriends[1];


        if (friendsuser.equals("no"))
        {

            friend_s = "false";


            if (requestuser.equals("no")) {

                request_s = "false";

                friendicon.setImageResource(R.drawable.add_friend);

                request_tv.setText("Add Friend");

            } else if (requestuser.equals("user")) {

                request_s = "true";

                friendicon.setImageResource(R.drawable.cancel_request);

                request_tv.setText("Cancel Request");

            } else {

                request_s = "other";

                friendicon.setImageResource(R.drawable.request_received);

                request_tv.setText("Confirm Request");

            }

        } else {

            friend_s = "true";

            friendicon.setImageResource(R.drawable.unfriend);

            request_tv.setText("Friends");

        }


        if (blockuser.equals("user")) {

            block_s = "true";

            block_tv.setText("Unblock");

            blockicon.setImageResource(R.drawable.un_block_icon);

        }else if (blockuser.equals("other")){

            block_s = "true";

            block_tv.setText("Blocked");

            blockicon.setImageResource(R.drawable.block_icon);


            CommonMethod.showAlertDialog(OthersProfile.this, "", name.getText().toString()+" Blocked You", "Ok", "", new CommonMethod.DialogClickListener() {
                        @Override
                        public void dialogOkBtnClicked(String value) {

                            finish();
                        }
                        @Override
                        public void dialogNoBtnClicked(String value) {

                        }
                    }
            );
        }else {

            block_s = "false";

            block_tv.setText("Block");

            blockicon.setImageResource(R.drawable.block_icon);

        }

        if (flirtuser.equals("no")) {

            flirt_s = "false";
            flirt_smile.setImageResource(R.drawable.flirt_white_icon);

        } else {

            flirt_s = "true";
            flirt_tv.setText("Flirted");
            flirt_smile.setImageResource(R.drawable.flirted_icon);

        }

        if (favuser.equals("user")) {

            fav_img.setImageResource(R.drawable.fav_pink_border);

            favs = "true";

        } else {

            fav_img.setImageResource(R.drawable.fav_white);

            favs = "false";

        }

        if (likeuser.equals("user")) {

            likes_img.setImageResource(R.drawable.like_pink);

            likes = "true";

        } else {

            likes_img.setImageResource(R.drawable.likes_white);

            likes = "false";
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


}
