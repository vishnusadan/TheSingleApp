package com.thesingleapp.thesingleapp.tab.Requests;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.thesingleapp.thesingleapp.adapter.FriendsAdapter;
import com.thesingleapp.thesingleapp.adapter.RequestAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Blocked_model;
import com.thesingleapp.thesingleapp.model.Connections_model;
import com.thesingleapp.thesingleapp.model.Request_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Requests_Fragment extends Fragment {

    private RecyclerView recyclerViewrequest;

    //adapter object
    private RequestAdapter adapterrequest;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;


    private StringRequest stringRequest;

    ImageView logo;

    public int extracount = 0;

    //list to hold all the uploaded images
    private List<Request_model> friendsModelList;

    public String token,userid,useridvalue,first_name,city,date,profileimage,premium,status,id,otheridvalue;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View request =inflater.inflate(R.layout.fragment_requests,container,false);
        logo = request.findViewById(R.id.applogo);
        recyclerViewrequest = request.findViewById(R.id.recyclerView_request);
        recyclerViewrequest.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewrequest.setLayoutManager(llm);


        friendsModelList = new ArrayList<Request_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());


        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();


        //Flirted me
        checkconnection();

        recyclerViewrequest.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

//                    getExtraFlirtedme_Data();
                }
            }
        });

        return request;
    }

    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getrequest_Data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    private void getrequest_Data() {

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.REQUEST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);
                            Log.d("request", String.valueOf(jsondata));
                            if(jsondata!=null && jsondata.length()>0) {

                            } else {
                                logo.setVisibility(View.VISIBLE);

                            }
                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);


                                    id = json.getString("id");
                                    useridvalue = json.getString("user");
                                    otheridvalue = json.getString("other");
                                    city = json.getString("u_city");
                                    premium  = json.getString("u_is_premium");
                                    first_name = json.getString("u_first_name");
                                    profileimage = json.getString("u_image");

                                    Log.d("id",id);
                                    Log.d("useridvalue",useridvalue);
                                Log.d("otheridvalue",otheridvalue);
                                Log.d("city",city);
                                Log.d("premium",premium);
                                Log.d("first_name",first_name);
                                Log.d("profileimage",profileimage);





                                friendsModelList.add(new Request_model(useridvalue,id,otheridvalue,first_name,profileimage,premium,city));


                                // creating adapter
                                adapterrequest = new RequestAdapter(getActivity(), friendsModelList);

                                //adding adapter to recyclerview
                                recyclerViewrequest.setAdapter(adapterrequest);
                                adapterrequest.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterrequest.showshimmer = false;

                                        adapterrequest.notifyDataSetChanged();
                                    }
                                }, 1500);


                            }

                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterrequest.showshimmer = false;

                                    adapterrequest.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"You Dont Have Any Friends");

                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

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
