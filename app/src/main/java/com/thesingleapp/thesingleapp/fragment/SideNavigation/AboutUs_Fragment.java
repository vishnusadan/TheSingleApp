package com.thesingleapp.thesingleapp.fragment.SideNavigation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

@SuppressWarnings("SpellCheckingInspection")
public class AboutUs_Fragment extends Fragment {

    TextView content;

    StringRequest stringRequest;

    public String token;

    ImageView logo;

    ShimmerFrameLayout sh1;

    ScrollView scrollview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View aboutus =inflater.inflate(R.layout.fragment_aboutus,container,false);



        return aboutus;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        content = view.findViewById(R.id.textView16);
        logo = view.findViewById(R.id.applogo);
        sh1= view.findViewById(R.id.parentShimmerLayout);
        scrollview = view.findViewById(R.id.scrollview);
        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();

        checkconnection();

    }


    public void getAboutus(){

        //start shimmer effect
        sh1.startShimmer();

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.ABOUTUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide shimmer
                        sh1.stopShimmer();
                        sh1.setShimmer(null);
                        scrollview.setBackground(null);


                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);

                            JSONObject data =jsondata.getJSONObject(0);

                            String aboutus_cont = data.getString("about_single");

                            if(aboutus_cont!=null && aboutus_cont.length()>0) {

                            } else {

                                logo.setVisibility(View.VISIBLE);

                            }

                            content.setText(aboutus_cont);


                        } catch (JSONException e) {
                            e.printStackTrace();
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

            getAboutus();
        } else {
            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            //Hide shimmer
            sh1.stopShimmer();
            sh1.setShimmer(null);
            scrollview.setBackground(null);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }
}

