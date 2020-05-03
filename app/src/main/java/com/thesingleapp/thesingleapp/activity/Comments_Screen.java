package com.thesingleapp.thesingleapp.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.adapter.CommentsAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Comments_model;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.thesingleapp.thesingleapp.activity.OthersProfile.likesCountConvet;

public class Comments_Screen extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerViewfavorited;

    //adapter object
    private CommentsAdapter adaptercomments;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private long mLastClickTime = 0;


    //thread handler
    public static Handler msghandler;

    public static String msgthread = "no";

    public int arraycount;

    private StringRequest stringRequest;



    //list to hold all the uploaded images
    private List<Comments_model> commentsModelList;

    private ImageView heart;
    private EditText ed_msg;
    private TextView likescounttv,commentscounttv;
    private ConstraintLayout mainlayout;
    public static String likes= "false";
    public String token,userid,useridvalue,first_name,city,profileimage,imageid,msg,otheruserid,username,commentscount,likescount,image,likeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments__screen);

        mainlayout = findViewById(R.id.constramain);
        recyclerViewfavorited = findViewById(R.id.recyclerView_favoritedme);
        recyclerViewfavorited.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewfavorited.setLayoutManager(llm);

        likescounttv = findViewById(R.id.tvTitle);
        commentscounttv = findViewById(R.id.tvcity);
        ImageView send = findViewById(R.id.send);
        ImageView backbutton = findViewById(R.id.backbutton);
        heart = findViewById(R.id.heart);
        ImageView imageview = findViewById(R.id.ivImage);
        ed_msg = findViewById(R.id.typemessage);

        send.setOnClickListener(this);
        backbutton.setOnClickListener(this);
        heart.setOnClickListener(this);
        likescounttv.setOnClickListener(this);

        Intent intent = getIntent();
        imageid = intent.getStringExtra("imageid");
        otheruserid = intent.getStringExtra("otheruserid");
        image = intent.getStringExtra("image");
        likescount = intent.getStringExtra("likescount");
        commentscount = intent.getStringExtra("commentscount");

        commentsModelList = new ArrayList<Comments_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(this);

        //creating adapter
        adaptercomments = new CommentsAdapter(getApplicationContext(), commentsModelList);

        //adding adapter to recyclerview
        recyclerViewfavorited.setAdapter(adaptercomments);
        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();
        username = data.getUsername();

        Glide.with(Comments_Screen.this)
                .load(Api.IP+image)
                .placeholder(R.drawable.single_image_dummy)
                .dontAnimate()
                .centerCrop().into(imageview);

        likescounttv.setText(likescount+" Likes");
        commentscounttv.setText(commentscount+" Comments");


        //Comment
        getComments_Data();

        msghandler = new Handler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                msgthread="yes";

                //GetMsgcount
                getExtraComments_Data();

                msghandler.postDelayed(this, 6000);//300000=5mins

            }
        }, 6000);//300000=5mins
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // to stop the thread
        stopRepeatingTask();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // to stop the thread
        stopRepeatingTask();

    }
    @Override
    protected void onStop() {
        super.onStop();
        // to stop the thread
        stopRepeatingTask();
    }


    public void getComments_Data(){

        //clear the array
        commentsModelList.clear();

        stringRequest = new StringRequest(Request.Method.GET, Api.COMMENT+imageid+"/?other="+otheruserid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {
                            JSONObject image = new JSONObject(ServerResponse);

                            JSONArray commentcountv = image.getJSONArray("commentimage");

                            String userimagelike = image.getString("user_imagelike");


                            String[] splitedlike = userimagelike.split("-");

                            String likeuser = splitedlike[0];
                            likeid = splitedlike[1];

                            if(commentcountv!=null && commentcountv.length()>0) {


                            } else {



                            }
                            if (likeuser.equals("yes")) {

                                heart.setImageResource(R.drawable.likes_image_pink);

                                likes = "true";

                            } else {

                                heart.setImageResource(R.drawable.likes_border);

                                likes = "false";

                            }


                             arraycount = commentcountv.length();
                            //taking array count for comment count
                            commentscounttv.setText(arraycount +" Comments");

                            for (int i =0; i<=arraycount-1;  i++) {

                                JSONObject json = commentcountv.getJSONObject(i);

                                useridvalue = json.getString("user");
                                msg = json.getString("msg");
                                first_name = json.getString("u_first_name");
                                profileimage = json.getString("u_image");

                                commentsModelList.add(new Comments_model(useridvalue,msg,first_name, profileimage));
                                //creating adapter
                                adaptercomments = new CommentsAdapter(getApplicationContext(), commentsModelList);

                                //adding adapter to recyclerview
                                recyclerViewfavorited.setAdapter(adaptercomments);
                                adaptercomments.notifyDataSetChanged();


                                //scroll to bottom
                                scrollToBottom();

                            }

                        } catch (JSONException e) {
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

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {


        if (commentsModelList.isEmpty()) {

        } else {

            adaptercomments.notifyDataSetChanged();

            if (adaptercomments.getItemCount() > 1)

                recyclerViewfavorited.getLayoutManager().smoothScrollToPosition(recyclerViewfavorited, null, adaptercomments.getItemCount() - 1);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.send:


                if (ed_msg.getText().toString().equals("")){

                    ed_msg.setError("Please Enter The Comment");

                }else if ( UserData.COMMENTPERMISSION.equals("no")){


                    CommonMethod.showToast(Comments_Screen.this, "Only Friends Can Comment");

                }else  {
                    //updatecomment
                    uploadComment();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainlayout.getWindowToken(), 0);
                }

                break;

            case R.id.backbutton:


                finish();

                break;

            case R.id.heart:

                likeOnclick();
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                break;

            case R.id.tvTitle:

                likeOnclick();

                break;
        }
    }

    public void uploadComment(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, String.format("%s",Api.SENDCOMMENT),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        commentsModelList.add(new Comments_model(userid,ed_msg.getText().toString(),username, profileimage));
                        //creating adapter


                        ed_msg.setText("");

                        arraycount=arraycount+1;

                        commentscounttv.setText(arraycount +" Comments");

                        scrollToBottom();
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.

                        CommonMethod.showToast(Comments_Screen.this, "Something Went Wrong");
                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("msg", ed_msg.getText().toString());
                params.put("user", userid);
                params.put("other", otheruserid);
                params.put("image", imageid);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token "+token);
                return params;
            }


        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(Comments_Screen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }




    public void likeOnclick(){

        if(likes.equals("true")){


            likes = "false";

            heart.setImageResource(R.drawable.likes_border);

            Integer likescountv = Integer.parseInt(likescount)-1;

            likescount = String.valueOf(likescountv);

            if (likescountv == 0 || likescountv < 0){

                likescounttv.setText("0 Likes");

            }else {

                likescounttv.setText(likesCountConvet(String.valueOf(likescountv))+" Likes");

            }

            //to hit the api
            UnFLF();



        }else {


            likes = "true";

            heart.setImageResource(R.drawable.likes_image_pink);

            Integer likescountv = Integer.parseInt(likescount)+1;

            likescount = String.valueOf(likescountv);

            likescounttv.setText(likesCountConvet(String.valueOf(likescountv))+" Likes");

            //to hit the api
            HITDATA();

        }
    }

    public void UnFLF(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.DELETE, Api.IMAGELIKE+likeid+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {



                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.

                        CommonMethod.showToast(Comments_Screen.this, "Something Went Wrong");

                    }
                }
        )

        {
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

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(Comments_Screen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public void HITDATA(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.IMAGELIKE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {
                            JSONObject json = new JSONObject(ServerResponse);

                            String status = json.getString("status");

                            if (status.equals("success")){

                                likeid = json.getString("id");


                            }

                        } catch (JSONException e) {
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

                // Adding All values to Params.


                params.put("image_id", imageid);
                params.put("user", userid);
                params.put("other", otheruserid);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(Comments_Screen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public void stopRepeatingTask()
    {
        if(msgthread.equals("yes")) {
            msghandler.removeCallbacksAndMessages(null);
            msgthread = "no";
        }

    }
   public void getExtraComments_Data()
   {
       stringRequest = new StringRequest(Request.Method.GET, Api.COMMENT+imageid+"/?other="+otheruserid,
               new Response.Listener<String>() {
                   @Override
                   public void onResponse(String ServerResponse) {


                       try {

                           JSONObject image = new JSONObject(ServerResponse);

                           JSONArray commentcountv1 = image.getJSONArray("commentimage");

                           String userimagelike = image.getString("user_imagelike");


                           String[] splitedlike = userimagelike.split("-");

                           String likeuser = splitedlike[0];
                           likeid = splitedlike[1];

                           if(commentcountv1!=null && commentcountv1.length()> arraycount)
                           {
                               if (likeuser.equals("yes")) {

                                   heart.setImageResource(R.drawable.likes_image_pink);

                                   likes = "true";

                               } else {

                                   heart.setImageResource(R.drawable.likes_border);

                                   likes = "false";

                               }


                               int arraycount1 = commentcountv1.length();

                               //taking array count for comment count
                               commentscounttv.setText(arraycount1+" Comments");

                               for (int i =commentcountv1.length()-1; i>=arraycount;  i--) {

                                   JSONObject json = commentcountv1.getJSONObject(i);

                                   useridvalue = json.getString("user");
                                   msg = json.getString("msg");
                                   first_name = json.getString("u_first_name");
                                   profileimage = json.getString("u_image");

                                   commentsModelList.add(new Comments_model(useridvalue,msg,first_name, profileimage));

                                   //creating adapter
                                   adaptercomments = new CommentsAdapter(getApplicationContext(), commentsModelList);

                                   //adding adapter to recyclerview
                                   recyclerViewfavorited.setAdapter(adaptercomments);
                                   adaptercomments.notifyDataSetChanged();

                                    arraycount=commentcountv1.length();

                                   //scroll to bottom
                                   scrollToBottom();

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

                       stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                               100,
                               DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                               DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                       // Showing error message if something goes wrong.

                       CommonMethod.showToast(Comments_Screen.this, "You Have No Comments");
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

//             Adding the StringRequest object into requestQueue.
       requestQueue.add(stringRequest);
    }



}
