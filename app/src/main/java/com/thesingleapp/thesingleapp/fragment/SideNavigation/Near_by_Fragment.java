package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.LoginScreen;
import com.thesingleapp.thesingleapp.activity.OthersProfile;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Map_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.userdata.UserData;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import static android.content.Context.MODE_PRIVATE;
import static com.thesingleapp.thesingleapp.fragment.SideNavigation.Matches_Fragment.latitudevalue;
import static com.thesingleapp.thesingleapp.fragment.SideNavigation.Matches_Fragment.longitudevalue;


public class Near_by_Fragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.maps.OnMapReadyCallback {

    Context mcontext;
    public Map_model mmapData;
    private Hashtable<String, String> markers;
    private String image,age;

    private SharedPreferences nearbyPreferences;
    private SharedPreferences.Editor nearbyPrefsEditor;
    private String seekingfor,fromage,toage;

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View myContentsView;
       JSONObject json;
    MyInfoWindowAdapter(){
        myContentsView = getLayoutInflater().inflate(R.layout.custom_markerinfo, null);
    }


    @Override
    public View getInfoContents(Marker marker) {


        if (marker.getTitle().equals("null")){

            TextView tvTitle = myContentsView.findViewById(R.id.title);
            tvTitle.setText("");

        }else {

            String[] splitedmap = marker.getTitle().split("id");

            String name = splitedmap[0];

            TextView tvTitle = myContentsView.findViewById(R.id.title);
            tvTitle.setText(name);
        }

// comment for userid to next screen
        if (marker.getSnippet().equals("null")||marker.getSnippet().equals("")){

            TextView tvSnippet = myContentsView.findViewById(R.id.snippet);
            tvSnippet.setText("");

        }else {

            String[] splitedmap = marker.getSnippet().split("img");

            if(splitedmap.length == 0 ) {

                TextView tvSnippet = myContentsView.findViewById(R.id.snippet);
                tvSnippet.setText("");

            }else {
                age = splitedmap[0];
                image = splitedmap[1];

                TextView tvSnippet = myContentsView.findViewById(R.id.snippet);

                if (age.equals("")){

                    tvSnippet.setText("");

                }else {

                    tvSnippet.setText("Age " + age);

                }


                ImageView markerpic = myContentsView.findViewById(R.id.ivImage);
                Glide.with(getActivity())
                        .load(Api.IP+image)//+markers.get("markerimage")
                        .placeholder(R.drawable.single_image_dummy)
                        .dontAnimate()
                        .centerCrop().into(markerpic);


            }
        }

        return myContentsView;
    }



    @Override
    public View getInfoWindow(Marker marker) {
        // TODO Auto-generated method stub


        return null;
    }


}
    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    public static final int REQUEST_LOCATION_CODE = 99;
    public static Double currentlatitude, currentlongitude;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;


    private StringRequest stringRequest;

    // Near By screen timer
    private static int TIME_OUT = 4000;

    String token, userid, profilepic, latitude, longitude, first_name, dob,otheruserid,user_tracking;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View nearby = inflater.inflate(R.layout.fragment_nearby, container, false);


        //To check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(getActivity());

        getGPSInfo();

        setUpMap();

        //get User data
        UserDataModel data = UserDataModel.getInstance();
        token = data.getToken();
        userid = data.getId();

        // to pass image in info window
        markers = new Hashtable<String, String>();

        // Shared prefference intiated
        nearbyPreferences = getActivity().getSharedPreferences("filterdata", MODE_PRIVATE);
        nearbyPrefsEditor = nearbyPreferences.edit();

        String seekingfor = nearbyPreferences.getString("seekingfor", "");
        String online = nearbyPreferences.getString("online", "");
        String fromage = nearbyPreferences.getString("fromage", "");
        String toage = nearbyPreferences.getString("toage", "");
        String premium = nearbyPreferences.getString("premium", "");

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

        

        return nearby;
    }

    private void setUpMap() {
        if (mMap == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);



            if (mMap != null) {


            }

        }
    }

    private void getGPSInfo() {
        Criteria criteria = new Criteria();
        String provider;
        Location location;
        LocationManager locationmanager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = locationmanager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation


                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationmanager.getLastKnownLocation(provider);

        } else {

            showGPSDisabledAlertToUser();

        }
    }


    //if Gps is off
    private void showGPSDisabledAlertToUser() {

        CommonMethod.showAlertDialog(getActivity(), "", "GPS is disabled in your device. Would you like to enable it?", "Goto Settings Page To Enable GPS", "Ok", new CommonMethod.DialogClickListener() {
                    @Override
                    public void dialogOkBtnClicked(String value) {

                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                    @Override
                    public void dialogNoBtnClicked(String value) {

                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                }
        );




        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setMessage(
                        "GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });
    }


    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(26.2355837,26.6592427) , 11.0f) );
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                // TODO Auto-generated method stub

                String[] splitedmapdata = arg0.getTitle().split("id");

               String mapuseridmarker = splitedmapdata[1];

               if (mapuseridmarker.equals(userid)){


               }else {

                   startActivity(new Intent(getActivity(), OthersProfile.class).putExtra("userid", mapuseridmarker));

               }

            }
        });


    }

    @Override
    public void onResume() {

        buildGoogleApiClient();

        super.onResume();
    }

    @Override
    public void onConnected(Bundle bundle) {
   mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500000);
        mLocationRequest.setFastestInterval(500000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //stop location updates
            if (mGoogleApiClient.isConnected()) {
                   //coment to check current location
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            }else {

                buildGoogleApiClient();

            }
        }else{

            CommonMethod.showToast(getActivity(), "Please wait to get Your Location");

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        currentlatitude = location.getLatitude();
        currentlongitude = location.getLatitude();

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Your Location id"+userid);
        markerOptions.snippet("img"+ UserData.MAPSNIPPETIMAGE);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_marker));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 8));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(13)                    // Sets the zoom
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //get server data from
        getlatlng();

        //stop location updates
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } else {
            buildGoogleApiClient();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //to get google api client connect
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();


    }

    //to get location permission
    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else

            setUpMap();

        return true;
    }



    public void getlatlng(){

        //Show Progress Dailog
        CommonMethod.showpDialog(getActivity(),"Getting your Matches Profile");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.GET, Api.PROFILEVIEW+"latitude="+latitudevalue+"&longitude="+longitudevalue+"&seeking_for="+seekingfor+"&fromage="+fromage+"&toage="+toage+"&screen=N",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Show Progress Dailog
                        CommonMethod.hidepDialog();


                        try {

                            JSONObject jsondata = new JSONObject(ServerResponse);

                            JSONArray array = jsondata.getJSONArray("data");


                            for (int i = array.length()-1; i>=0; i--) {

                                JSONObject json = array.getJSONObject(i);

                                otheruserid = json.getString("user");
                                first_name = json.getString("first_name");
                                dob = json.getString("age");
                                latitude = json.getString("latitude");
                                longitude = json.getString("longitude");
                                user_tracking = json.getString("user_tracking");

                                String profile = json.getString("profile_img");

                                if (user_tracking.equals("true")) {

                                    if (first_name.equals("null")) {
                                        first_name = "";
                                    }

                                    if (dob.equals("null")) {
                                        dob = "";
                                    }

                                    if (profile.equals("null")) {

                                        profilepic = "";

                                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(latLng);
                                        markerOptions.title(first_name + "id" + otheruserid);
                                        markerOptions.snippet(dob + "img" + "");
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_marker));
                                        markers.put("markerimage", "");
                                        mCurrLocationMarker = mMap.addMarker(markerOptions);

                                    } else {

                                        JSONObject prof = json.getJSONObject("profile_img");

                                        profilepic = prof.getString("img");

                                        //Place current location marker
                                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(latLng);
                                        markerOptions.title(first_name + "id" + otheruserid);
                                        markerOptions.snippet(dob + "img" + profilepic);
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_marker));
                                        markers.put("markerimage", profilepic);
                                        mCurrLocationMarker = mMap.addMarker(markerOptions);

                                    }

                                mmapData = new Map_model();
                                mmapData.setUserid(otheruserid);
                                mmapData.setName(first_name);
                                mmapData.setProfilepic(profilepic);
                                mmapData.setAge(dob);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Show Progress Dailog
                        CommonMethod.hidepDialog();

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

        //Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);


    }

    public void checkconnectionGps(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            //permission for gps
            getGPSInfo();


        } else {

            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }
    }

}
