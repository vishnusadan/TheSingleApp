package com.thesingleapp.thesingleapp.tab.photoLike;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.thesingleapp.thesingleapp.activity.LoginScreen;
import com.thesingleapp.thesingleapp.adapter.ConnectionsAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Connections_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LikeGivenPhoto_Fragment extends Fragment {

    private RecyclerView recyclerViewlikegiven;

    SwipeRefreshLayout swipeRefreshLayout;

    //adapter object
    private ConnectionsAdapter adapterlikegiven;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    ImageView logo;

    public int extracount = 0;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Connections_model> likegivenModelList;

    public String token,userid,useridvalue,first_name,city,date,profileimage,upremium,uOnline;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View likegiven =inflater.inflate(R.layout.fragment_likegiven,container,false);

        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = likegiven.findViewById(R.id.simpleSwipeRefreshLayout);
        logo = likegiven.findViewById(R.id.applogo);
        recyclerViewlikegiven = likegiven.findViewById(R.id.recyclerView_likegiven);
        recyclerViewlikegiven.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewlikegiven.setLayoutManager(llm);

        likegivenModelList = new ArrayList<Connections_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        //likegiven me

       checkconnection();

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (likegivenModelList.isEmpty()){

                }else {
                    likegivenModelList.clear();

                    extracount = 0;
                    //likegiven me
                    checkconnection();
                }
            }
        });

        recyclerViewlikegiven.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraLikeGiven_Data();
                }
            }
        });


        return likegiven;
    }

    public void getLikeGiven_Data(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.IMAGELIKE+"?q=theylike&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsonArray = new JSONArray(ServerResponse);

                            if(jsonArray!=null && jsonArray.length()>0) {


                            } else {

                                logo.setVisibility(View.VISIBLE);

                            }

                            for (int i = 0; i<=jsonArray.length()-1; i++) {

                                JSONObject json = jsonArray.getJSONObject(i);


                                useridvalue = json.getString("user");
                                first_name = json.getString("u_first_name");
                                city = json.getString("u_city");
                                profileimage = json.getString("u_image");
                                date = json.getString("str_date");
                                upremium= json.getString("u_is_premium");
                                uOnline =json.getString("u_online");

                                if (city.equals("null")){

                                    city="";

                                }


                                likegivenModelList.add(new Connections_model(useridvalue,first_name, city,date,profileimage,upremium,uOnline));


                                //creating adapter
                                adapterlikegiven = new ConnectionsAdapter(getActivity(), likegivenModelList);

                                //adding adapter to recyclerview
                                recyclerViewlikegiven.setAdapter(adapterlikegiven);
                                adapterlikegiven.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterlikegiven.showshimmer = false;

                                        adapterlikegiven.notifyDataSetChanged();
                                    }
                                }, 1500);

                            }

                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterlikegiven.showshimmer = false;

                                    adapterlikegiven.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"No One Liked Your Photo");

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
    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getLikeGiven_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    public  void getExtraLikeGiven_Data(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.IMAGELIKE+"?q=theylike&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsonArray = new JSONArray(ServerResponse);

                            if(jsonArray!=null && jsonArray.length()>0) {

                                for (int i = 0; i<=jsonArray.length()-1; i++) {

                                    JSONObject json = jsonArray.getJSONObject(i);


                                    useridvalue = json.getString("user");
                                    first_name = json.getString("u_first_name");
                                    city = json.getString("u_city");
                                    profileimage = json.getString("u_image");
                                    date = json.getString("str_date");
                                    upremium= json.getString("u_is_premium");
                                    uOnline =json.getString("u_online");

                                    if (city.equals("null")){

                                        city="";

                                    }

                                    likegivenModelList.add(new Connections_model(useridvalue,first_name, city,date,profileimage,upremium,uOnline));

                                    //creating adapter
                                    adapterlikegiven = new ConnectionsAdapter(getActivity(), likegivenModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewlikegiven.setAdapter(adapterlikegiven);
                                    adapterlikegiven.notifyDataSetChanged();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapterlikegiven.showshimmer = false;

                                            adapterlikegiven.notifyDataSetChanged();
                                        }
                                    }, 1500);
                                }
                            } else {

                            }



                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterlikegiven.showshimmer = false;

                                    adapterlikegiven.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"No One Liked Your Photo");

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
}