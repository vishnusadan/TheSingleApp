package com.thesingleapp.thesingleapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.adapter.ChatAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Chat_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ChatScreen extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mMessageRecycler;
    private ChatAdapter mMessageAdapter;
    private LinearLayout chatspacerlayout;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private StringRequest stringRequest;

    //list to hold all the uploaded images
    private List<Chat_model> messageModelList;

    String msgid,token,userid,otheruserid,blocked,otheruseridvalue,first_name,date, message, profileimage,username,userprofilepic,current,current1,format;

    private ImageView send;

    private EditText ed_message;

    public static int Maxlength = 0;

    //thread handler
    public static Handler msghandler;

    public static String msgthread = "no";

    public static String reachedvalue = "";

    public int extracount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        mMessageRecycler = findViewById(R.id.recyclerView_chat);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));

        send = findViewById(R.id.send);
        ImageView backbtn = findViewById(R.id.backbutton);
        ed_message = findViewById(R.id.typemessage);
        chatspacerlayout = findViewById(R.id.chatspacer);
        send.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        ed_message.setOnClickListener(this);
        ed_message.addTextChangedListener(textWatcher);


        messageModelList = new ArrayList<Chat_model>();

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(this);

        //progress daialog


        Intent intent = getIntent();

        otheruserid = intent.getStringExtra("UserId");


        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();
        username = data.getUsername();
        userprofilepic = data.getProfilepic();


        CommonMethod.showpDialog(ChatScreen.this,"Loading...");

        //get chat messages
        getChat_Data();

        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        current = simpleDateFormat.format(new Date());

        //For Time
        SimpleDateFormat dateFormat = new SimpleDateFormat( "hh:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        current1= dateFormat.format(today);

         // Concatenation to our date format
        format = current+"T"+current1;

        //creating adapter
        mMessageAdapter = new ChatAdapter(getApplicationContext(), messageModelList);

        //adding adapter to recyclerview
        mMessageRecycler.setAdapter(mMessageAdapter);

        mMessageRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    reachedvalue= "no";
                } else {
                    // Scrolling down
                    reachedvalue= "yes";

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if(reachedvalue.equals("yes")) {
                        extracount = extracount+1;

                        //get older chat
                        getOlderChat_Data();
                    }
                    // Do something
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                } else {
                    // Do something
                }
            }
        });



    }



    // to check msg typing to make scroll down
    private TextWatcher textWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

            //to scroll down
            scrollToBottom();
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }};


    public void getChat_Data(){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.MESSAGE+"?user="+userid+"&other="+otheruserid+"&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

//                         Showing response message coming from server.
                        CommonMethod.hidepDialog();

                        try {

                            JSONObject data = new JSONObject(ServerResponse);

                            JSONArray jsondata = data.getJSONArray("data");
                            blocked = data.getString("blocked");

                            if (blocked.equals("true")){

                                chatspacerlayout.setVisibility(View.GONE);

                            }else {

                                chatspacerlayout.setVisibility(View.VISIBLE);

                            }

                            for (int i = jsondata.length()-1; i>=0; i--) {

                                JSONObject json = jsondata.getJSONObject(i);

                                msgid = json.getString("id");
                                otheruseridvalue = json.getString("user");
                                first_name = json.getString("u_first_name");
                                message = json.getString("msg");
                                date = json.getString("str_date");
                                profileimage = json.getString("u_image");

                                messageModelList.add(new Chat_model(otheruseridvalue,first_name, message,date, profileimage,msgid));

                                //creating adapter
                                mMessageAdapter = new ChatAdapter(getApplicationContext(), messageModelList);

                                //adding adapter to recyclerview
                                mMessageRecycler.setAdapter(mMessageAdapter);

                                mMessageAdapter.notifyDataSetChanged();

                                //scroll to bottom
                                scrollToBottom();

                                String msglastid = json.getString("id");

                                Maxlength = Integer.parseInt(msglastid);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        CommonMethod.hidepDialog();

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

    public void get_Extra_Chat_Data() {

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.MESSAGE+"?user="+userid+"&other="+otheruserid+"&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {

                            JSONObject data = new JSONObject(ServerResponse);

                            JSONArray jsondata = data.getJSONArray("data");

                            blocked = data.getString("blocked");

                            if (blocked.equals("true")){

                                chatspacerlayout.setVisibility(View.GONE);

                            }else {

                                chatspacerlayout.setVisibility(View.VISIBLE);

                            }

                                JSONObject id = jsondata.getJSONObject(0);


                                String msglastid = id.getString("id");

                                if (Maxlength < Integer.parseInt(msglastid)) {


                                for (int i = Integer.parseInt(msglastid); i > Maxlength; i--) {

                                int j = i-Maxlength-1;

                                JSONObject json = jsondata.getJSONObject(j);

                                msgid = json.getString("id");
                                otheruseridvalue = json.getString("user");
                                first_name = json.getString("u_first_name");
                                message = json.getString("msg");
                                date = json.getString("str_date");
                                profileimage = json.getString("u_image");

                                //creating adapter
                                messageModelList.add(new Chat_model(otheruseridvalue,first_name, message,date, profileimage,msgid));

                                    }

                                    Maxlength = Integer.parseInt(msglastid);

                                    //scroll to bottom
                                    scrollToBottom();
                                }

                            else {

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

                            startActivity(new Intent(ChatScreen.this, LoginScreen.class));
                            finish();
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


    //method to scroll the recyclerview to bottom
    private void scrollToBottom() {

        if(messageModelList.isEmpty()){

        }else {

            mMessageAdapter.notifyDataSetChanged();
            if (mMessageAdapter.getItemCount() > 1)
                mMessageRecycler.getLayoutManager().smoothScrollToPosition(mMessageRecycler, null, mMessageAdapter.getItemCount() - 1);
        }

    }

    public void getOlderChat_Data(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.MESSAGE+"?user="+userid+"&other="+otheruserid+"&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONObject data = new JSONObject(ServerResponse);

                            JSONArray jsondata = data.getJSONArray("data");

                            if (jsondata != null && jsondata.length() > 0) {

                            for (int i = 0; i < jsondata.length(); i++) {

                                JSONObject json = jsondata.getJSONObject(i);

                                msgid = json.getString("id");
                                otheruseridvalue = json.getString("user");
                                first_name = json.getString("u_first_name");
                                message = json.getString("msg");
                                date = json.getString("str_date");
                                profileimage = json.getString("u_image");

                                messageModelList.add(0,new Chat_model(otheruseridvalue,first_name, message,date, profileimage,msgid));

                                //creating adapter
                                mMessageAdapter = new ChatAdapter(getApplicationContext(), messageModelList);

                                //adding adapter to recyclerview
                                mMessageRecycler.setAdapter(mMessageAdapter);

                            }

                            }else {

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

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.send:

                if (ed_message.getText().toString().equals("")){


                }else {

                    send.setEnabled(false);
                    uploadMessage();

                }


                break;

            case R.id.backbutton:

                // to stop the thread
                stopRepeatingTask();

                finish();


                break;


            case R.id.typemessage:

                //scroll to bottom
                scrollToBottom();

                break;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // to stop the thread
        stopRepeatingTask();

        finish();
    }

    public void uploadMessage(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, String.format("%s",Api.MESSAGE),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //creating adapter
                        messageModelList.add(new Chat_model(userid,username, ed_message.getText().toString(),format, userprofilepic,msgid));

                        ed_message.setText("");

                        //increase the length to remove duplicate msg
                        Maxlength = Maxlength+1;

                        scrollToBottom();
                        send.setEnabled(true);
                        // to start the thread
//                        onResume();

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        //clear the array
                        messageModelList.clear();
//                        get chat item
                        getChat_Data();


                        scrollToBottom();


                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(ChatScreen.this, "Can't send message");
                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("msg", ed_message.getText().toString());
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
        RequestQueue requestQueue = Volley.newRequestQueue(ChatScreen.this);

         //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }


    public void stopRepeatingTask()
    {
        if(msgthread.equals("yes")) {
            msghandler.removeCallbacksAndMessages(null);
            msgthread = "no";
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        msghandler = new Handler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                msgthread="yes";

                //GetMsgcount
                getmessagedata();

                msghandler.postDelayed(this, 6000);//300000=5mins

            }
        }, 6000);//300000=5mins

    }

    private void getmessagedata() {

        get_Extra_Chat_Data();

    }
    @Override
    protected void onDestroy() {

        stopRepeatingTask();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        stopRepeatingTask();
        super.onPause();
    }

}
