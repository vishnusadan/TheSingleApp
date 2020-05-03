package com.thesingleapp.thesingleapp.tab.Message;


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
import com.thesingleapp.thesingleapp.adapter.BlockedAdapter;
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

public class Blocked_Fragment extends Fragment {

    private RecyclerView recyclerViewmessage;

    //adapter object
    private BlockedAdapter adapterBlock;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    ImageView logo;

    public int extracount = 0;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Blocked_model> blockModelList;

    public String token,userid,useridvalue,first_name,status,profileimage,premium,city;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View blocked = inflater.inflate(R.layout.fragment_blocked, container, false);

        logo = blocked.findViewById(R.id.applogo);
        recyclerViewmessage = blocked.findViewById(R.id.recyclerView_blocked);
        recyclerViewmessage.setHasFixedSize(true);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerViewmessage.setLayoutManager(mGridLayoutManager);


        blockModelList = new ArrayList<Blocked_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();

        //messages data

        checkconnection();

        recyclerViewmessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraBlocked_data();

                }
            }
        });

        return blocked;
    }

    @Override
    public void onResume() {

        if (blockModelList.isEmpty()){

        }else {
            blockModelList.clear();
            //messages data
            getBlocked_data();
        }
        super.onResume();
    }

    public void getBlocked_data(){

        blockModelList.clear();

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.BLOCK+"?q="+userid+"&count=0",
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

                                    useridvalue = json.getString("other");
                                    first_name = json.getString("o_first_name");
                                    profileimage = json.getString("o_image");
                                    premium = json.getString("o_is_premium");


                                    blockModelList.add(new Blocked_model(useridvalue,first_name, status,profileimage,premium));


//                                creating adapter
                                    adapterBlock = new BlockedAdapter(getActivity(), blockModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewmessage.setAdapter(adapterBlock);
                                    adapterBlock.notifyDataSetChanged();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterBlock.showshimmer = false;

                                        adapterBlock.notifyDataSetChanged();
                                    }
                                }, 1500);


                            }

                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterBlock.showshimmer = false;

                                    adapterBlock.notifyDataSetChanged();
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

                            CommonMethod.showToast(getActivity(),"You Dont Have  Bloked Any One ");

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
    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getBlocked_data();
        } else {

            logo.setImageResource(R.drawable.nointernet);
            logo.setVisibility(View.VISIBLE);
            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

    public void getExtraBlocked_data(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.BLOCK+"?q="+userid+"&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);

                            if(jsondata!=null && jsondata.length()>0) {


                                for (int i = jsondata.length()-1; i>=0; i--) {

                                    JSONObject json = jsondata.getJSONObject(i);

                                    useridvalue = json.getString("other");
                                    first_name = json.getString("o_first_name");
                                    profileimage = json.getString("o_image");
                                    premium = json.getString("o_is_premium");

                                    blockModelList.add(new Blocked_model(useridvalue,first_name, status,profileimage,premium));

//                                  creating adapter
                                    adapterBlock = new BlockedAdapter(getActivity(), blockModelList);

                                    //adding adapter to recyclerview
                                    recyclerViewmessage.setAdapter(adapterBlock);
                                    adapterBlock.notifyDataSetChanged();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            adapterBlock.showshimmer = false;

                                            adapterBlock.notifyDataSetChanged();
                                        }
                                    }, 1500);

                                }
                            } else {

                            }

                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterBlock.showshimmer = false;

                                    adapterBlock.notifyDataSetChanged();
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
                            CommonMethod.showToast(getActivity(),"You Dont Have  Bloked Any One more");

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