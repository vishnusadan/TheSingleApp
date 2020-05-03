package com.thesingleapp.thesingleapp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.service.VolleyMultipartRequest;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import static com.thesingleapp.thesingleapp.activity.LoginScreen.MY_PERMISSIONS_REQUEST_WRITE_CALENDAR;

public class ProfileUpdateScreen extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mGoogleApiClient;

    private Button back;

    private ImageView profilepic,icon_delete,backbutton,backimage;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    public TextView agecount,favcount,likescount,emailid,password,phonenumber,premium,city;

    public EditText aboutcontent,hobbycontent,name,date,month,year,lookingforvalue,religionvalue,occupationvalue,retiredvalue,martialstatusvalue,numberofchildrenvalue,livingathomevalue;

    public LinearLayout updateaccount;

    private LinearLayout linear,linear1;

    private static String profileadd = "no",social;

    private int mYear, mMonth, mDay,currentyear,minmum,maximum;

    private StringRequest stringRequest;

    byte[] byteArray;

    Uri outputFileUri;

    private static final int PICK_FROM_CAMERA = 1;

    public static String  token,userid,profilepic_s,profileid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update_screen);

        agecount = findViewById(R.id.agecount);
        favcount = findViewById(R.id.favouritecount);
        likescount = findViewById(R.id.likescount);
        city = findViewById(R.id.city);
        lookingforvalue = findViewById(R.id.lookingforvalue);
        religionvalue = findViewById(R.id.religionvalue);
        occupationvalue = findViewById(R.id.occupationvalue);
        retiredvalue = findViewById(R.id.retiredvalue);
        martialstatusvalue = findViewById(R.id.martialstatusvalue);
        numberofchildrenvalue = findViewById(R.id.numberofchildrenvalue);
        livingathomevalue = findViewById(R.id.livingathomevalue);
        name = findViewById(R.id.name);
        date = findViewById(R.id.month);
        month = findViewById(R.id.datepicker);
        year = findViewById(R.id.year);
        linear = findViewById(R.id.message);
        linear1 = findViewById(R.id.request);
        premium = findViewById(R.id.premium);
        phonenumber = findViewById(R.id.phonenumber);
        emailid = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        aboutcontent = findViewById(R.id.aboutcontent);
        hobbycontent = findViewById(R.id.hobbycontent);
        profilepic = findViewById(R.id.imageview);
        icon_delete = findViewById(R.id.icon_delete);
        backbutton = findViewById(R.id.backbutton);
        backimage = findViewById(R.id.backimage);
        updateaccount = findViewById(R.id.update);
        profilepic.setOnClickListener(this);
        updateaccount.setOnClickListener(this);
        backbutton.setOnClickListener(this);

        social= UserData.logintype;
        currentyear= Calendar.getInstance().get(Calendar.YEAR);
        minmum = currentyear - 12;
        maximum=currentyear-100;
        if (social.equals("gmail"))
        {
            linear.setVisibility(View.GONE);
            linear1.setVisibility(View.GONE);
        }
        else
        {

        }

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();
        profileid=data.getProfileid();


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

        //check permission
        checkPermission();


        //Get Profile Data
        getProfileData();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);

            return;

        }

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){


            case R.id.imageview:

                selectImage();

                break;

            case R.id.backbutton:

                if (profileadd.equals("yes")){

                    CommonMethod.showAlertDialog(ProfileUpdateScreen.this, "","Did you want to Save the Changes?", "Yes", "No", new CommonMethod.DialogClickListener() {
                                @Override
                                public void dialogOkBtnClicked(String value) {

                                    uploadMultipart(bitmap);
                                }
                                @Override
                                public void dialogNoBtnClicked(String value) {
                                    finish();
                                }
                            }
                    );

                }else {

                    finish();

                }

                break;



            case R.id.update:

                if (UserData.SIGNUPMALEFEMALE.equals("")&& name.getText().toString().equals("")){

                    name.setError("Please Give Name");

                }else if (date.getText().toString().equals("")) {

                    date.setError("Please Give your D.O.B");

                }else if (Integer.parseInt(year.getText().toString())<maximum || Integer.parseInt(year.getText().toString())>minmum || year.getText().toString().length()>4){

                    year.setError("You are Below our Age Restriction");

                }else if (Integer.parseInt(month.getText().toString())<1 || Integer.parseInt(month.getText().toString())>12 || month.getText().toString().length()>2){

                    month.setError("Please Enter valid Month");

                }else if (Integer.parseInt(date.getText().toString())<1 || Integer.parseInt(date.getText().toString())>31 || date.getText().toString().length()>2){

                    date.setError("Please Enter valid Date");

                }else {

                    uploadMultipart(bitmap);

                }
                break;

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //image from gallery

    private void selectImage() {


        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };


        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogeTheme);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))
                {
                    //to know user click on image view or not

                    // Checking Permission for Android M and above
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(ProfileUpdateScreen.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_CAMERA);
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {


                        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);
                        m_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(m_intent, 1);
//
                    } else
                    {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        outputFileUri = Uri.fromFile(file);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(captureIntent, 1);
                    }


                }

                else if (options[item].equals("Choose from Gallery"))

                {
                    //to know user click on image view or not

                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                }

                else if (options[item].equals("Cancel")) {


                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }



    @TargetApi(Build.VERSION_CODES.N)
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                profileadd="yes";

                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), this.getApplicationContext().getPackageName() + ".provider", file);
                Bitmap bitmap= null;
                try
                {
                    bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }
                profilepic.setImageBitmap(bitmap);

            } else if (requestCode == 2) {


                profileadd="yes";

                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);

                c.close();

                bitmap = (BitmapFactory.decodeFile(picturePath));

                profilepic.setImageBitmap(bitmap);

            }

        }
    }


    /*
     * This is the method responsible for image upload
     * We need the full image path and the name for the image in this method
     * */

    public void uploadMultipart(final Bitmap bitmap) {

        if(profileadd.equals("yes")){

            // For get Drawable from Image
            Drawable d = profilepic.getDrawable();

            Bitmap bitmapOrg = ((BitmapDrawable)d).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byteArray = stream.toByteArray();

        }else {

//          GlideBitmapDrawable  BitmapDrawable
            Bitmap bmp = ((GlideBitmapDrawable)profilepic.getDrawable().getCurrent()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byteArray = stream.toByteArray();


        }

        CommonMethod.showpDialog(this,"Updating...");

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.PUT, Api.PROFILEADD+profileid+"/?img="+profileadd,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        CommonMethod.hidepDialog();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));


                            String status = obj.getString("status");


                            if (status.equals("update successfully")) {

                                String profileimg = obj.getString("pro_img");
                                String firstname = obj.getString("first_name");

                                UserDataModel data = UserDataModel.getInstance();

                                data.setUsername(firstname);
                                data.setProfilepic(profileimg);

                                startActivity(new Intent(ProfileUpdateScreen.this, HomeScreen.class));
                                finish();


                                CommonMethod.showToast(ProfileUpdateScreen.this, "Profile Updated");

                            }else {

                                CommonMethod.showToast(ProfileUpdateScreen.this,"Not Updated");

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonMethod.hidepDialog();

                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                params.put("first_name", name.getText().toString());
                params.put("last_name", "");
                params.put("dob",year.getText().toString()+"-"+month.getText().toString()+"-"+date.getText().toString());
                params.put("about", aboutcontent.getText().toString());
                params.put("hobby", hobbycontent.getText().toString());
                params.put("looking_for", lookingforvalue.getText().toString());
                params.put("religion", religionvalue.getText().toString());
                params.put("occupation", occupationvalue.getText().toString());
                params.put("retired", retiredvalue.getText().toString());
                params.put("marital_status", martialstatusvalue.getText().toString());
                params.put("number_of_children", numberofchildrenvalue.getText().toString());
                params.put("living_at_home", livingathomevalue.getText().toString());

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                    params.put("image", new DataPart(imagename + ".jpeg", byteArray));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

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


                                }else {

                                    String yearv = dobv.substring(0,4);
                                    String monthv = dobv.substring(5,7);
                                    String datev = dobv.substring(8,10);
                                    date.setText(datev);
                                    month.setText(monthv);
                                    year.setText(yearv);

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

                                    Glide.with(getApplicationContext()).load(profilepic_s).into(profilepic);

                                    // Insert the profile image back the URL into the ImageView.
                                    Glide.with(ProfileUpdateScreen.this)
                                            .load(profilepic_s)
                                            .placeholder(R.drawable.single_image_dummy)
                                            .dontAnimate()
                                            .centerCrop().into(backimage);


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

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }


    public  boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileUpdateScreen.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    CommonMethod.showAlertDialog(ProfileUpdateScreen.this, "Permission Necessary", "To Get Matches", "Yes", "", new CommonMethod.DialogClickListener() {
                                @Override
                                public void dialogOkBtnClicked(String value) {

                                    ActivityCompat.requestPermissions(ProfileUpdateScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                                }
                                @Override
                                public void dialogNoBtnClicked(String value) {

                                }
                            }
                    );

                } else {

                    ActivityCompat.requestPermissions(ProfileUpdateScreen.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);

                }

                return false;

            } else {

                return true;

            }
        } else {

            return true;

        }
    }

}

