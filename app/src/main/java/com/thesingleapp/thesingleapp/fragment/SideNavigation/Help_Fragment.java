package com.thesingleapp.thesingleapp.fragment.SideNavigation;


import android.content.Intent;
import android.os.Bundle;
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
import com.thesingleapp.thesingleapp.adapter.HelpAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Help_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Help_Fragment extends Fragment {


    private RecyclerView recyclerViewreply;

    //adapter object
    private HelpAdapter adapterGifts;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Help_model> helpModelList;

    ImageView logo;

    public Help_Fragment() {
    }

    public String token,userid;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View help =inflater.inflate(R.layout.fragment_help,container,false);


        recyclerViewreply = help.findViewById(R.id.recyclerView_inbox);
        recyclerViewreply.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewreply.setLayoutManager(llm);
        logo = help.findViewById(R.id.applogo);

        helpModelList = new ArrayList<Help_model>();

        // Creating Volley newRequestQueue.
        requestQueue = Volley.newRequestQueue(getActivity());

        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();

        // Get data for help
        checkconnection();

        return help;
    }

    public void getData(){

      // Showing progress dialog at user registration time.
        CommonMethod.showpDialog(getActivity(),"Please Wait");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.HELP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        // Hiding the progress dialog after all task complete.
                        CommonMethod.hidepDialog();

                        try {

                            JSONArray jsondata = new JSONArray(ServerResponse);


                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);

                                String help = json.getString("question");
                                String answer = json.getString("answer");


                                if(help!=null && help.length()>0 || answer!=null && answer.length()>0) {


                                } else {

                                    logo.setVisibility(View.VISIBLE);
                                    CommonMethod.showToast(getActivity(),"Not able to help you Sorry for Our Inconvenience ");

                                }


                                helpModelList.add(new Help_model(help,answer));

                                //creating adapter
                                adapterGifts = new HelpAdapter(getActivity(), helpModelList);

                                //adding adapter to recyclerview
                                recyclerViewreply.setAdapter(adapterGifts);
                                adapterGifts.notifyDataSetChanged();

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

                        }else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        }


                    }
                }
        )

        {
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

        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    public void checkconnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getData();
        } else {

            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }
}