package com.thesingleapp.thesingleapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordScreen extends AppCompatActivity implements View.OnClickListener {

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;

    private EditText emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_screen);

        emailid = findViewById(R.id.emailid);
        ImageButton submit = findViewById(R.id.submitbutton);
        ImageView backbtn = findViewById(R.id.backbutton123);

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(ForgetPasswordScreen.this);

        submit.setOnClickListener(this);

        backbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

               ForgetPasswordScreen.this.finish();

            }});
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.submitbutton:

                if (emailid.getText().toString().equals("") || !emailid.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){

                    emailid.setError("Please Give your Correct Email Id");

                }else {

                    Forgotpass();

                }

                break;

        }
    }

    public void Forgotpass(){
        //Show Progress Dailog
        CommonMethod.showpDialog(this,"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.FORGETPASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        try {
                            JSONObject jsondata = new JSONObject(ServerResponse);


                            String status = jsondata.getString("detail");



                            if(status.equalsIgnoreCase("Password reset e-mail has been sent.")) {

                                CommonMethod.showToast(ForgetPasswordScreen.this, "Please Check You Mail To change your Password");

                                emailid.setText("");

                            }else {

                                CommonMethod.showToast(ForgetPasswordScreen.this,"Please Contact Our Customer Care");

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
                        CommonMethod.showToast(ForgetPasswordScreen.this, "Please Check your Internet");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                params.put("email", emailid.getText().toString());

                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(ForgetPasswordScreen.this);

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

}
