package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.LoginScreen;
import com.thesingleapp.thesingleapp.adapter.GalleryAdapter;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Galley_model;
import com.thesingleapp.thesingleapp.network.ConnectivityReceiver;
import com.thesingleapp.thesingleapp.service.VolleyMultipartRequest;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.app.Activity.RESULT_OK;

public class Gallery_Fragment extends Fragment implements View.OnClickListener {

    RecyclerView mRecyclerView;
    List<Galley_model> mGalleryList;
    Galley_model mGalley_model;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    byte[] byteArray;

    private GalleryAdapter galleryAdapter;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //button
    private Button upload;

    Uri outputFileUri;

    public int extracount = 0;

    private static final int PICK_FROM_CAMERA = 1;

    private StringRequest stringRequest;

    BottomSheetDialog dialog;

    String token,userid;

    public ImageView profilepic,logo;

    public ImageButton addimage;

    private static String profileadd = "no";

    View dialogView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View gallery =inflater.inflate(R.layout.fragment_gallery,container,false);


        logo = gallery.findViewById(R.id.applogo);
        addimage = gallery.findViewById(R.id.floatingbtn);
        mRecyclerView = gallery.findViewById(R.id.recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        addimage.setOnClickListener(this);


        // Creating Volley newRequestQueue.
        requestQueue = Volley.newRequestQueue(getActivity());


        mGalleryList = new ArrayList<>();

        //get User data
        UserDataModel data = UserDataModel.getInstance();

        token = data.getToken();
        userid = data.getId();

        // Getting images
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            getGalleryData();

        } else {


            CommonMethod.showToast(getActivity(), "Please Check the internet connection");

        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)&& newState==RecyclerView.SCROLL_STATE_IDLE) {

                    extracount = extracount+1;
                    mGalleryList.clear();
                    getExtraGalleryData();

                }
            }
        });
        return gallery;
    }


    @Override
    public void onResume() {


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
        stringRequest = new StringRequest(Request.Method.GET, Api.COMMENT+"?count=0",
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

                                mGalley_model = new Galley_model(userid,id,profilepic,likecount, commentcount);
                                mGalleryList.add(mGalley_model);

                                //adding adapter to recyclerview
                                 galleryAdapter = new GalleryAdapter(getActivity(), mGalleryList);
                                mRecyclerView.setAdapter(galleryAdapter);
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

                        if (volleyError instanceof AuthFailureError) {

                            startActivity(new Intent(getActivity(), LoginScreen.class));
                            getActivity().finish();


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


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.button2:

                if(profileadd.equals("yes")) {
                    upload.setEnabled(false);
                    uploadMultipart(bitmap);

                    //canceling the dailoge
                    dialog.cancel();


                }else {

                    Toast.makeText(getActivity(), "Please Select Image to Upload ", Toast.LENGTH_SHORT).show();

                }

                break;


            case R.id.floatingbtn:

                profileadd ="no";

                bottomSheet();

                break;



            case R.id.imageView9:

                selectImage();

                break;
        }


    }

    public void bottomSheet(){

        dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_imageupload, null);

        upload = dialogView.findViewById(R.id.button2);
        profilepic = dialogView.findViewById(R.id.imageView9);

        upload.setOnClickListener(this);
        profilepic.setOnClickListener(this);

        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);
        dialog.show();
    }

    //image from gallery

    private void selectImage() {


        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.DialogeTheme);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))
                {
                    //to know user click on image view or not


                    // Checking Permission for Android M and above
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_CAMERA);
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    {


                        Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        Uri uri = FileProvider.getUriForFile(getContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
                        m_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(m_intent, 1);
//
                    } else
                        {
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                        outputFileUri = Uri.fromFile(file);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(captureIntent, 1);
                    }


                }

                else if (options[item].equals("Choose from Gallery"))

                {
                    //to know user click on image view or not

                    Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                }

                else if (options[item].equals("Cancel")) {


                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }



    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            if (requestCode == 1) {

                profileadd="yes";

                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                Uri uri = FileProvider.getUriForFile(getContext(), this.getActivity().getApplicationContext().getPackageName() + ".provider", file);
                Bitmap bitmap= null;
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                profilepic.setImageBitmap(bitmap);

            } else if (requestCode == 2) {


                profileadd="yes";

                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);

                c.close();

                bitmap = (BitmapFactory.decodeFile(picturePath));

                profilepic.setImageBitmap(bitmap);

            }

        }
    }

    /*
     * This is the method responsible for image upload
     * We need the full image path and the name for the image in this method
     * */

    public void uploadMultipart(final Bitmap bitmap) {

        if(profileadd.equals("yes")){

            // For get Drawable from Image
            Drawable d = profilepic.getDrawable();

            Bitmap bitmapOrg = ((BitmapDrawable)d).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byteArray = stream.toByteArray();

        }else {

           //GlideBitmapDrawable  BitmapDrawable
            Bitmap bmp = ((GlideBitmapDrawable)profilepic.getDrawable().getCurrent()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
            byteArray = stream.toByteArray();

        }

        CommonMethod.showpDialog(getActivity(),"Updating...");

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Api.COMMENT,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        CommonMethod.hidepDialog();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            //canceling the dailoge
                            dialog.cancel();


                            if (logo.getVisibility() == View.VISIBLE) {

                                logo.setVisibility(View.INVISIBLE);
                            } else {

                            }

                            //to refresh
                            onResume();

                            upload.setEnabled(true);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        CommonMethod.hidepDialog();

                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user", userid);
                params.put("is_profile", "false");

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();

                params.put("img", new DataPart(imagename + ".jpeg", byteArray));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        //adding the request to volley
        Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);

    }

public void getExtraGalleryData(){

    // Creating string request with post method.
    stringRequest = new StringRequest(Request.Method.GET, Api.COMMENT+"?"+extracount,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String ServerResponse) {

                    try {

                        JSONArray imagedata = new JSONArray(ServerResponse);

                        if(imagedata!=null && imagedata.length()>0) {
                            for (int i = imagedata.length()-1; i>=0; i--) {

                                JSONObject image = imagedata.getJSONObject(i);

                                String id = image.getString("id");
                                String profilepic = image.getString("img");
                                JSONArray likescountv = image.getJSONArray("likeimage");
                                JSONArray commentcountv = image.getJSONArray("commentimage");

                                String likecount = String.valueOf(likescountv.length());
                                String commentcount = String.valueOf(commentcountv.length());

                                mGalley_model = new Galley_model(userid,id,profilepic,likecount, commentcount);
                                mGalleryList.add(mGalley_model);
                                galleryAdapter = new GalleryAdapter(getActivity(), mGalleryList);
                                mRecyclerView.setAdapter(galleryAdapter);
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


                    if (volleyError instanceof AuthFailureError) {

                        startActivity(new Intent(getActivity(), LoginScreen.class));
                        getActivity().finish();


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

    // Adding the StringRequest object into requestQueue.
    requestQueue.add(stringRequest);
}


}

