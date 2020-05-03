package com.thesingleapp.thesingleapp.activity;


import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.thesingleapp.thesingleapp.adapter.GalleryAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.Galley_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryScreen extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    List<Galley_model> mGalleryList;
    private Galley_model mGalley_model;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;
    public int extracount = 0,Maxlength;
    private StringRequest stringRequest;
    private GalleryAdapter galleryAdapter;
    String token,userid,profilepic,otheruserid,commentpermission;

    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_screen);

        logo =findViewById(R.id.applogo);
        ImageView backbtn = findViewById(R.id.backbutton);
        mRecyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        backbtn.setOnClickListener(this);

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(this);
        mGalleryList = new ArrayList<>();

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        Intent intent=getIntent();

        otheruserid = intent.getStringExtra("otheruserid");
        commentpermission = intent.getStringExtra("commentpermission");

        // Getting images
            getGalleryData();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {


                    getExtraGalleryData();

                }
            }
        });
    }

    @Override
    protected void onResume() {

        if (mGalleryList.isEmpty()) {


        } else {

        mGalleryList.clear();
        //get data from gallery
        getGalleryData();

    }
        super.onResume();
    }

    public void getGalleryData()
    {

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.COMMENT+"?count=0&other="+otheruserid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray imagedata = new JSONArray(ServerResponse);

                            if(imagedata!=null && imagedata.length()>0) {


                            } else {
                                logo.setVisibility(View.VISIBLE);

                            }

                            for (int i = imagedata.length()-1; i>=0; i--) {

                                JSONObject image = imagedata.getJSONObject(i);

                                String id = image.getString("id");
                                String profilepic = image.getString("img");
                                JSONArray likescountv = image.getJSONArray("likeimage");
                                JSONArray commentcountv = image.getJSONArray("commentimage");
                                String likecount = String.valueOf(likescountv.length());
                                String commentcount = String.valueOf(commentcountv.length());


                                mGalley_model = new Galley_model(otheruserid,id,profilepic,likecount, commentcount                                        );
                                mGalleryList.add(mGalley_model);
                                //adding adapter to recyclerview
                                galleryAdapter = new GalleryAdapter(getApplicationContext(), mGalleryList);
                                mRecyclerView.setAdapter(galleryAdapter);

                                Maxlength = imagedata.length();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        galleryAdapter.showshimmer = false;

                                        galleryAdapter.notifyDataSetChanged();
                                    }
                                }, 1500);
                            }


                        } catch (JSONException e) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    galleryAdapter.showshimmer = false;

                                    galleryAdapter.notifyDataSetChanged();
                                }
                            }, 1500);
                            e.printStackTrace();
                        }


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.backbutton:

                finish();

                break;

        }
    }

    public void getExtraGalleryData()
    {

        extracount = extracount+1;
        mGalleryList.clear();
        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.COMMENT+"?"+ extracount+"&other="+otheruserid,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONArray imagedata = new JSONArray(ServerResponse);

                            if(imagedata.length()>Maxlength) {


                                for (int i = imagedata.length()-1; i>=0; i--) {

                                    JSONObject image = imagedata.getJSONObject(i);

                                    String id = image.getString("id");
                                    String profilepic = image.getString("img");
                                    JSONArray likescountv = image.getJSONArray("likeimage");
                                    JSONArray commentcountv = image.getJSONArray("commentimage");

                                    String likecount = String.valueOf(likescountv.length());
                                    String commentcount = String.valueOf(commentcountv.length());


                                    mGalley_model = new Galley_model(otheruserid,id,profilepic,likecount, commentcount                                        );
                                    mGalleryList.add(mGalley_model);
                                    //adding adapter to recyclerview
                                    galleryAdapter = new GalleryAdapter(getApplicationContext(), mGalleryList);
                                    mRecyclerView.setAdapter(galleryAdapter);

                                    Maxlength = imagedata.length();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            galleryAdapter.showshimmer = false;

                                            galleryAdapter.notifyDataSetChanged();
                                        }
                                    }, 1500);
                                }

                            } else {


                            }


                        } catch (JSONException e) {


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    galleryAdapter.showshimmer = false;
                                    galleryAdapter.notifyDataSetChanged();
                                }
                            }, 1500);
                            e.printStackTrace();
                        }


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
        RequestQueue requestQueue = Volley.newRequestQueue(this);

            // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
}
