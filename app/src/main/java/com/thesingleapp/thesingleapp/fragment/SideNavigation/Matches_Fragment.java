package com.thesingleapp.thesingleapp.fragment.SideNavigation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.LoginScreen;
import com.thesingleapp.thesingleapp.adapter.MatchAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.MatchData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static android.content.Context.MODE_PRIVATE;
import static com.thesingleapp.thesingleapp.apilist.Api.USER;

public class Matches_Fragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    RecyclerView mRecyclerView;
    List<MatchData > mMatchList;
    MatchData mMatchData;
    SwipeRefreshLayout swipeRefreshLayout;

    //Matches save in share preference
    private SharedPreferences matchesPreferences;
    private SharedPreferences.Editor matchesPrefsEditor;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    private MatchAdapter myMatchAdapter;
    // Creating Progress dialog.
    ProgressDialog progressDialog;

    private StringRequest stringRequest;

    ImageView logo;

    public int extracount = 0;
    public static int sum;

    String token,userid,profilepic;

    public static String city="",latitudevalue="",longitudevalue="", screen="";

    private String seekingfor,online,fromage,toage,premium;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
        }

        View matches =inflater.inflate(R.layout.fragment_matches,container,false) ;

        // init SwipeRefreshLayout and ListView
        swipeRefreshLayout = matches.findViewById(R.id.simpleSwipeRefreshLayout);
        logo = matches.findViewById(R.id.applogo);
        mRecyclerView = matches.findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

        //progress daialog
        progressDialog = new ProgressDialog(getActivity());

        mMatchList = new ArrayList<>();

        // clear array list
        if(mMatchList.isEmpty()){

        }else {
            mMatchList.clear();
        }

        // Shared prefference intiated
        matchesPreferences = getActivity().getSharedPreferences("filterdata", MODE_PRIVATE);
        matchesPrefsEditor = matchesPreferences.edit();

        seekingfor = matchesPreferences.getString("seekingfor", "");
        online = matchesPreferences.getString("online", "");
        fromage = matchesPreferences.getString("fromage", "");
        toage = matchesPreferences.getString("toage", "");
        premium = matchesPreferences.getString("premium", "");

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
        if (online.equals("B"))
        {
            online="";
        }



        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //Checking Permission for location
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        // implement setOnRefreshListener event on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // cancel the Visual indication of a refresh
                swipeRefreshLayout.setRefreshing(false);

                if (mMatchList.isEmpty()){

                }else {
                    mMatchList.clear();

                    extracount = 0;
                    //messages data

                    getMatchesData();
                }
            }
        });



        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;

                    getExtraMatchesData();

                }
            }
        });
        return matches;
    }

    public void getMatchesData()
    {

        mMatchList.clear();
        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PROFILEVIEW + "online=" + true + "&latitude=" + latitudevalue + "&longitude=" + longitudevalue + "&city=" + city + "&seeking_for=" + seekingfor + "&otheronline=" + online + "&fromage=" + fromage + "&toage=" + toage + "&premium=" + premium + "&screen=M" + "&count=0",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);
                            JSONArray data = jsondata.getJSONArray("data");


                            if (data != null && data.length() > 0) {


                            } else {

                                logo.setVisibility(View.VISIBLE);
                                CommonMethod.showToast(getActivity(), "You Dont Have Any Matches,Please Use Filter to get Matches");

                            }


                            for (int i = data.length() - 1; i >= 0; i--) {

                                JSONObject json = data.getJSONObject(i);

                                String userid = json.getString("user");
                                String first_name = json.getString("first_name");
                                String city = json.getString("city");
                                String premium = json.getString("is_premium");
                                String profile = json.getString("profile_img");
                                String status = json.getString("online");


                                if (profile.equals("null")) {


                                } else {

                                    JSONObject prof = json.getJSONObject("profile_img");

                                    profilepic = prof.getString("img");

                                }

                                mMatchData = new MatchData(userid, first_name, city, status, premium, profilepic);
                                mMatchList.add(mMatchData);
                                sum=sum+1;
                                //adding adapter to recyclerview
                                myMatchAdapter = new MatchAdapter(getActivity(), mMatchList);
                                mRecyclerView.setAdapter(myMatchAdapter);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        myMatchAdapter.showshimmer = false;

                                        myMatchAdapter.notifyDataSetChanged();
                                    }
                                }, 1500);
                            }



                        } catch (JSONException e) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    myMatchAdapter.showshimmer = false;

                                    myMatchAdapter.notifyDataSetChanged();
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

                        } else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            // Showing error message if something goes wrong.
                            CommonMethod.showToast(getActivity(), "You Dont Have More Matches..");

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
                params.put("Authorization", "Token " + token);
                return params;
            }
        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }



    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }




    @Override
    public void onConnected(Bundle bundle) {

        if (city.equals("")) {

            CommonMethod.showpDialog(getActivity(), "Fetching Your Location...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CommonMethod.hidepDialog();
                    getMatchesData();
                }
            }, 4000);//4 second

            startLocationUpdates();

        }else {

            getMatchesData();

        }

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    private void updateUI() {
        if (null != mCurrentLocation) {
            latitudevalue = String.valueOf(mCurrentLocation.getLatitude());
            longitudevalue = String.valueOf(mCurrentLocation.getLongitude());
            try {

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {

                    Address address = addresses.get(0);
                    String locality = address.getLocality();

                    city=locality;

                    //hide Progress Dailog

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            CommonMethod.showToast(getActivity(),"Not able to get your location");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
//        if(sum>30){
//            USER="";
//        }
    }

    public void getExtraMatchesData(){

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PROFILEVIEW + "&online=" + true + "&latitude=" + latitudevalue + "&longitude=" + longitudevalue + "&city=" + city + "&seeking_for=" + seekingfor + "&otheronline=" + online + "&fromage=" + fromage + "&toage=" + toage + "&premium=" + premium + "&screen=M" + "&count="+extracount,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);

                            JSONArray data = jsondata.getJSONArray("data");


                            if (data != null && data.length() > 0) {



                                for (int i = data.length() - 1; i >= 0; i--) {

                                    JSONObject json = data.getJSONObject(i);

                                    String userid = json.getString("user");
                                    String first_name = json.getString("first_name");
                                    String city = json.getString("city");
                                    String premium = json.getString("is_premium");
                                    String profile = json.getString("profile_img");
                                    String status = json.getString("online");


                                    if (profile.equals("null")) {


                                    } else {

                                        JSONObject prof = json.getJSONObject("profile_img");

                                        profilepic = prof.getString("img");

                                    }

                                    mMatchData = new MatchData(userid, first_name, city, status, premium, profilepic);
                                    mMatchList.add(mMatchData);

                                    //adding adapter to recyclerview
                                    myMatchAdapter = new MatchAdapter(getActivity(), mMatchList);
                                    mRecyclerView.setAdapter(myMatchAdapter);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            myMatchAdapter.showshimmer = false;

                                            myMatchAdapter.notifyDataSetChanged();
                                        }
                                    }, 1500);
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

                            CommonMethod.showToast(getActivity(), "Session Expired");

                        } else {

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    100,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            // Showing error message if something goes wrong.
                            CommonMethod.showToast(getActivity(), "You Dont Have More Matches");

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
                params.put("Authorization", "Token " + token);
                return params;
            }
        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }
}
