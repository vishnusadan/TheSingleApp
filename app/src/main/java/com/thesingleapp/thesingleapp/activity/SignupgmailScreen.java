package com.thesingleapp.thesingleapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import java.util.HashMap;
import java.util.Map;

public class SignupgmailScreen extends AppCompatActivity implements View.OnClickListener {

    private ImageButton submit;
    private ImageView backbtn;
    public static EditText datepicker,month,year;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private String number,token,userid,profileid;
    private StringRequest stringRequest;

    private CheckBox male,female,seekmale,seekfemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupgmail_screen);

        submit = findViewById(R.id.submit);
        backbtn = findViewById(R.id.backbutton);
        male = findViewById(R.id.ch_male);
        female = findViewById(R.id.ch_female);
        seekmale = findViewById(R.id.ch_seek_male);
        seekfemale = findViewById(R.id.ch_seek_female);
        datepicker = findViewById(R.id.month);
        month = findViewById(R.id.datepicker);
        year = findViewById(R.id.year);
        submit.setOnClickListener(this);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        seekmale.setOnClickListener(this);
        seekfemale.setOnClickListener(this);
        male.setOnClickListener(this);

        number= UserData.SIGNUPPHONENO;

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();
        profileid = data.getProfileid();

    }
    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.submit:

                if (UserData.SIGNUPMALEFEMALE.equals("") ){

                    male.setError("Please Give Gender");

                }
                else if (UserData.SIGNUPSEEKMALEFEMALE.equals("")){

                    seekmale.setError("Please Give Seeking Gender");

                }else if (datepicker.getText().toString().equals("")) {

                    datepicker.setError("Please Give your D.O.B");

                }else if (Integer.parseInt(year.getText().toString())<1930 || Integer.parseInt(year.getText().toString())>2010 || year.getText().toString().length()>4){

                    year.setError("Please Enter valid Year");

                }else if (Integer.parseInt(month.getText().toString())<1 || Integer.parseInt(month.getText().toString())>12 || month.getText().toString().length()>2){

                    month.setError("Please Enter valid Month");

                }else if (Integer.parseInt(datepicker.getText().toString())<1 || Integer.parseInt(datepicker.getText().toString())>31 || datepicker.getText().toString().length()>2){

                    datepicker.setError("Please Enter valid Date");

                }else {

                    Register();

                }

                break;



            case R.id.ch_male:


                if (male.isChecked()){

                    UserData.SIGNUPMALEFEMALE="M";
                    female.setChecked(false);

                }

                else {

                    UserData.SIGNUPMALEFEMALE="";

                }

                break;

            case R.id.ch_female:


                if (female.isChecked()){

                    UserData.SIGNUPMALEFEMALE="F";
                    male.setChecked(false);

                }

                else {

                    UserData.SIGNUPMALEFEMALE="";

                }
                break;

            case R.id.ch_seek_male:


                if (seekmale.isChecked()){

                    UserData.SIGNUPSEEKMALEFEMALE="M";
                    seekfemale.setChecked(false);

                }

                else {

                    UserData.SIGNUPSEEKMALEFEMALE="";

                }
                break;

            case R.id.ch_seek_female:


                if (seekfemale.isChecked()){

                    UserData.SIGNUPSEEKMALEFEMALE="F";
                    seekmale.setChecked(false);

                }

                else {

                    UserData.SIGNUPSEEKMALEFEMALE="";
                }
                break;



        }
    }


    // user registering method
    public void Register(){

        //Show Progress Dailog
        CommonMethod.showpDialog(SignupgmailScreen.this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.PUT, Api.PROFILEADD+profileid+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                                Intent intent = new Intent(SignupgmailScreen.this, HomeScreen.class);
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


                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {


                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("gender",UserData.SIGNUPMALEFEMALE);
                params.put("seeking_for",UserData.SIGNUPSEEKMALEFEMALE);
                params.put("dob",year.getText().toString()+"-"+month.getText().toString()+"-"+datepicker.getText().toString());


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
        RequestQueue requestQueue = Volley.newRequestQueue(SignupgmailScreen.this);

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

}

