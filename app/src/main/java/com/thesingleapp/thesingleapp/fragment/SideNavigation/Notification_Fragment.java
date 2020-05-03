package com.thesingleapp.thesingleapp.fragment.SideNavigation;


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
import com.thesingleapp.thesingleapp.adapter.NotificationAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Notification_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notification_Fragment extends Fragment {

    private RecyclerView recyclerViewfavorited;

    SwipeRefreshLayout swipeRefreshLayout;

    //adapter object
    private NotificationAdapter adapternotification;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    ImageView logo;

    public int extracount = 0;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Notification_model> notificationModelList;

    public String token,userid,useridvalue,first_name,msg,profileimage,upremium,uonline,date;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View notification =inflater.inflate(R.layout.fragment_notification,container,false);
        logo = notification.findViewById(R.id.applogo);
        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = notification.findViewById(R.id.simpleSwipeRefreshLayout);

        recyclerViewfavorited = notification.findViewById(R.id.recyclerView_notification);
        recyclerViewfavorited.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewfavorited.setLayoutManager(llm);


        notificationModelList = new ArrayList<Notification_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());


        //Notification
        checkconnection();



        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (notificationModelList.isEmpty()){

                }else {
                    notificationModelList.clear();
                    extracount = 0;

                    //Notification
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

                    getExtraNotification_Data();
                }
            }
        });



        return notification;
    }


    public void getNotification_Data(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.NOTIFICATION+"?count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);

                            if(jsondata!=null && jsondata.length()>0) {

                            } else {

                                logo.setVisibility(View.VISIBLE);

                            }

                            for (int i =0; i<=jsondata.length()-1; i++) {

                                JSONObject json = jsondata.getJSONObject(i);

                                profileimage = json.getString("u_image");
                                useridvalue = json.getString("user");
                                msg = json.getString("msg");
                                upremium = json.getString("u_is_premium");
                                uonline = json.getString("u_online");
                                date = json.getString("str_date");


                                notificationModelList.add(new Notification_model(useridvalue,msg, profileimage,upremium,uonline,date));


                                //creating adapter
                                adapternotification = new NotificationAdapter(getActivity(), notificationModelList);

                                //adding adapter to recyclerview
                                recyclerViewfavorited.setAdapter(adapternotification);
                                adapternotification.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapternotification.showshimmer = false;

                                        adapternotification.notifyDataSetChanged();
                                    }
                                }, 1500);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapternotification.showshimmer = false;

                                    adapternotification.notifyDataSetChanged();
                                }
                            }, 1500);

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
                            CommonMethod.showToast(getActivity(), "You Dont Have Any Notifications");

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

            getNotification_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    public void getExtraNotification_Data(){



        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.NOTIFICATION+"?count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);

                            if(jsondata!=null && jsondata.length()>0) {



                                for(int i =0; i<=jsondata.length()-1; i++) {

                                    JSONObject json = jsondata.getJSONObject(i);

                                    profileimage = json.getString("u_image");
                                    useridvalue = json.getString("user");
                                    msg = json.getString("msg");
                                    upremium = json.getString("u_is_premium");
                                    uonline = json.getString("u_online");
                                    date = json.getString("str_date");


                                    notificationModelList.add(new Notification_model(useridvalue,msg, profileimage,upremium,uonline,date));


                                    //creating adapter
                                    adapternotification = new NotificationAdapter(getActivity(), notificationModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewfavorited.setAdapter(adapternotification);

                                    adapternotification.notifyDataSetChanged();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapternotification.showshimmer = false;

                                            adapternotification.notifyDataSetChanged();
                                        }
                                    }, 1500);

                                }
                            } else {


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.
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
                            CommonMethod.showToast(getActivity(), "You Dont Have Any More Notifications");

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