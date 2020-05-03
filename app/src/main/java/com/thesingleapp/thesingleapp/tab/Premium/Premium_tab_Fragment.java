package com.thesingleapp.thesingleapp.tab.Premium;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.thesingleapp.thesingleapp.adapter.PremiumAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.fragment.PremiumDetailDialogFragment;
import com.thesingleapp.thesingleapp.model.Premium_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Premium_tab_Fragment extends Fragment implements View.OnClickListener, PremiumDetailDialogFragment.DialogListener{


    private RecyclerView recyclerView_premium;

    SwipeRefreshLayout swipeRefreshLayout;

    //adapter object
    private PremiumAdapter adapterpremium;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    ImageView logo;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Premium_model> premiumModelList;


    private LinearLayout premiumdetails;

    public String token,userid,useridvalue,first_name,city,profileimage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View premium =inflater.inflate(R.layout.fragment_premium_tab,container,false);

        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = premium.findViewById(R.id.simpleSwipeRefreshLayout);
        logo = premium.findViewById(R.id.applogo);
        recyclerView_premium = premium.findViewById(R.id.recyclerView_premium);
        premiumdetails = premium.findViewById(R.id.premiumdetails);
        premiumdetails.setOnClickListener(this);
        recyclerView_premium.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_premium.setLayoutManager(llm);

        premiumModelList = new ArrayList<Premium_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());


        //Viewed me

       checkconnection();

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (premiumModelList.isEmpty()){

                }else {
                    premiumModelList.clear();
                    //Viewed me
                    checkconnection();
                }
            }
        });


        return premium;
    }

    public void getViewedMe_Data() {

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PREMIUM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);


                            if(jsondata!=null && jsondata.length()>0) {


                            } else {

                                logo.setVisibility(View.VISIBLE);

                            }

                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);


                                String id = json.getString("id");
                                String noofdays = json.getString("no_of_days");
                                String amount = json.getString("amount");
                                String month = json.getString("month");
                                String year = json.getString("year");


                                premiumModelList.add(new Premium_model(id,noofdays,amount,month,year));

                                //creating adapter
                                adapterpremium = new PremiumAdapter(getActivity(), premiumModelList);

                                //adding adapter to recyclerview
                                recyclerView_premium.setAdapter(adapterpremium);
                                adapterpremium.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterpremium.showshimmer = false;

                                        adapterpremium.notifyDataSetChanged();
                                    }
                                }, 1500);
                            }

                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterpremium.showshimmer = false;

                                    adapterpremium.notifyDataSetChanged();
                                }
                            }, 1500);
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

                            CommonMethod.showToast(getActivity(),"Session Expired");

                        }else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            // Showing error message if something goes wrong.

                            CommonMethod.showToast(getActivity(), "No Premium Price Announced");

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

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.premiumdetails:

                PremiumDetailDialogFragment dialogFragment = new PremiumDetailDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("notAlertDialog", true);

                dialogFragment.setArguments(bundle);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                dialogFragment.show(ft, "dialog");


                break;

        }
    }

    // filterdailoge values return
    @Override
    public void onFinishEditDialog(String inputText) {

        if (TextUtils.isEmpty(inputText)) {
//            textView.setText("Email was not entered");
        } else{
//            textView.setText("Email entered: " + inputText);
        }
    }

    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getViewedMe_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }


}