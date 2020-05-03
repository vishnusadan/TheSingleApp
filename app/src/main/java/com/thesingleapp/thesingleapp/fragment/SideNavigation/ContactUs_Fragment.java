package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.LoginScreen;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import java.util.HashMap;
import java.util.Map;


public class ContactUs_Fragment extends Fragment implements View.OnClickListener {


    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;

    public Button submit;
    public EditText message;
    String token,userid;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contactus =inflater.inflate(R.layout.fragment_contactus,container,false);

        message = contactus.findViewById(R.id.input_message);
        submit = contactus.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        //user data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        return contactus;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.submit:

                if(message.getText().toString().equals("")){

                    message.setError("Please Enter the Message");

                }else if (message.getText().length()<6){

                    message.setError("Please Enter More Content");

                }else {

                    message.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    checkconnection();

                }

                break;

        }

    }

    public void Senddata(){

        //Show Progress Dailog
        CommonMethod.showpDialog(getActivity(),"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.CONTACTUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        message.setText("");


                        CommonMethod.showToast(getActivity(), "Thanks For Your Feedback");


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        if (volleyError instanceof AuthFailureError) {

                            startActivity(new Intent(getActivity(), LoginScreen.class));
                            getActivity().finish();

                            // Showing error message if something goes wrong.
                            CommonMethod.showToast(getActivity(), "Session Expired");

                        }else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        }


                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("feed", message.getText().toString());
                params.put("user", userid);


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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }


    @Override
    public void onDestroy() {

        // to close keyboard if opened
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        super.onDestroy();
    }

    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            Senddata();
        } else {


            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }
}
