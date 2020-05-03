package com.thesingleapp.thesingleapp.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.adapter.GalleryAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Galley_model;
import com.thesingleapp.thesingleapp.service.SharedPreference;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mGoogleApiClient;

    //Login save in share preference
    private SharedPreferences loginPreferences,filterPreferences;
    private SharedPreferences.Editor loginPrefsEditor,filterPrefsEditor;
    private Boolean saveLogin,saveFilter;

    private Button logoutbtn, back;

    private GalleryAdapter myGalleryAdapter;

    private RecyclerView mRecyclerView;

    private ImageView profilepic, icon_delete, backbutton,profileback,icon_delete1;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    public TextView agecount, favcount, likescount, dob, emailid, password, phonenumber, premium, city,lookingforvalue,religionvalue,occupationvalue,retiredvalue,martialstatusvalue,numberofchildrenvalue,livingathomevalue;

    public EditText aboutcontent, hobbycontent, name;

    public LinearLayout updateaccount;

    private static String profileadd = "no";

    private StringRequest stringRequest;

    private LinearLayout linear,linear1;

    byte[] byteArray;

    List<Galley_model> mGalleryList;
    Galley_model mGalley_model;

    String token, userid, profilepic_s,social,firstname,lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        lookingforvalue = findViewById(R.id.lookingforvalue);
        religionvalue = findViewById(R.id.religionvalue);
        occupationvalue = findViewById(R.id.occupationvalue);
        retiredvalue = findViewById(R.id.retiredvalue);
        martialstatusvalue = findViewById(R.id.martialstatusvalue);
        numberofchildrenvalue = findViewById(R.id.numberofchildrenvalue);
        livingathomevalue = findViewById(R.id.livingathomevalue);
        agecount = findViewById(R.id.agecount);
        linear = findViewById(R.id.message);
        linear1= findViewById(R.id.request);
        favcount = findViewById(R.id.favouritecount);
        likescount = findViewById(R.id.likescount);
        city = findViewById(R.id.city);
        name = findViewById(R.id.name);
        dob = findViewById(R.id.dob);
        premium = findViewById(R.id.premium);
        phonenumber = findViewById(R.id.phonenumber);
        emailid = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        aboutcontent = findViewById(R.id.aboutcontent);
        hobbycontent = findViewById(R.id.hobbycontent);
        logoutbtn = findViewById(R.id.button4);
        profilepic = findViewById(R.id.imageview);
        icon_delete = findViewById(R.id.icon_delete);
        icon_delete1 = findViewById(R.id.icon_delete1);
        backbutton = findViewById(R.id.backbutton);
        profileback = findViewById(R.id.profileback);
        updateaccount = findViewById(R.id.update);
        logoutbtn.setOnClickListener(this);
        updateaccount.setOnClickListener(this);
        backbutton.setOnClickListener(this);
        premium.setOnClickListener(this);
        icon_delete1.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.recyclerview_images);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mGalleryList = new ArrayList<>();

        social= UserData.logintype;


        if (social.equals("gmail"))
        {
            linear.setVisibility(View.GONE);
            linear1.setVisibility(View.GONE);
        }

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        //gmail login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //for Profile update checking
        profileadd = "no";


        //Get Profile Data
        getProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mGalleryList.isEmpty()){

        }else {

            mGalleryList.clear();
            getProfileData();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button4:

                CommonMethod.showAlertDialog(ProfileScreen.this, "Logout", "Are You sure,Do you want to logout",  "Yes, Logout",  "Cancel", new CommonMethod.DialogClickListener() {
                            @Override
                            public void dialogOkBtnClicked(String value) {

                                logout();

                            }
                            @Override
                            public void dialogNoBtnClicked(String value) {

                            }
                        }
                );



                break;


            case R.id.backbutton:

                finish();


                break;


            case R.id.update:

                startActivity(new Intent(ProfileScreen.this, ProfileUpdateScreen.class));

                break;

            case R.id.icon_delete1:

                startActivity(new Intent(ProfileScreen.this, ChangePasswordScreen.class));

                break;

            case R.id.premium:

                startActivity(new Intent(ProfileScreen.this, PremiumScreen.class));

                break;
        }
    }


    private void logout() {




        if (social.equals("gmail")) {


            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });

            //Logout
            Logout();


        } else if (social.equals("normal")) {

            deleteCache(this);

            // Set LoggedIn status to false
            SharedPreference.setLoggedIn(getApplicationContext(), false);

            //Logout
            Logout();


        } else {


            CommonMethod.showToast(ProfileScreen.this, "Cant find Login");

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });

            //Logout
            Logout();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }

}


    public void Logout(){


        //Show Progress Dailog
        CommonMethod.showpDialog(ProfileScreen.this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();


                        //clear shared prefferencce data
                        clearSharedprefference();
                        deleteCache(ProfileScreen.this);
                        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        deleteCache(ProfileScreen.this);
                        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(ProfileScreen.this, "Please Check your Internet");
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
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileScreen.this);

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    //get data from profile
    public void getProfileData()
    {

        //Show Progress Dailog
        CommonMethod.showpDialog(this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.USER+userid+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Show Progress Dailog
                        CommonMethod.hidepDialog();


                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);


                            String  favcountv = jsondata.getString("user_favou_count");


                            String  likecountv = jsondata.getString("user_like_count");


                            JSONObject data = jsondata.getJSONObject("profile");


                            for (int i = data.length()-1; i>=0; i--) {

                                String first_name = data.getString("first_name");
                                String last_name = data.getString("last_name");
                                String cityv = data.getString("city");
                                String profile = data.getString("profile_img");
                                String ispremium = data.getString("is_premium");
                                String dobv = data.getString("dob");
                                String age = data.getString("age");
                                String email = data.getString("email");
                                String about = data.getString("about");
                                String hobby = data.getString("hobby");
                                String phone_no = data.getString("phone_no");
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
                                favcount.setText(favcountv);
                                likescount.setText(likecountv);

                                if(age.equals("null")){

                                    agecount.setHint("");

                                }else {

                                    agecount.setText(age);

                                }



                                if(dobv.equals("null")){

                                    dob.setHint("Date Of Birth");

                                }else {

                                    dob.setText(dobv);

                                }


                                if(email.equals("null")){

                                    emailid.setHint("Email Id");

                                }else {

                                    emailid.setText(email);

                                }

                                if(phone_no.equals("null")){

                                    phonenumber.setHint("Phone Number");

                                }else {

                                    phonenumber.setText(phone_no);

                                }

                                if(cityv.equals("null")){

                                    city.setHint("City");

                                }else {

                                    city.setText(cityv);

                                }

                                if (ispremium.equals("false")){

                                premium.setText("Not Premium");

                                }else {

                                    premium.setText("Premium");

                                }

                                if (about.equals("null")){

                                    aboutcontent.setText("");

                                }else {
                                    aboutcontent.setText(about);
                                }

                                if (hobby.equals("null")){

                                    hobbycontent.setText("");

                                }else {

                                    hobbycontent.setText(hobby);

                                }

                                if (first_name.equals("null")){

                                    first_name="";
                                }
                                if (last_name.equals("null")){

                                    last_name="";
                                }

                                name.setText(first_name+" "+last_name);

                                if(profile.equals("null")){


                                }else {

                                    JSONObject prof = data.getJSONObject("profile_img");

                                    profilepic_s = prof.getString("img");

                                    // Insert the profile image from the URL into the ImageView.
                                    Glide.with(ProfileScreen.this)
                                            .load(profilepic_s)
                                            .placeholder(R.drawable.single_image_dummy)
                                            .dontAnimate()
                                            .centerCrop().into(profilepic);


                                    // Insert the profile image back of background the URL into the ImageView.
                                    Glide.with(ProfileScreen.this)
                                            .load(profilepic_s)
                                            .placeholder(R.drawable.single_image_dummy)
                                            .dontAnimate()
                                            .centerCrop().into(profileback);
                                }


                            }

                            JSONArray imagedata = jsondata.getJSONArray("images");

                            for (int i = imagedata.length()-1; i>=0; i--) {

                                JSONObject image = imagedata.getJSONObject(i);

                                String id = image.getString("id");
                                String profilepic = image.getString("img");
                                JSONArray likescountv = image.getJSONArray("likeimage");
                                JSONArray commentcountv = image.getJSONArray("commentimage");
                                String likecount = String.valueOf(likescountv.length());
                                String commentcount = String.valueOf(commentcountv.length());


                                mGalley_model = new Galley_model(userid,id,profilepic,likecount, commentcount);
                                mGalleryList.add(mGalley_model);

                            }

                            //adding adapter to recyclerview
                            myGalleryAdapter = new GalleryAdapter(ProfileScreen.this, mGalleryList);
                            mRecyclerView.setAdapter(myGalleryAdapter);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    myGalleryAdapter.showshimmer = false;

                                    myGalleryAdapter.notifyDataSetChanged();
                                }
                            }, 1500);

                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    myGalleryAdapter.showshimmer = false;

                                    myGalleryAdapter.notifyDataSetChanged();
                                }
                            }, 1500);
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

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(ProfileScreen.this, "Please Check Your Internet Connection");
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

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    public void clearSharedprefference(){


        // Shared prefference intiated
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        // Shared prefference intiated
        filterPreferences = getSharedPreferences("filterdata", MODE_PRIVATE);
        filterPrefsEditor = filterPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }
        saveFilter = filterPreferences.getBoolean("saveFilter", false);
        if (saveFilter == true) {
            filterPrefsEditor.clear();
            filterPrefsEditor.commit();
        }


    }

}
