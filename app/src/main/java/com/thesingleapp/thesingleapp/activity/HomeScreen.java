package com.thesingleapp.thesingleapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
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
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.fragment.FilterDialogFragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.AboutUs_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.ContactUs_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Gallery_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Help_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Matches_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Message_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Near_by_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Notification_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Premium_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Privacyandpolicy;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Settings_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.Termsandcondition;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.connections.Favorites_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.connections.Flirt_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.connections.PhotoLike_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.connections.Request_Fragment;
import com.thesingleapp.thesingleapp.fragment.SideNavigation.connections.View_Fragment;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.service.SharedPreference;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.thesingleapp.thesingleapp.activity.LoginScreen.MY_PERMISSIONS_REQUEST_WRITE_CALENDAR;
import static com.thesingleapp.thesingleapp.activity.ProfileScreen.deleteCache;
import static com.thesingleapp.thesingleapp.fragment.SideNavigation.Matches_Fragment.screen;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, FilterDialogFragment.DialogListener, GoogleApiClient.OnConnectionFailedListener {

    DrawerLayout drawer;

    private long mLastClickTime = 0;

    private TextView toolbar_title,unreadcountmessage,unreadcountfav;

    public GoogleApiClient mGoogleApiClient;

    private StringRequest stringRequest;

    private  ImageView message,search,profile,fav,home,filter;

    private SharedPreferences.Editor filterPrefsEditor;

    private Boolean saveLogin,saveFilter;

    //Login save in share preference
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    String token,username,userimg,userid,firebasetoken,msgcount,favcount;
    //thread handler
    public static Handler handler;

    public static String thread = "no";

    private SharedPreferences filterPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //To show default fragment
        replaceFragment(new Matches_Fragment());

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);

        TextView name = headerView.findViewById(R.id.name);
        ImageView profilepic = headerView.findViewById(R.id.imageView);
        message = findViewById(R.id.imageView7);
        search = findViewById(R.id.imageView4);
        fav = findViewById(R.id.imageView6);
        profile = findViewById(R.id.imageView5);
        home = findViewById(R.id.home_b_icon);
        filter = findViewById(R.id.filter);
        toolbar_title = findViewById(R.id.toolbar_title);
        unreadcountmessage = findViewById(R.id.unreadcountmessage);
        unreadcountfav = findViewById(R.id.unreadcountfav);

        toolbar_title.setText("Matches");

        // Shared prefference intiated
        loginPreferences = getSharedPreferences("firebasetoken", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        message.setOnClickListener(this);
        search.setOnClickListener(this);
        fav.setOnClickListener(this);
        profile.setOnClickListener(this);
        home.setOnClickListener(this);
        filter.setOnClickListener(this);



        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        username = data.getUsername();
        userimg = data.getProfilepic();
        userid = data.getId();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,  this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


         if(username==null){

            name.setText(R.string.app_name);

        }else {

            name.setText(username);

        }

        if(userimg==null){

            Glide.with(HomeScreen.this)
                    .load(Api.IP+"media/"+userimg)
                    .placeholder(R.drawable.single_image_dummy)
                    .dontAnimate()
                    .centerCrop().into(profilepic);

        }else {

            Glide.with(HomeScreen.this)
                    .load(Api.IP+"media/"+userimg)
                    .placeholder(R.drawable.single_image_dummy)
                    .dontAnimate()
                    .centerCrop().into(profilepic);
        }

        //onclick listner in navigation image
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this,ProfileScreen.class));
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeScreen.this,ProfileScreen.class));
            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_sidenav_menu_icon);
        navigationView.setNavigationItemSelectedListener(this);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // getting firebase token from shared pefference
        firebasetoken = loginPreferences.getString("firebasetoken", "");

        // send firebase token to server
        Firebasetokensend();

        //check permission
        checkPermission();

        //GetMsgcount
        getCounts();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else {

            CommonMethod.showAlertDialog(HomeScreen.this, "", "Are you sure you want to exit?","Yes","No", new CommonMethod.DialogClickListener() {
                        @Override
                        public void dialogOkBtnClicked(String value) {
                            //stop the thread for count
                            stopRepeatingTask();

                            HomeScreen.this.finishAffinity();
                        }
                        @Override
                        public void dialogNoBtnClicked(String value) {

                        }
                    }
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home_screen, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_matches) {

            toolbar_title.setText(R.string.Matches);
            replaceFragment(new Matches_Fragment());
            filter.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_nearby) {

            checkconnection();

        } else if (id == R.id.nav_message) {

            toolbar_title.setText(R.string.Message);
            filter.setVisibility(View.INVISIBLE);

            replaceFragment(new Message_Fragment());

        }
        else if (id == R.id.nav_images) {

            toolbar_title.setText(R.string.Gallery);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Gallery_Fragment());

        }
        else if (id == R.id.nav_settings) {

            toolbar_title.setText(R.string.Settings);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Settings_Fragment());

        } else if (id == R.id.nav_notification) {

            toolbar_title.setText(R.string.Notification);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Notification_Fragment());

        } else if (id == R.id.nav_view) {

            toolbar_title.setText(R.string.View);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new View_Fragment());

        } else if (id == R.id.nav_favorites) {

            toolbar_title.setText(R.string.Favorites);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Favorites_Fragment());

        } else if (id == R.id.nav_flirt) {

            toolbar_title.setText(R.string.Flirt);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Flirt_Fragment());

        }
        else if (id == R.id.nav_request) {

            toolbar_title.setText(R.string.Request);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Request_Fragment());

        }else if (id == R.id.nav_photolike) {

            toolbar_title.setText(R.string.Photo);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new PhotoLike_Fragment());

        } else if (id == R.id.nav_help) {

            toolbar_title.setText(R.string.Help);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Help_Fragment());

        } else if (id == R.id.nav_contactus) {

            toolbar_title.setText(R.string.FeedBack);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new ContactUs_Fragment());

        } else if (id == R.id.nav_premium) {

            toolbar_title.setText(R.string.Premium);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Premium_Fragment());

        } else if (id == R.id.nav_aboutus) {

            toolbar_title.setText(R.string.AboutUs);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new AboutUs_Fragment());

        }
        else if (id == R.id.nav_termsndcondition) {

            toolbar_title.setText(R.string.Terms);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Termsandcondition());

        }
        else if (id == R.id.nav_privacypolicy) {

            toolbar_title.setText(R.string.Privacy);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Privacyandpolicy());

        }else if (id == R.id.nav_delete) {


            CommonMethod.showAlertDialog(HomeScreen.this, "", "Are you sure you want to Delete Account?","Yes", "No", new CommonMethod.DialogClickListener() {
                        @Override
                        public void dialogOkBtnClicked(String value) {

                            // delete account
                            internetCheck();
                        }
                        @Override
                        public void dialogNoBtnClicked(String value) {

                        }
                    }
            );

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void deleteAccount(){

        //Show Progress Dailog
        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.PATCH, Api.USER+userid+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //stop the thread for count
                         stopRepeatingTask();
                        deleteCache(HomeScreen.this);
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


                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.

                        CommonMethod.showToast(HomeScreen.this, "Cant Able to Delete the Account");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.


                params.put("is_active", "false");
                params.put("deleted_by", "U");
                params.put("deleted_date", String.valueOf(date));

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
        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMan.beginTransaction();
        fragTrans.replace(R.id.content, fragment, "MY_FRAGMENT");
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.imageView7:
//                message.setEnabled(false);

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                message.setImageResource(R.drawable.message_pink);
                fav.setImageResource(R.drawable.riple_favourite_effect);
                profile.setImageResource(R.drawable.riple_profile_effect);
                search.setImageResource(R.drawable.riple_search_effect);
                mLastClickTime = SystemClock.elapsedRealtime();
                filter.setVisibility(View.INVISIBLE);
                toolbar_title.setText("Message");
                filter.setVisibility(View.INVISIBLE);
                //To show message fragment
                replaceFragment(new Message_Fragment());
                message.setClickable(false);
                if(fav.isClickable() )
                {

                }
                else
                {
                    fav.setClickable(true);

                }
                if(home.isClickable() )
                {

                }
                else
                {
                    home.setClickable(true);

                }
                break;

            case R.id.imageView6:
                fav.setImageResource(R.drawable.favorite_pink);
                message.setImageResource(R.drawable.riple_message_effect);
                profile.setImageResource(R.drawable.riple_profile_effect);
                search.setImageResource(R.drawable.riple_search_effect);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                filter.setVisibility(View.INVISIBLE);
                toolbar_title.setText("Favorites");
                filter.setVisibility(View.INVISIBLE);

                //updating server fav count in local
                loginPrefsEditor.putString("favcount", favcount);
                loginPrefsEditor.commit();
                unreadcountfav.setVisibility(View.INVISIBLE);
                fav.setClickable(false);
                //To show fav fragment
                replaceFragment(new Favorites_Fragment());
                if(message.isClickable() )
                {

                }
                else
                {
                    message.setClickable(true);

                }
                if(home.isClickable() )
                {

                }
                else
                {
                    home.setClickable(true);

                }
                break;


            case R.id.imageView5:

                profile.setImageResource(R.drawable.user_pink);
                message.setImageResource(R.drawable.riple_message_effect);
                fav.setImageResource(R.drawable.riple_favourite_effect);
                search.setImageResource(R.drawable.riple_search_effect);
                if(fav.isClickable() )
                {

                }
                else
                {
                    fav.setClickable(true);

                }
                if(message.isClickable() )
                {

                }
                else
                {
                    message.setClickable(true);

                }
                if(home.isClickable() )
                {

                }
                else
                {
                    home.setClickable(true);

                }

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
               startActivity(new Intent(HomeScreen.this,ProfileScreen.class));


                break;


            case R.id.imageView4:

                search.setImageResource(R.drawable.search_pink);
                profile.setImageResource(R.drawable.riple_profile_effect);
                message.setImageResource(R.drawable.riple_message_effect);
                fav.setImageResource(R.drawable.riple_favourite_effect);

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(fav.isClickable() )
                {

                }
                else
                {
                    fav.setClickable(true);

                }
                if(message.isClickable() )
                {

                }
                else
                {
                    message.setClickable(true);

                }
                if(home.isClickable() )
                {

                }
                else
                {
                    home.setClickable(true);

                }
                startActivity(new Intent(HomeScreen.this,SearchScreen.class));


                break;


            case R.id.home_b_icon:

                search.setImageResource(R.drawable.riple_search_effect);
                profile.setImageResource(R.drawable.riple_profile_effect);
                message.setImageResource(R.drawable.riple_message_effect);
                fav.setImageResource(R.drawable.riple_favourite_effect);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                toolbar_title.setText("Matches");
                filter.setVisibility(View.VISIBLE);
                home.setClickable(false);
                //To show default fragment
                replaceFragment(new Matches_Fragment());
                if(fav.isClickable() )
                {

                }
                else
                {
                    fav.setClickable(true);

                }
                if(message.isClickable() )
                {

                }
                else
                {
                    message.setClickable(true);

                }
                break;

            case R.id.filter:


                screen = "Home";

                FilterDialogFragment dialogFragment = new FilterDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("notAlertDialog", true);

                dialogFragment.setArguments(bundle);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                dialogFragment.show(ft, "dialog");

                break;
        }
    }

    // filterdailoge values return
    @Override
    public void onFinishEditDialog(String inputText) {

        if (TextUtils.isEmpty(inputText)) {
//            textView.setText("Email was not entered");
        } else{
//            textView.setText("Email entered: " + inputText);
    }
}

    public void Firebasetokensend(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.DEVICEID,
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

                params.put("registration_id", firebasetoken);
                params.put("active", "true");
                params.put("type", "android");


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
        RequestQueue requestQueue = Volley.newRequestQueue(HomeScreen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    public void getCounts(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.USER+userid+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);

                            msgcount = jsondata.getString("unread_msg_count");

                             favcount = jsondata.getString("user_favou_count");

                            if(loginPreferences.getString("favcount", "").equals("")){

                                if (favcount.equals("0")){

                                    unreadcountfav.setVisibility(View.INVISIBLE);

                                    loginPrefsEditor.putString("favcount", favcount);
                                    loginPrefsEditor.commit();

                                }else {
                                    unreadcountfav.setVisibility(View.VISIBLE);
                                    unreadcountfav.setText(favcount);

                                }
                            }else {

                               int favcountfromsum =  Integer.parseInt(favcount)-Integer.parseInt(loginPreferences.getString("favcount", ""));

                               if (favcountfromsum>0){

                                   unreadcountfav.setVisibility(View.VISIBLE);
                                   unreadcountfav.setText(String.valueOf(favcountfromsum));

                               }else {

                                   loginPrefsEditor.putString("favcount", favcount);
                                   loginPrefsEditor.commit();
                                   unreadcountfav.setVisibility(View.INVISIBLE);

                               }

                            }


                            if (msgcount.equals("0")) {

                                unreadcountmessage.setVisibility(View.INVISIBLE);

                            }else {

                                unreadcountmessage.setVisibility(View.VISIBLE);
                                unreadcountmessage.setText(msgcount);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        ) {
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

    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(HomeScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    CommonMethod.showAlertDialog(HomeScreen.this, "Permission necessary", "To Get Matches", "Yes", "No", new CommonMethod.DialogClickListener() {
                                @Override
                                public void dialogOkBtnClicked(String value) {

                                    ActivityCompat.requestPermissions(HomeScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                                }
                                @Override
                                public void dialogNoBtnClicked(String value) {

                                }
                            }
                    );


                } else {

                    ActivityCompat.requestPermissions(HomeScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                }

                return false;

            } else {

                return true;

            }
        } else {

            return true;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }


    public void stopRepeatingTask()
    {
        if(thread.equals("yes")) {
            handler.removeCallbacksAndMessages(null);
            thread = "no";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                thread="yes";

                //GetMsgcount
                getCounts();

                handler.postDelayed(this, 10000);//300000=5mins

            }
        }, 10000);//300000=5mins

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            toolbar_title.setText(R.string.Near);
            filter.setVisibility(View.INVISIBLE);
            replaceFragment(new Near_by_Fragment());

        } else {

            stopRepeatingTask();
            CommonMethod.showToast(HomeScreen.this, "Please Check the internet connection");

        }
    }

    public void internetCheck(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            CommonMethod.showpDialog(HomeScreen.this,"Deleting The Account...");
            logout();

        } else {

            stopRepeatingTask();
            CommonMethod.showToast(HomeScreen.this, "Please Check the internet connection");

        }
    }



    private void logout() {


        String social = UserData.logintype;
        //Hide Progress  Dailog
        CommonMethod.hidepDialog();

        if (social.equals("gmail")) {


            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {


                        }
                    });


            //Logout
            deleteAccount();


        } else if (social.equals("normal")) {

            deleteCache(this);

            // Set LoggedIn status to false
            SharedPreference.setLoggedIn(HomeScreen.this, false);

            //Logout
            deleteAccount();


        } else {


            CommonMethod.showToast(HomeScreen.this, "Cant find Login");

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });

            //Logout
            deleteAccount();
        }
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
