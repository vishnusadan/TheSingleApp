package com.thesingleapp.thesingleapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Map_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Testing extends AppCompatActivity {

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    // Creating Progress dialog.
    ProgressDialog progressDialog;

    private StringRequest stringRequest;


    public Map_model mmapData;
    String token, userid, profilepic, latitude, longitude, first_name, dob,otheruserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(Testing.this);

        //progress daialog
        progressDialog = new ProgressDialog(Testing.this);
        
        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();


        progressDialog = new ProgressDialog(Testing.this);
//        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching your Location...");
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();


        getlatlng();
    }

    public void getlatlng(){

        //Show Progress Dailog
        CommonMethod.showpDialog(Testing.this,"Getting your Matches Profile");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PROFILEVIEW,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Show Progress Dailog
                        CommonMethod.hidepDialog();


                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);

                            JSONArray array = jsondata.getJSONArray("data");


                            for (int i = array.length()-1; i>=0; i--) {

                                JSONObject json = array.getJSONObject(i);

                                otheruserid = json.getString("user");
                                first_name = json.getString("first_name");
                                dob = json.getString("age");
                                latitude = json.getString("latitude");
                                longitude = json.getString("longitude");

                                String profile = json.getString("profile_img");



                                if(profile.equals("null")){

                                    profilepic = "";

                                }else {

                                    JSONObject prof = json.getJSONObject("profile_img");

                                    profilepic = prof.getString("img");


                                }

                            }
                            mmapData = new Map_model();
                            mmapData.setUserid(otheruserid);
                            mmapData.setName(first_name);
                            mmapData.setProfilepic(profilepic);
                            mmapData.setAge(dob);

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

                        // Showing error message if something goes wrong.

                        CommonMethod.showToast(Testing.this,"Something Went Wrong");

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
                params.put("Authorization", "jwt "+token);
                return params;
            }
        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(Testing.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }


}
