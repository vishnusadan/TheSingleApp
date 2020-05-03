package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
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

public class Privacyandpolicy  extends Fragment {

    TextView content;

    StringRequest stringRequest;

    public String token;

    ImageView logo;

    ScrollView scrollview;

    ShimmerFrameLayout sh1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View privacyandpolicy = inflater.inflate(R.layout.fragment_privacy, container, false);


        return privacyandpolicy;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles

        content = view.findViewById(R.id.textView16);
        logo = view.findViewById(R.id.applogo);
        sh1= view.findViewById(R.id.parentShimmerLayout);
        scrollview = view.findViewById(R.id.scrollview);
        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();

        checkconnection();

    }


    public void getPrivacypolicy(){

        //start shimmer effect
        sh1.startShimmer();

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PRIVACYPOLICY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide shimmer
                        sh1.stopShimmer();
                        sh1.setShimmer(null);
                        scrollview.setBackground(null);


                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);
                            JSONObject data =jsondata.getJSONObject(1);
                            String aboutus_cont = data.getString("terms");

                            if(aboutus_cont!=null && aboutus_cont.length()>0) {

                            } else {

                                logo.setVisibility(View.VISIBLE);
                                CommonMethod.showToast(getActivity(),"No Privacy and Policy Announced");

                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                content.setText((Html.fromHtml(aboutus_cont, Html.FROM_HTML_MODE_COMPACT)));
                            }else{
                                content.setText(Html.fromHtml(aboutus_cont));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Hide shimmer
                            sh1.stopShimmer();
                            sh1.setShimmer(null);
                            scrollview.setBackground(null);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }
    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getPrivacypolicy();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            sh1.stopShimmer();
            sh1.setShimmer(null);
            scrollview.setBackground(null);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

}

