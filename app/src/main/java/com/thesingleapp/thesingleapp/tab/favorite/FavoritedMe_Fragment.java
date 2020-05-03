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

public class FavoritedMe_Fragment extends Fragment {

    private RecyclerView recyclerViewfavorited;

    SwipeRefreshLayout swipeRefreshLayout;

    //adapter object
    private ConnectionsAdapter adapterfavoritedme;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    ImageView logo;

    public int extracount = 0;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Connections_model> favoritedmeModelList;

    public String token,userid,useridvalue,first_name,city,profileimage,date,upremium,uOnline;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View favoritedme =inflater.inflate(R.layout.fragment_favoritedme,container,false);

        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = favoritedme.findViewById(R.id.simpleSwipeRefreshLayout);

        recyclerViewfavorited = favoritedme.findViewById(R.id.recyclerView_favoritedme);
        recyclerViewfavorited.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewfavorited.setLayoutManager(llm);

        logo = favoritedme.findViewById(R.id.applogo);

        favoritedmeModelList = new ArrayList<Connections_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        //Favourited me
       checkconnection();


        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (favoritedmeModelList.isEmpty()){

                }else {
                    favoritedmeModelList.clear();

                    extracount = 0;
                    //Favourited me
                    checkconnection();
                }
            }
        });

        recyclerViewfavorited.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraFavoritedme_Data();
                }
            }
        });
        return favoritedme;
    }

    public void getFavoritedme_Data(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.FAV+"?q=theyfav&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);

                            if(jsondata!=null && jsondata.length()>0) {


                            } else {

                                logo.setVisibility(View.VISIBLE);
                            }


                            for (int i = 0; i<=jsondata.length()-1; i++) {

                                JSONObject json = jsondata.getJSONObject(i);

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

                                favoritedmeModelList.add(new Connections_model(useridvalue,first_name, city,date, profileimage,upremium,uOnline));


                                //creating adapter
                                adapterfavoritedme = new ConnectionsAdapter(getActivity(), favoritedmeModelList);

                                //adding adapter to recyclerview
                                recyclerViewfavorited.setAdapter(adapterfavoritedme);
                                adapterfavoritedme.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterfavoritedme.showshimmer = false;

                                        adapterfavoritedme.notifyDataSetChanged();
                                    }
                                }, 1500);
                            }

                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterfavoritedme.showshimmer = false;

                                    adapterfavoritedme.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"No one still Favourite you,Impress Others,All the best");

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

        public void getExtraFavoritedme_Data(){


            // Creating string request with post method.
            stringRequest = new StringRequest(Request.Method.GET, Api.FAV+"?q=theyfav&count="+extracount,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String ServerResponse) {

                            try {

                                JSONArray jsondata = new JSONArray(ServerResponse);

                                if(jsondata!=null && jsondata.length()>0) {


                                    for (int i = 0; i<=jsondata.length()-1; i++) {

                                        JSONObject json = jsondata.getJSONObject(i);


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

                                        favoritedmeModelList.add(new Connections_model(useridvalue,first_name, city,date, profileimage,upremium,uOnline));



                                        //creating adapter
                                        adapterfavoritedme = new ConnectionsAdapter(getActivity(), favoritedmeModelList);

                                        //adding adapter to recyclerview
                                        recyclerViewfavorited.setAdapter(adapterfavoritedme);
                                        adapterfavoritedme.notifyDataSetChanged();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                adapterfavoritedme.showshimmer = false;

                                                adapterfavoritedme.notifyDataSetChanged();
                                            }
                                        }, 1500);
                                    }
                                } else {

                                }

                            } catch (JSONException e) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterfavoritedme.showshimmer = false;

                                        adapterfavoritedme.notifyDataSetChanged();
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
                                CommonMethod.showToast(getActivity(),"No one still Favourite you,Impress Others,All the best");

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

            getFavoritedme_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

}
