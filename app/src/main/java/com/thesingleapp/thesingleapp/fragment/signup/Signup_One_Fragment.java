package com.thesingleapp.thesingleapp.fragment.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.VerficationScreen;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Signup_One_Fragment extends Fragment implements View.OnClickListener {

    private ImageButton submit;
    private ImageView backbtn;
    public static EditText datepicker,month,year;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private String number;
    private StringRequest stringRequest;
    private int currentyear,minmum,maximum;
    private CheckBox male,female,seekmale,seekfemale;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View signupone =inflater.inflate(R.layout.fragment_signup_one,container,false);

        currentyear=Calendar.getInstance().get(Calendar.YEAR);
        minmum = currentyear - 12;
        maximum=currentyear-100;
        submit = signupone.findViewById(R.id.submit);
        backbtn = signupone.findViewById(R.id.backbutton);
        male = signupone.findViewById(R.id.ch_male);
        female = signupone.findViewById(R.id.ch_female);
        seekmale = signupone.findViewById(R.id.ch_seek_male);
        seekfemale = signupone.findViewById(R.id.ch_seek_female);
        datepicker = signupone.findViewById(R.id.month);
        month = signupone.findViewById(R.id.datepicker);
        year = signupone.findViewById(R.id.year);
        submit.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        seekmale.setOnClickListener(this);
        seekfemale.setOnClickListener(this);
        male.setOnClickListener(this);

        number= UserData.SIGNUPPHONENO;


        return signupone;
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

                }
                else if (month.getText().toString().equals("")) {

                    month.setError("Please Give your D.O.B");

                }
                else if (year.getText().toString().equals("")) {

                    year.setError("Please Give your D.O.B");

                }
                else if (Integer.parseInt(year.getText().toString())< maximum || Integer.parseInt(year.getText().toString())>minmum || year.getText().toString().length()>4){

                    year.setError("You are Below our Age Restriction");

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


            case R.id.backbutton:

                getActivity().finish();

                break;

        }
    }

    // user registering method
    public void Register(){

        //Show Progress Dailog
        CommonMethod.showpDialog(getActivity(),"Loading...");

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

                            if(status.equalsIgnoreCase("Phone No already exist")) {

                                CommonMethod.showToast(getActivity(), "Already Phone Number Used");

                            }else if(status.equals("Something went wrong")){

                                CommonMethod.showToast(getActivity(), "Something Went Wrong! Please Try Later");

                            }else if (status.equals("Email already exist")) {

                                CommonMethod.showToast(getActivity(), "Your Email Id is Already Used Please Login..!");

                            }else {

                                JSONObject statusobject = jsondata.getJSONObject("status");

                                String id = statusobject.getString("id");
                                String profileid = statusobject.getString("Profile_id");
                                String token = statusobject.getString("token");

                                UserDataModel data = UserDataModel.getInstance();
                                data.setId(id);
                                data.setToken(token);
                                data.setUsername(UserData.SIGNUPUSERNAME);
                                data.setProfilepic("");
                                data.setProfileid(profileid);
                                data.setPremium("false");
                                UserData.logintype ="normal";

                                Intent intent = new Intent(getActivity(), VerficationScreen.class);
                                intent.putExtra("number",number );
                                startActivity(intent);
                                getActivity().finish();


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
                        CommonMethod.showToast(getActivity(), "Please Check your Internet");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {


                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();


                // Adding All values to Params.
                params.put("first_name", UserData.SIGNUPUSERNAME);
                params.put("last_name", "");
//                params.put("username", UserData.SIGNUPPHONENO);
                params.put("phone", UserData.SIGNUPPHONENO);
                params.put("email", UserData.SIGNUPEMAIL);
                params.put("password", UserData.SIGNUPPASSWORD);
                params.put("login_by","N");
                params.put("gender",UserData.SIGNUPMALEFEMALE);
                params.put("seeking_for",UserData.SIGNUPSEEKMALEFEMALE);
                params.put("dob",year.getText().toString()+"-"+month.getText().toString()+"-"+datepicker.getText().toString());
                params.put("is_active","false");
                params.put("domain","M");

                return params;
            }


        };


        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

}
