package com.thesingleapp.thesingleapp.tab.Premium;


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
import com.thesingleapp.thesingleapp.adapter.OrdersAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Orders_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Orders_tab_Fragment extends Fragment {


    private RecyclerView recyclerView_orders;

    SwipeRefreshLayout swipeRefreshLayout;

    //adapter object
    private OrdersAdapter adapterorders;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

      ImageView logo;


    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Orders_model> ordersModelList;

    public String token,userid,useridvalue,first_name,city,profileimage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View orders =inflater.inflate(R.layout.fragment_orders_tab,container,false);

        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = orders.findViewById(R.id.simpleSwipeRefreshLayout);
        logo = orders.findViewById(R.id.applogo);
        recyclerView_orders = orders.findViewById(R.id.recyclerView_orders);
        recyclerView_orders.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_orders.setLayoutManager(llm);


        ordersModelList = new ArrayList<Orders_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());


        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        //get ordered data

       checkconnection();

        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (ordersModelList.isEmpty()){

                }else {
                    ordersModelList.clear();
                    //get ordered data
                    checkconnection();
                }
            }
        });

        return orders;
    }

    public void getOdered_Data() {

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.ORDERS+"?userid="+userid,
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


                                String referenceno = json.getString("book_id");
                                String amount = json.getString("amount");
                                String dateofpayment = json.getString("date_of_payments");
                                String timeofpayment = json.getString("time_of_payments");
                                String dateofvalid = json.getString("date_of_valid");
                                String timeofvalid = json.getString("time_of_valid");
                                String paymentstatus = json.getString("payment_status");
                                String noofdays = json.getString("no_of_days");



                                ordersModelList.add(new Orders_model(referenceno,noofdays,amount,dateofpayment,timeofpayment,dateofvalid,timeofvalid,paymentstatus));

                                //creating adapter
                                adapterorders = new OrdersAdapter(getActivity(), ordersModelList);

                                //adding adapter to recyclerview
                                recyclerView_orders.setAdapter(adapterorders);
                                adapterorders.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterorders.showshimmer = false;

                                        adapterorders.notifyDataSetChanged();
                                    }
                                }, 1500);
                            }

                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterorders.showshimmer = false;

                                    adapterorders.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(), "Session Expired");

                        }else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            // Showing error message if something goes wrong.

                            CommonMethod.showToast(getActivity(), "No Orders Booked");

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

            getOdered_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

}