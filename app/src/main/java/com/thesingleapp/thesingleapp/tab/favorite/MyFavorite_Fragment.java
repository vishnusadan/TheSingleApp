package com.thesingleapp.thesingleapp.tab.favorite;


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

public class MyFavorite_Fragment extends Fragment {

    private RecyclerView recyclerViewmyfavorite;

    SwipeRefreshLayout swipeRefreshLayout;

    //adapter object
    private ConnectionsAdapter adaptermyfavorite;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    public int extracount = 0;

    private StringRequest stringRequest;

    ImageView logo;

    //list to hold all the uploaded images
    private List<Connections_model> myfavoriteModelList;

    public String token,userid,useridvalue,first_name,date,city,profileimage,opremium,oonline;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View myfavorite =inflater.inflate(R.layout.fragment_myfavorite,container,false);

        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = myfavorite.findViewById(R.id.simpleSwipeRefreshLayout);

        recyclerViewmyfavorite = myfavorite.findViewById(R.id.recyclerView_myfavorite);
        recyclerViewmyfavorite.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewmyfavorite.setLayoutManager(llm);
        logo = myfavorite.findViewById(R.id.applogo);

        myfavoriteModelList = new ArrayList<Connections_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        //MY Faavourite

        checkconnection();

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (myfavoriteModelList.isEmpty()){

                }else {
                    myfavoriteModelList.clear();

                    extracount = 0;
                    //MY Faavourite
                    checkconnection();
                }
            }
        });
        recyclerViewmyfavorite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraMyFavorite_Data();
                }
            }
        });
        return myfavorite;
    }

    public void getMyFavorite_Data(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.FAV+"?q=ifav&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {


                            JSONArray jsonArray = new JSONArray(ServerResponse);


                            if(jsonArray!=null && jsonArray.length()>0) {


                            } else {

                                logo.setVisibility(View.VISIBLE);

                            }

                            for (int i =0; i<=jsonArray.length()-1; i++) {

                                JSONObject json = jsonArray.getJSONObject(i);


                                useridvalue = json.getString("other");
                                first_name = json.getString("o_first_name");
                                city = json.getString("o_city");
                                profileimage = json.getString("o_image");
                                date = json.getString("str_date");
                                opremium = json.getString("o_is_premium");
                                oonline = json.getString("o_online");


                                if (city.equals("null")){

                                    city="";

                                }

                                myfavoriteModelList.add(new Connections_model(useridvalue,first_name, city,date, profileimage,opremium,oonline));


                                //creating adapter
                                adaptermyfavorite = new ConnectionsAdapter(getActivity(), myfavoriteModelList);

                                //adding adapter to recyclerview
                                recyclerViewmyfavorite.setAdapter(adaptermyfavorite);
                                adaptermyfavorite.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adaptermyfavorite.showshimmer = false;

                                        adaptermyfavorite.notifyDataSetChanged();
                                    }
                                }, 1500);

                            }

                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adaptermyfavorite.showshimmer = false;

                                    adaptermyfavorite.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"You Dont Have a Favourite Profile");

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

            getMyFavorite_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    public void getExtraMyFavorite_Data(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.FAV+"?q=ifav&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {


                            JSONArray jsonArray = new JSONArray(ServerResponse);

                            if(jsonArray!=null && jsonArray.length()>0) {


                                for (int i = 0; i<=jsonArray.length()-1; i++) {

                                    JSONObject json = jsonArray.getJSONObject(i);


                                    useridvalue = json.getString("other");
                                    first_name = json.getString("o_first_name");
                                    city = json.getString("o_city");
                                    profileimage = json.getString("o_image");
                                    date = json.getString("str_date");
                                    opremium = json.getString("o_is_premium");
                                    oonline = json.getString("o_online");


                                    if (city.equals("null")){

                                        city="";

                                    }

                                    myfavoriteModelList.add(new Connections_model(useridvalue,first_name, city,date, profileimage,opremium,oonline));



                                    //creating adapter
                                    adaptermyfavorite = new ConnectionsAdapter(getActivity(), myfavoriteModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewmyfavorite.setAdapter(adaptermyfavorite);
                                    adaptermyfavorite.notifyDataSetChanged();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            adaptermyfavorite.showshimmer = false;

                                            adaptermyfavorite.notifyDataSetChanged();
                                        }
                                    }, 1500);
                                }
                            } else {

                            }

                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adaptermyfavorite.showshimmer = false;

                                    adaptermyfavorite.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"You Dont Have a Favourite Profile");

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
