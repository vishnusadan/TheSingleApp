package com.thesingleapp.thesingleapp.tab.Message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.thesingleapp.thesingleapp.adapter.MessageAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Message_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message_Tab_Fragment extends Fragment {

    private RecyclerView recyclerViewmessage;

    Context mContext;
    //adapter object
    private MessageAdapter adapterMessage;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    public static Handler msghandler;

    public static String msgthread = "no";

    public int extracount = 0;

    ImageView logo;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Message_model> messageModelList;

    public String token,userid,useridvalue,first_name, msg,status,date,profileimage,friendvalue,friendonlyvalue,countvalue,premium;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();

        msghandler = new Handler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                extracount = 0;
                msgthread="yes";
                messageModelList.clear();
                //GetMsgcount
                getMessagethread_data();

                msghandler.postDelayed(this, 7000);//300000=5mins

            }
        }, 7000);//300000=5mins
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View message = inflater.inflate(R.layout.fragment_tab_message, container, false);

        // init SwipeRefreshLayout and ListView

        logo = message.findViewById(R.id.applogo);
        recyclerViewmessage = message.findViewById(R.id.recyclerView_msg);
        recyclerViewmessage.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewmessage.setLayoutManager(llm);

        messageModelList = new ArrayList<Message_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());


        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();

        //messages data

        checkconnection();

        // implement setOnRefreshListener event on SwipeRefreshLayout

        recyclerViewmessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraMessage_data();
                }
            }
        });

        return message;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopRepeatingTask();
        //messages data

    }


    public void stopRepeatingTask()
    {
        if(msgthread.equals("yes")) {
            msghandler.removeCallbacksAndMessages(null);
            msgthread = "no";
        }

    }
    public void getMessage_data(){

        CommonMethod.showpDialog(getActivity(),"Loading");
        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.MESSAGE+"?q=list&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        CommonMethod.hidepDialog();

                        try {

                            JSONObject data = new JSONObject(ServerResponse);

                            JSONArray jsondata = data.getJSONArray("data");
                            JSONObject count = data.getJSONObject("count");
                            JSONObject friend = data.getJSONObject("friend");
                            JSONObject friendonly = data.getJSONObject("friend_only");

                            if(jsondata!=null && jsondata.length()>0) {


                            } else {

                                logo.setVisibility(View.VISIBLE);

                            }

                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);

                                //to get the username correctly
                                if(userid.equals(json.getString("user"))) {

                                    String currentuserid = json.getString("other");

                                    countvalue = count.getString(currentuserid);
                                    friendvalue = friend.getString(currentuserid);
                                    friendonlyvalue = friendonly.getString(currentuserid);
                                    useridvalue = json.getString("other");
                                    first_name = json.getString("o_first_name");
                                    msg = json.getString("msg");
                                    status = json.getString("o_online");
                                    date = json.getString("str_date");
                                    profileimage = json.getString("o_image");
                                    premium = json.getString("o_is_premium");


                                }else {

                                    String currentuserid = json.getString("user");

                                    countvalue = count.getString(currentuserid);
                                    friendvalue = friend.getString(currentuserid);
                                    friendonlyvalue = friendonly.getString(currentuserid);
                                    useridvalue = json.getString("user");
                                    first_name = json.getString("u_first_name");
                                    msg = json.getString("msg");
                                    status = json.getString("u_online");
                                    date = json.getString("str_date");
                                    profileimage = json.getString("u_image");
                                    premium = json.getString("u_is_premium");


                                }
                                messageModelList.add(new Message_model(useridvalue,first_name, msg,status,date, profileimage,countvalue,premium,friendvalue,friendonlyvalue));


                                //creating adapter
                                adapterMessage = new MessageAdapter(getActivity(), messageModelList);

                                //adding adapter to recyclerview
                                recyclerViewmessage.setAdapter(adapterMessage);
                                adapterMessage.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
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

            getMessage_data();
        } else {

             logo.setImageResource(R.drawable.nointernet);
             logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    public void getExtraMessage_data(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.MESSAGE+"?q=list&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONObject data = new JSONObject(ServerResponse);

                            JSONArray jsondata = data.getJSONArray("data");
                            JSONObject count = data.getJSONObject("count");

                            if(jsondata!=null && jsondata.length()>0) {

                                CommonMethod.showpDialog(getActivity(),"Loading");

                                for (int i = jsondata.length()-1; i>=0; i--) {

                                    JSONObject json = jsondata.getJSONObject(i);
                                    JSONObject friend = data.getJSONObject("friend");
                                    JSONObject friendonly = data.getJSONObject("friend_only");


                                    //to get the username correctly
                                    if(userid.equals(json.getString("user"))) {

                                        String currentuserid = json.getString("other");

                                        countvalue = count.getString(currentuserid);
                                        friendvalue = friend.getString(currentuserid);
                                        friendonlyvalue = friendonly.getString(currentuserid);
                                        useridvalue = json.getString("other");
                                        first_name = json.getString("o_first_name");
                                        msg = json.getString("msg");
                                        status = json.getString("o_online");
                                        date = json.getString("str_date");
                                        profileimage = json.getString("o_image");
                                        premium = json.getString("o_is_premium");


                                    }else {

                                        String currentuserid = json.getString("user");

                                        countvalue = count.getString(currentuserid);
                                        friendvalue = friend.getString(currentuserid);
                                        friendonlyvalue = friendonly.getString(currentuserid);
                                        useridvalue = json.getString("user");
                                        first_name = json.getString("u_first_name");
                                        msg = json.getString("msg");
                                        status = json.getString("u_online");
                                        date = json.getString("str_date");
                                        profileimage = json.getString("u_image");
                                        premium = json.getString("u_is_premium");


                                    }
                                    messageModelList.add(new Message_model(useridvalue,first_name, msg,status,date, profileimage,countvalue,premium,friendvalue,friendonlyvalue));

                                    CommonMethod.hidepDialog();

//                                creating adapter
                                    adapterMessage = new MessageAdapter(getActivity(), messageModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewmessage.setAdapter(adapterMessage);
                                    adapterMessage.notifyDataSetChanged();

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

                            CommonMethod.showToast(getActivity(), "You Dont Have Any Message more");
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
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }

    public void getMessagethread_data(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.MESSAGE+"?q=list&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONObject data = new JSONObject(ServerResponse);

                            JSONArray jsondata = data.getJSONArray("data");
                            JSONObject count = data.getJSONObject("count");
                            JSONObject friend = data.getJSONObject("friend");
                            JSONObject friendonly = data.getJSONObject("friend_only");

                            if(jsondata!=null && jsondata.length()>0) {


                            } else {

                            }

                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);

                                //to get the username correctly
                                if(userid.equals(json.getString("user"))) {

                                    String currentuserid = json.getString("other");

                                    countvalue = count.getString(currentuserid);
                                    friendvalue = friend.getString(currentuserid);
                                    friendonlyvalue = friendonly.getString(currentuserid);
                                    useridvalue = json.getString("other");
                                    first_name = json.getString("o_first_name");
                                    msg = json.getString("msg");
                                    status = json.getString("o_online");
                                    date = json.getString("str_date");
                                    profileimage = json.getString("o_image");
                                    premium = json.getString("o_is_premium");

                                }else {

                                    String currentuserid = json.getString("user");

                                    countvalue = count.getString(currentuserid);
                                    friendvalue = friend.getString(currentuserid);
                                    friendonlyvalue = friendonly.getString(currentuserid);
                                    useridvalue = json.getString("user");
                                    first_name = json.getString("u_first_name");
                                    msg = json.getString("msg");
                                    status = json.getString("u_online");
                                    date = json.getString("str_date");
                                    profileimage = json.getString("u_image");
                                    premium = json.getString("u_is_premium");


                                }
                                messageModelList.add(new Message_model(useridvalue,first_name, msg,status,date, profileimage,countvalue,premium,friendvalue,friendonlyvalue));


//                                creating adapter
                                adapterMessage = new MessageAdapter(getActivity(), messageModelList);

                                recyclerViewmessage.setAdapter(adapterMessage);
                                adapterMessage.notifyDataSetChanged();

                            }

                        } catch (JSONException e) {
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
                            CommonMethod.showToast(getActivity(),"Session Expired");

                        }else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);

       // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }
}
