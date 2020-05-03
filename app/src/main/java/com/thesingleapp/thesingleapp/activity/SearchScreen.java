package com.thesingleapp.thesingleapp.activity;


import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import com.thesingleapp.thesingleapp.adapter.SearchAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.fragment.FilterDialogFragment;
import com.thesingleapp.thesingleapp.model.SearchData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.thesingleapp.thesingleapp.fragment.SideNavigation.Matches_Fragment.screen;

public class SearchScreen extends AppCompatActivity implements View.OnClickListener {

  RecyclerView mRecyclerView;
  List<SearchData> mSearchList;
  SearchData mSearchData;
  SearchAdapter mySearchAdapter;
  // Creating Volley RequestQueue.
  RequestQueue requestQueue;


  //Matches save in share preference
  private SharedPreferences searchPreferences;
  private SharedPreferences.Editor searchPrefsEditor;


  String profilepic,token,userid;
  EditText editsearch;
  ImageView backbutton,filter;
  private StringRequest stringRequest;
  private String seekingfor;
  private String online;
  private String fromage;
  private String toage;
  private String premium;
  public int extracount = 0;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search_screen);

    backbutton = findViewById(R.id.backbutton);
    filter = findViewById(R.id.filter);
    mRecyclerView = findViewById(R.id.recyclerview);
    GridLayoutManager mGridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
    mRecyclerView.setLayoutManager(mGridLayoutManager);
    filter.setOnClickListener(this);
    backbutton.setOnClickListener(this);


    UserDataModel data = UserDataModel.getInstance();

    token = data.getToken();
    userid = data.getId();
    
    // Creating Volley newRequestQueue .
    requestQueue = Volley.newRequestQueue(SearchScreen.this);

    mSearchList = new ArrayList<>();

// to clear the data
    mSearchList.clear();

    // Locate the EditText in listview_main.xml
    editsearch = findViewById(R.id.search);

    // Capture Text in EditText
    editsearch.addTextChangedListener(new TextWatcher() {

      @Override
      public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
        mySearchAdapter.filter(text);
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1,
                                    int arg2, int arg3) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                int arg3) {
        // TODO Auto-generated method stub
      }
    });


    // Shared prefference intiated
    searchPreferences = this.getSharedPreferences("filterdata", MODE_PRIVATE);
    searchPrefsEditor = searchPreferences.edit();

    seekingfor = searchPreferences.getString("seekingfor", "");
    online = searchPreferences.getString("online", "");
    fromage = searchPreferences.getString("fromage", "");
    toage = searchPreferences.getString("toage", "");
    premium = searchPreferences.getString("premium", "");

    //if null need to add 0 becz it cant send in api
    if (fromage.equals("")){
      fromage= "0";
    }

    if (toage.equals("")){
      toage= "0";
    }

    if (seekingfor.equals("")){
      seekingfor = "B";
    }
    if (premium.equals("B"))
    {
      premium ="";
    }
    if (online.equals("B"))
    {
      online="";
    }
    getSearchScreenData();


    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

          extracount = extracount+1;

          getExtraSearchScreenData();
        }
      }
    });


  }

  public void getSearchScreenData(){

    // Creating string request with post method.
    stringRequest = new StringRequest(Request.Method.GET, Api.SEARCH+"?seeking_for="+seekingfor+"&otheronline="+online+"&fromage="+fromage+"&toage="+toage+"&premium="+premium + "&count=0",
            new Response.Listener<String>() {
              @Override
              public void onResponse(String ServerResponse) {

                try {
                  JSONArray array=new JSONArray(ServerResponse);
                  for (int i = array.length()-1; i>=0; i--) {

                    JSONObject json = array.getJSONObject(i);

                    String userid = json.getString("user");
                    String first_name = json.getString("first_name");
                    String city = json.getString("city");
                    String profile = json.getString("profile_img");
                    String status = json.getString("online");
                    String premium = json.getString("is_premium");

                    if(profile.equals("null")){


                    }else {

                      JSONObject prof = json.getJSONObject("profile_img");

                      profilepic = prof.getString("img");

                    }


                    mSearchData = new SearchData(userid,first_name, city,status,
                            premium,profilepic);
                    mSearchList.add(mSearchData);

                  }

                  //adding adapter to recyclerview
                  mySearchAdapter = new SearchAdapter(SearchScreen.this, mSearchList);
                  mRecyclerView.setAdapter(mySearchAdapter);
                  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                      mySearchAdapter.showshimmer = false;

                      mySearchAdapter.notifyDataSetChanged();
                    }
                  }, 1500);
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

                CommonMethod.showToast(SearchScreen.this, "Please Check your Internet");

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
    RequestQueue requestQueue = Volley.newRequestQueue(SearchScreen.this);

     //Adding the StringRequest object into requestQueue.
    requestQueue.add(stringRequest);

  }

  @Override
  public void onClick(View view) {

    switch (view.getId()) {

      case R.id.backbutton:

        this.finish();

        break;


        case R.id.filter:

            screen = "search";

          FilterDialogFragment dialogFragment = new FilterDialogFragment();

          Bundle bundle = new Bundle();
          bundle.putBoolean("notAlertDialog", true);

          dialogFragment.setArguments(bundle);

          FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
          Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
          if (prev != null) {
            ft.remove(prev);
          }
          ft.addToBackStack(null);


          dialogFragment.show(ft, "dialog");


        break;
    }
  }

  public void getExtraSearchScreenData(){

    // Creating string request with post method.
    stringRequest = new StringRequest(Request.Method.GET, Api.SEARCH+"?seeking_for="+seekingfor+"&otheronline="+online+"&fromage="+fromage+"&toage="+toage+"&premium="+premium + "&count="+extracount,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String ServerResponse) {


                try {

                  JSONArray array=new JSONArray(ServerResponse);

                  if (array != null && array.length() > 0) {

                    for (int i = array.length()-1; i>=0; i--) {

                      JSONObject json = array.getJSONObject(i);

                      String userid = json.getString("user");
                      String first_name = json.getString("first_name");
                      String city = json.getString("city");
                      String profile = json.getString("profile_img");
                      String status = json.getString("online");
                      String premium = json.getString("is_premium");

                      if(profile.equals("null")){


                      }else {

                        JSONObject prof = json.getJSONObject("profile_img");

                        profilepic = prof.getString("img");

                      }


                      mSearchData = new SearchData(userid,first_name, city,status,
                              premium,profilepic);
                      mSearchList.add(mSearchData);

                    }

                    //adding adapter to recyclerview
                    mySearchAdapter = new SearchAdapter(SearchScreen.this, mSearchList);
                    mRecyclerView.setAdapter(mySearchAdapter);

                    new Handler().postDelayed(new Runnable() {
                      @Override
                      public void run() {

                        mySearchAdapter.showshimmer = false;

                        mySearchAdapter.notifyDataSetChanged();
                      }
                    }, 1500);
                  } else {


                  }


                } catch (JSONException e) {

                  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                      mySearchAdapter.showshimmer = false;

                      mySearchAdapter.notifyDataSetChanged();
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

                // Showing error message if something goes wrong.
                CommonMethod.showToast(SearchScreen.this, "Please Check your Internet");

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
    RequestQueue requestQueue = Volley.newRequestQueue(SearchScreen.this);

    //Adding the StringRequest object into requestQueue.
    requestQueue.add(stringRequest);
  }
}
