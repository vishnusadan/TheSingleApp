package com.thesingleapp.thesingleapp.activity;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.adapter.PremiumAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.fragment.PremiumDetailDialogFragment;
import com.thesingleapp.thesingleapp.model.Premium_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PremiumScreen extends AppCompatActivity implements View.OnClickListener, PremiumDetailDialogFragment.DialogListener{


    private RecyclerView recyclerView_premium;

    //adapter object
    private PremiumAdapter adapterpremium;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Premium_model> premiumModelList;

    private LinearLayout premiumdetails;

    private ImageView back, logo;

    private TextView Screenname;

    public String token,userid,useridvalue,first_name,city,profileimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_screen);

        recyclerView_premium = findViewById(R.id.recyclerView_premium);
        premiumdetails = findViewById(R.id.premiumdetails);
        logo = findViewById(R.id.applogo);
        back= findViewById(R.id.backbutton);
        Screenname= findViewById(R.id.textpremium);
        premiumdetails.setOnClickListener(this);
        recyclerView_premium.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_premium.setLayoutManager(llm);

        premiumModelList = new ArrayList<Premium_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(this);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        //Viewed me
        getPremium();

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

    }

    public void getPremium() {

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PREMIUM,
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


                                String id = json.getString("id");
                                String noofdays = json.getString("no_of_days");
                                String amount = json.getString("amount");
                                String month = json.getString("month");
                                String year = json.getString("year");


                                premiumModelList.add(new Premium_model(id,noofdays,amount,month,year));

                                //creating adapter
                                adapterpremium = new PremiumAdapter(getApplicationContext(), premiumModelList);

                                //adding adapter to recyclerview
                                recyclerView_premium.setAdapter(adapterpremium);
                                adapterpremium.notifyDataSetChanged();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapterpremium.showshimmer = false;

                                        adapterpremium.notifyDataSetChanged();
                                    }
                                }, 1500);
                            }

                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    adapterpremium.showshimmer = false;

                                    adapterpremium.notifyDataSetChanged();
                                }
                            }, 1500);
                            e.printStackTrace();
                        }


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                adapterpremium.showshimmer = false;

                                adapterpremium.notifyDataSetChanged();
                            }
                        }, 1500);

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(PremiumScreen.this, "No Premium Price Announced");
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

       //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.premiumdetails:

                PremiumDetailDialogFragment dialogFragment = new PremiumDetailDialogFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean("notAlertDialog", true);

                dialogFragment.setArguments(bundle);

                FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
                Fragment prev = this.getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                dialogFragment.show(ft, "dialog");


                break;

        }
    }

    // filterdailoge values return
    @Override
    public void onFinishEditDialog(String inputText) {

        if (TextUtils.isEmpty(inputText)) {
        } else{
        }
    }

}
