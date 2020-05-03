package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Settings_Fragment extends Fragment {

    public String premium = "false";
    public String profile = "false";
    public String user_tracking = "false";
    public String message = "false";
    public String onlineoffline = "false";
    public String premiummsg = "false";
    public String comments = "false";
    private StringRequest stringRequest;
    private String token,userid,settingsid;

    public Switch sw_viewprofile,sw_messagefromunknow,sw_onlineoroffline,sw_premiummeber,sw_comment,sw_trackyouvalue;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View settings =inflater.inflate(R.layout.fragment_settings,container,false);

        sw_viewprofile = settings.findViewById(R.id.sw_viewprofile);
        sw_messagefromunknow = settings.findViewById(R.id.sw_messagefromunknow);
        sw_onlineoroffline = settings.findViewById(R.id.sw_onlineoroffline);
        sw_premiummeber = settings.findViewById(R.id.sw_premiummeber);
        sw_comment = settings.findViewById(R.id.sw_comment);
        sw_trackyouvalue = settings.findViewById(R.id.sw_trackyouvalue);

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();


        sw_premiummeber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    premium = "true";

                    checkconnection1();

                } else {

                    premium = "false";

                    checkconnection1();

                }
            }
        });

        sw_viewprofile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    profile = "true";

                    checkconnection1();

                } else {

                    profile = "false";

                    checkconnection1();
                }
            }
        });

        sw_messagefromunknow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    message = "true";

                    checkconnection1();

                } else {

                    message = "false";

                    checkconnection1();

                }
            }
        });

        sw_onlineoroffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    onlineoffline = "true";

                    checkconnection1();


                } else {

                    onlineoffline = "false";

                    checkconnection1();

                }
            }
        });


        sw_comment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    comments = "true";

                    checkconnection1();

                } else {

                    comments = "false";

                    checkconnection1();

                }
            }
        });


        sw_trackyouvalue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    user_tracking = "true";

                    checkconnection1();

                } else {

                    user_tracking = "false";

                    checkconnection1();

                }
            }
        });

        //get settings data
        checkconnection();

        return settings;
    }

    public void Settings(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.PUT, Api.SETTINGS+settingsid+"/",

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);

                                settingsid = jsondata.getString("id");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        if (volleyError instanceof AuthFailureError) {

                            startActivity(new Intent(getActivity(), LoginScreen.class));
                            getActivity().finish();

                            // Showing error message if something goes wrong.

                            CommonMethod.showToast(getActivity(),"Session Espired");

                        }else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                            // Showing error message if something goes wrong.
                            CommonMethod.showToast(getActivity(), "Cant Able to change your Settings");

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

                params.put("friendly_only_can_view_profile", profile);
                params.put("only_friend_can_comment", comments);
                params.put("premium_members_only_view", premium);
                params.put("hide_online_status", onlineoffline);
                params.put("premium_members_only_msg", premiummsg);
                params.put("friendly_only_can_msg", message);
                params.put("user_tracking", user_tracking);

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


    public void getSettings_Data() {

        //Show Progress Dailog
        CommonMethod.showpDialog(getActivity(),"Loading...");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.SETTINGS+"?q="+userid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);


                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);


                                settingsid = json.getString("id");
                                String viewprofile = json.getString("friendly_only_can_view_profile");
                                String msgfromunknow = json.getString("friendly_only_can_msg");
                                String commentfromunknow = json.getString("only_friend_can_comment");
                                String onlineoroffline = json.getString("hide_online_status");
                                String premium_membersonlymsg = json.getString("premium_members_only_msg");
                                String permiummeberonlyview = json.getString("premium_members_only_view");
                                String user_trackingvalue = json.getString("user_tracking");



                                if (viewprofile.equals("false")){

                                    sw_viewprofile.setChecked(false);

                                    profile = "false";

                                }else {

                                    sw_viewprofile.setChecked(true);

                                    profile = "true";

                                }

                                if (msgfromunknow.equals("false")){

                                    sw_messagefromunknow.setChecked(false);

                                    message = "false";

                                }else {

                                    sw_messagefromunknow.setChecked(true);

                                    message = "true";

                                }

                                if (commentfromunknow.equals("false")) {

                                    sw_comment.setChecked(false);

                                    comments = "false";

                                }else {

                                    sw_comment.setChecked(true);

                                    comments = "true";

                                }

                                if (onlineoroffline.equals("false")){

                                    sw_onlineoroffline.setChecked(false);

                                    onlineoffline = "false";

                                }else {

                                    sw_onlineoroffline.setChecked(true);

                                    onlineoffline = "true";

                                }



                                if (permiummeberonlyview.equals("false")){

                                    sw_premiummeber.setChecked(false);

                                    premium = "false";

                                }else {

                                    sw_premiummeber.setChecked(true);

                                    premium = "true";

                                }

                                if (user_trackingvalue.equals("false")){

                                    sw_trackyouvalue.setChecked(false);

                                    user_tracking = "false";

                                }else {

                                    sw_trackyouvalue.setChecked(true);

                                    user_tracking = "true";

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

                            // Showing error message if something goes wrong.
                            CommonMethod.showToast(getActivity(), "");

                        }

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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getSettings_Data();
        } else {

            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }
    public void checkconnection1(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            Settings();
        } else {


            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }
}
