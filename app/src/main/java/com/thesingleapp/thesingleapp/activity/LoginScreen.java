package com.thesingleapp.thesingleapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.service.SharedPreference;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static android.Manifest.permission.CALL_PHONE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static com.thesingleapp.thesingleapp.activity.ProfileScreen.deleteCache;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;

    private static final String TAG = LoginScreen.class.getSimpleName();

    private static final int RC_SIGN_IN = 007;

    public GoogleApiClient mGoogleApiClient;

    String gmailtoken, personNamesocial,personPhotoUrlsocial,emailsocial;

    //Login save in share preference
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    private CheckBox remember;
    private TextView forgetpassword,signup,gmaillogin;
    private LinearLayout submit;
    private EditText mobilenumber,password;
    FirebaseAuth mAuth;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        signup = findViewById(R.id.textView3);
        forgetpassword = findViewById(R.id.textView2);
        mobilenumber = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        submit = findViewById(R.id.button);
        gmaillogin = findViewById(R.id.button3);
        remember = findViewById(R.id.checkBox);

        submit.setOnClickListener(this);
        gmaillogin.setOnClickListener(this);
        forgetpassword.setOnClickListener(this);
        signup.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        // Shared prefference intiated
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);

        if (saveLogin == true) {
            mobilenumber.setText(loginPreferences.getString("username", ""));
            password.setText(loginPreferences.getString("password", ""));
        }

        //Automatic login if looged
        if(SharedPreference.getLoggedStatus(getApplicationContext())) {
            submit.setEnabled(false);
            gmaillogin.setEnabled(false);
            forgetpassword.setEnabled(false);
            signup.setEnabled(false);
            mobilenumber.setText(loginPreferences.getString("username", ""));
            password.setText(loginPreferences.getString("password", ""));
            SendNormalLoginData();

        } else {


        }

        //check internet connection
        checkConnection();

        //check permission
        checkPermission();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(LoginScreen.this);


        //gmail login
        String serverClientId = "124537367019-i3eg36cfasinjk6m2q0neosib8a6eh81.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //set click on  check box to remeber the data
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                                                    if(isChecked) {

                                                        loginPrefsEditor.putBoolean("saveLogin", true);
                                                        loginPrefsEditor.putString("username", mobilenumber.getText().toString());
                                                        loginPrefsEditor.putString("password", password.getText().toString());
                                                        loginPrefsEditor.commit();

                                                    }else {

                                                        loginPrefsEditor.clear();
                                                        loginPrefsEditor.commit();

                                                    }
                                                }
                                            }
        );

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.button:

                SendNormalLoginData();

                break;

            case R.id.textView2:

                startActivity(new Intent(LoginScreen.this,ForgetPasswordScreen.class));

                break;

            case R.id.button3:

                GmailsignIn();

                break;

            case R.id.textView3:

                startActivity(new Intent(LoginScreen.this,SignupScreen.class));

                break;

        }
    }


    //gmail  login coding
    private void GmailsignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }



    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            // comment for demo on jan 3
            gmailtoken = acct.getServerAuthCode();

            if (acct.getDisplayName()!= null) {
                personNamesocial = acct.getDisplayName();
            } else {
                personNamesocial = "";
            }

            if (acct.getPhotoUrl() != null) {
                personPhotoUrlsocial = acct.getPhotoUrl().toString();
            } else {
                personPhotoUrlsocial = "";
            }

            if (!acct.getEmail().equals("")) {
                emailsocial = acct.getEmail();
            } else {
                emailsocial = "";
            }

            //Sending data to server
            SendGmailLoginData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {

                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }


    public  boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    CommonMethod.showAlertDialog(LoginScreen.this, "Permission Necessary","To get Matches", "Yes", "", new CommonMethod.DialogClickListener() {
                                @Override
                                public void dialogOkBtnClicked(String value) {
                                    ActivityCompat.requestPermissions(LoginScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
                                }

                                @Override
                                public void dialogNoBtnClicked(String value) {

                                }
                            }
                    );
                } else {

                    ActivityCompat.requestPermissions(LoginScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                }

                return false;

            } else {

                return true;

            }
        } else {

            return true;

        }
    }


    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {


        } else {

            CommonMethod.showToast(LoginScreen.this, "Please Check the internet connection");

        }
    }


    public void SendGmailLoginData(){

        //Show Progress Dailog
        CommonMethod.showpDialog(this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.SIGNUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        try {
                            JSONObject jsondata = new JSONObject(ServerResponse);

                            String status = jsondata.getString("status");

                            if(status.equalsIgnoreCase("validation error")) {

                                CommonMethod.showToast(LoginScreen.this, "Already Registered! Please Login");

                            }else if(status.equalsIgnoreCase("Something went wrong")){

                                CommonMethod.showToast(LoginScreen.this, "Please Check Your Fields");

                            }else if (status.equals("Email already exist")){

                                CommonMethod.showToast(LoginScreen.this, "Email Id Already Used");

                            }
                            else if(status.equalsIgnoreCase("User is blocked by admin")){

                                CommonMethod.showAlertDialog(LoginScreen.this, "", "You Are Blocked By The Single App Admin,Please Contact Customer Care", "Customer Care", "No Need", new CommonMethod.DialogClickListener() {
                                            @Override
                                            public void dialogOkBtnClicked(String value) {
                                                Intent i = new Intent(Intent.ACTION_CALL);
                                                i.setData(Uri.parse("tel:7135896496"));

                                                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                    startActivity(i);
                                                } else {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{CALL_PHONE}, 1);
                                                    }
                                                }

                                            }

                                            @Override
                                            public void dialogNoBtnClicked(String value) {
                                                LoginScreen.this.finishAffinity();
                                            }
                                        }
                                );
                            }else if(status.equalsIgnoreCase("User is Deleted")){

                                CommonMethod.showAlertDialog(LoginScreen.this, "", "You Had Deleted The Account,If You Need to Restore The Account, Please Contact Customer Care","Customer Care", "No Need", new CommonMethod.DialogClickListener() {
                                            @Override
                                            public void dialogOkBtnClicked(String value) {
                                                Intent i = new Intent(Intent.ACTION_CALL);
                                                i.setData(Uri.parse("tel:7135896496"));

                                                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                    startActivity(i);
                                                } else {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{CALL_PHONE}, 1);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void dialogNoBtnClicked(String value) {
                                                logout();

                                            }
                                        }
                                );

                            } else {


                                JSONObject statusobject = jsondata.getJSONObject("status");

                                String id = statusobject.getString("id");
                                String token = statusobject.getString("token");
                                String gender = statusobject.getString("gender");
                                String profileid = statusobject.getString("Profile_id");
                                String userimg = statusobject.getString("img");
                                String premium = statusobject.getString("is_premium");

                                UserDataModel data = UserDataModel.getInstance();
                                data.setId(id);
                                data.setToken(token);
                                data.setUsername(personNamesocial);
                                data.setPremium(premium);
                                data.setProfilepic(userimg);
                                data.setProfileid(profileid);

                                UserData.logintype ="gmail";

                                if (gender.equals("")){

                                    Intent intent = new Intent(getApplicationContext(), SignupgmailScreen.class);
                                    startActivity(intent);

                                }else {

                                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                    startActivity(intent);

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

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(LoginScreen.this, "Please Check the internet connection");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {


                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

//                 Adding All values to Params.

                params.put("first_name", personNamesocial);
                params.put("last_name", "");
                params.put("username", emailsocial);
                params.put("email", emailsocial);
                params.put("phone", emailsocial);
                params.put("password", "test123");
                params.put("login_by","E");
                params.put("gender","");
                params.put("seeking_for","B");
                params.put("is_active","true");
                params.put("domain","M");
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(LoginScreen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public void SendNormalLoginData(){

        //Show Progress Dailog
        CommonMethod.showpDialog(LoginScreen.this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();


                        try {
                            JSONObject jsondata = new JSONObject(ServerResponse);


                            String status = jsondata.getString("status");


                            if(status.equalsIgnoreCase("validation error")) {

                                CommonMethod.showToast(LoginScreen.this, "Please Sign Up");


                            }else if(status.equalsIgnoreCase("User authendication failed")){


                                CommonMethod.showToast(LoginScreen.this, "Password Is Wrong");

                            }
                            else if (status.equals("User does not exist")){

                                CommonMethod.showToast(LoginScreen.this, "You need to sign up first..Please Sign Up");

                            }else if(status.equalsIgnoreCase("User is blocked by admin")){

                                CommonMethod.showAlertDialog(LoginScreen.this, "", "You Are Blocked By The Single App Admin,Please Contact Customer Care", "Customer Care", "No Need", new CommonMethod.DialogClickListener() {
                                            @Override
                                            public void dialogOkBtnClicked(String value) {

                                                Intent i = new Intent(Intent.ACTION_CALL);
                                                i.setData(Uri.parse("tel:7135896496"));

                                                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                    startActivity(i);
                                                } else {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{CALL_PHONE}, 1);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void dialogNoBtnClicked(String value) {

                                                LoginScreen.this.finishAffinity();
                                            }
                                        }
                                );
                            }else if(status.equalsIgnoreCase("User is Deleted")){

                                CommonMethod.showAlertDialog(LoginScreen.this, "", "You Had Deleted The Account,If You Need to Restore The Account, Please Contact Customer Care", "Customer Care", "No Need", new CommonMethod.DialogClickListener() {
                                            @Override
                                            public void dialogOkBtnClicked(String value) {

                                                Intent i = new Intent(Intent.ACTION_CALL);
                                                i.setData(Uri.parse("tel:7135896496"));

                                                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                                    startActivity(i);
                                                } else {
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        requestPermissions(new String[]{CALL_PHONE}, 1);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void dialogNoBtnClicked(String value) {
                                                LoginScreen.this.finishAffinity();
                                            }
                                        }
                                );
                            }else if(status.equalsIgnoreCase("User is not active")){

                                startActivity(new Intent(LoginScreen.this,VerficationScreen.class).putExtra("number",mobilenumber.getText().toString()));

                            }else {

                                JSONObject statusobject = jsondata.getJSONObject("status");


                                String id = statusobject.getString("id");
                                String token = statusobject.getString("token");
                                String activate = statusobject.getString("is_active");
                                String username = statusobject.getString("user");
                                String profileid = statusobject.getString("profile");
                                String userimg = statusobject.getString("image");
                                String premium = statusobject.getString("is_premium");


                                UserDataModel data = UserDataModel.getInstance();
                                data.setId(id);
                                data.setToken(token);
                                data.setUsername(username);
                                data.setPremium(premium);
                                data.setProfilepic(userimg);
                                data.setProfileid(profileid);

                                //to make login session maintain
                                loginPrefsEditor.putString("username", mobilenumber.getText().toString());
                                loginPrefsEditor.putString("password", password.getText().toString());
                                loginPrefsEditor.commit();


                                UserData.logintype ="normal";

                                if (activate.equals("true")) {

                                    // Set Logged In statue to 'true'
                                    SharedPreference.setLoggedIn(getApplicationContext(), true);

                                    Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                }else {

                                    // Set Logged In statue to 'true'
                                    SharedPreference.setLoggedIn(getApplicationContext(), true);

                                    Intent intent = new Intent(getApplicationContext(), VerficationScreen.class);
                                    intent.putExtra("number",mobilenumber.getText().toString());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

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

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(LoginScreen.this,"Please Check Internet");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.


                params.put("username", mobilenumber.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(LoginScreen.this);

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    // on back button click
    @Override
    public void onBackPressed() {


        CommonMethod.showAlertDialog(LoginScreen.this, "", " Are you sure you want to exit?", "Yes", "No", new CommonMethod.DialogClickListener() {
                    @Override
                    public void dialogOkBtnClicked(String value) {

                        LoginScreen.this.finishAffinity();
                    }
                    @Override
                    public void dialogNoBtnClicked(String value) {

                    }
                }
        );
    }


    private void logout() {

        String social = UserData.logintype;

        if (social.equals("gmail")) {


            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {


                        }
                    });

        } else if (social.equals("normal")) {

            deleteCache(this);

            // Set LoggedIn status to false
            SharedPreference.setLoggedIn(LoginScreen.this, false);

        } else {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {

                        }
                    });
        }
    }


}
