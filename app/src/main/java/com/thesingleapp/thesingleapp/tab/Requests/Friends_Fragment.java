package com.thesingleapp.thesingleapp.tab.Requests;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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
import com.thesingleapp.thesingleapp.adapter.FriendsAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Blocked_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friends_Fragment extends Fragment {

    private RecyclerView recyclerViewmessage;

    //adapter object
    private FriendsAdapter adapterMessage;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    ImageView logo;

    public int extracount = 0;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Blocked_model> friendsModelList;

    public String token,userid,useridvalue,first_name, msg,status,date,profileimage,premium,city;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View message = inflater.inflate(R.layout.fragment_friends, container, false);

        logo = message.findViewById(R.id.applogo);
        recyclerViewmessage = message.findViewById(R.id.recyclerView_msg);
        recyclerViewmessage.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerViewmessage.setLayoutManager(mGridLayoutManager);

        friendsModelList = new ArrayList<Blocked_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());


        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();

        //Friends data
        checkconnection();

        recyclerViewmessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraFriends_data();
                }
            }
        });
        return message;
    }

    @Override
    public void onResume() {

        if (friendsModelList.isEmpty()){
            friendsModelList.clear();
        }else {
            friendsModelList.clear();
            //messages data
            getFriends_data();
        }
        super.onResume();
    }

    public void getFriends_data(){

        friendsModelList.clear();

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.FRIENDS+"?q="+userid+"&accepted=true&count=0",
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
                                if (json.getString("user").equals(userid)){

                                    useridvalue = json.getString("other");
                                    first_name = json.getString("o_first_name");
                                    status = json.getString("o_online");
                                    profileimage = json.getString("o_image");
                                    premium  = json.getString("o_is_premium");


                                }else {

                                    useridvalue = json.getString("user");
                                    first_name = json.getString("u_first_name");
                                    status = json.getString("u_online");
                                    profileimage = json.getString("u_image");
                                    premium  = json.getString("u_is_premium");

                                }


                                friendsModelList.add(new Blocked_model(useridvalue,first_name,status,profileimage,premium));


                                // creating adapter
                                adapterMessage = new FriendsAdapter(getActivity(), friendsModelList);

                                //adding adapter to recyclerview
                                recyclerViewmessage.setAdapter(adapterMessage);
                                adapterMessage.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterMessage.showshimmer = false;

                                        adapterMessage.notifyDataSetChanged();
                                    }
                                }, 1500);


                            }

                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterMessage.showshimmer = false;

                                    adapterMessage.notifyDataSetChanged();
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
    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getFriends_data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    public void getExtraFriends_data(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.FRIENDS+"?q="+userid+"&accepted=true&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);

                            if(jsondata!=null && jsondata.length()>0) {


                                for (int i = jsondata.length()-1; i>=0; i--) {

                                    JSONObject json = jsondata.getJSONObject(i);

                                    if (json.getString("user").equals(userid)){

                                        useridvalue = json.getString("other");
                                        first_name = json.getString("o_first_name");
                                        status = json.getString("o_online");
                                        profileimage = json.getString("o_image");
                                        premium  = json.getString("o_is_premium");

                                    }else {

                                        useridvalue = json.getString("user");
                                        first_name = json.getString("u_first_name");
                                        status = json.getString("u_online");
                                        profileimage = json.getString("u_image");
                                        premium  = json.getString("u_is_premium");

                                    }


                                    friendsModelList.add(new Blocked_model(useridvalue,first_name,status,profileimage,premium));

                                    //creating adapter
                                    adapterMessage = new FriendsAdapter(getActivity(), friendsModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewmessage.setAdapter(adapterMessage);
                                    adapterMessage.notifyDataSetChanged();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapterMessage.showshimmer = false;

                                            adapterMessage.notifyDataSetChanged();
                                        }
                                    }, 1500);


                                }
                            } else {


                            }


                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterMessage.showshimmer = false;

                                    adapterMessage.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"You Dont Have Any Friends more");

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

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
}
