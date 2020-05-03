package com.thesingleapp.thesingleapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ChangePasswordScreen extends AppCompatActivity implements View.OnClickListener {

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;
    private EditText password,oldpassword,conformpassword;
    String token,userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_screen);

        password = findViewById(R.id.password);
        oldpassword = findViewById(R.id.oldpassword);
        conformpassword = findViewById(R.id.conformpassword);
        ImageButton verify = findViewById(R.id.submitbutton);
        ImageView backbutton = findViewById(R.id.backbutton);

        verify.setOnClickListener(this);
        backbutton.setOnClickListener(this);

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.submitbutton:

                if (password.getText().toString().equals("")){

                    password.setError("Please Enter The Password");

                }else if(oldpassword.getText().toString().equals("")){

                    oldpassword.setError("Please Enter The Old Password");

                }else if(conformpassword.getText().toString().equals("")){

                    conformpassword.setError("Please Enter The Conform Password");

                }else if(password.getText().length() < 8){
                    password.setError("Password Must be Minimum 8 Digits");
                }
                else if(conformpassword.getText().length() <8){
                    conformpassword.setError("Conform Password Must be Minimum 8 Digits");
                }
                else if (password.getText().toString().equals(conformpassword.getText().toString())){
                    ChangePassword();
                }
                else {

                    password.setError("Password Mismatch");
                    conformpassword.setError("Password Mismatch");

                }
                break;

            case R.id.backbutton:

                finish();

                break;

        }
    }


    public void ChangePassword(){

        //Show Progress Dailog
        CommonMethod.showpDialog(this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.PUT, Api.CHANGEPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        try {
                            JSONObject jsondata = new JSONObject(ServerResponse);


                            String status = jsondata.getString("status");

                            if (status.equals("old password does not match")){

                                CommonMethod.showToast(ChangePasswordScreen.this,"Please Enter Your Old Password Correctly");

                            }else if(status.equals("success")){

                                startActivity(new Intent(ChangePasswordScreen.this,ProfileScreen.class));
                                finish();

                            }else {

                                CommonMethod.showToast(ChangePasswordScreen.this,"Can't Change the Password");

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
                        CommonMethod.showToast(ChangePasswordScreen.this,"Please Check your Internet");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("old_password", oldpassword.getText().toString());
                params.put("new_password", password.getText().toString());
                params.put("repeat_password", password.getText().toString());

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
        RequestQueue requestQueue = Volley.newRequestQueue(ChangePasswordScreen.this);

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

}
