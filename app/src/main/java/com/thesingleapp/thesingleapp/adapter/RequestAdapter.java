package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.Request_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {
    Request_model request_model;
    private Context context;
    private List<Request_model> blockList;
    public  boolean showshimmer = true;
    private StringRequest stringRequest;

    public  String token,userid,username,userpremiumtype,id,UNFAPI="";

    public RequestAdapter(Context context, List<Request_model> messageList) {
        this.blockList = messageList;
        this.context = context;

    }

    @Override
    public RequestAdapter.RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_list, parent, false);
        RequestAdapter.RequestHolder viewHolder = new RequestAdapter.RequestHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RequestAdapter.RequestHolder holder, int position) {
        request_model = blockList.get(position);

        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else {

            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.firstname.setBackground(null);
            holder.city.setBackground(null);
            holder.profilepic.setBackground(null);

            Resources resource= context.getResources();
            holder.firstname.setText(request_model.getFirstName());
            holder.city.setText(request_model.getCity());
            String premium = request_model.getPremium();


            holder.profilepic.setImageResource(R.drawable.single_image_dummy);

            holder.premium.setImageResource(R.drawable.star_gold_icon);

            Glide.with(context)
                    .load(Api.IP +"media/"+request_model.getProfilePic())
                    .placeholder(R.drawable.single_image_dummy)
                    .dontAnimate()
                    .centerCrop().into(holder.profilepic);

            // user premium status
            if (premium.equals("true"))
            {
                holder.premium.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.premium.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {

        return showshimmer ?  blockList.size() :  blockList.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView firstname,city;
        public ImageView profilepic,premium;
        ShimmerFrameLayout sh1;
        public Button accept,reject;


        public RequestHolder(View itemView) {
            super(itemView);

            city = itemView.findViewById(R.id.tvcity);
            firstname = itemView.findViewById(R.id.tvfirstname);
            profilepic = itemView.findViewById(R.id.ivImage);
            premium = itemView.findViewById(R.id.starImage);
            sh1= itemView.findViewById(R.id.parentShimmerLayout);
            accept=itemView.findViewById(R.id.accept);
            reject=itemView.findViewById(R.id.reject);

            accept.setOnClickListener(this);
            reject.setOnClickListener(this);

            UserDataModel data = UserDataModel.getInstance();

            token = data.getToken();
            userid = data.getId();
            username = data.getUsername();
            userpremiumtype = data.getPremium();
        }

        @Override
        public void onClick(View v) {

            if ((v == accept)){

                Request_model requestModel_s = blockList.get(getAdapterPosition());

                acceptedFriends(requestModel_s.getId(),requestModel_s.getUserid(),requestModel_s.getOtheruserid());

                blockList.remove(blockList.get(getAdapterPosition()));

                notifyDataSetChanged();
            }

            if((v == reject)){

                Request_model requestModel_ = blockList.get(getAdapterPosition());

                rejectRequest(requestModel_.getId());

                blockList.remove(blockList.get(getAdapterPosition()));

                notifyDataSetChanged();
            }

        }
    }



    private void acceptedFriends(String id,String userid,String otheruserid) {


        stringRequest = new StringRequest(Request.Method.PUT, Api.FRIENDS+id+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                        Toast.makeText(context, "Now Your are Friends ", Toast.LENGTH_SHORT).show();

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(context, "its not working ", Toast.LENGTH_SHORT).show();



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

                params.put("accepted", "true");
                params.put("user", otheruserid);
                params.put("other",userid);


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
        RequestQueue requestQueue = Volley.newRequestQueue(context);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

    public void rejectRequest(String id){


        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.DELETE, Api.FRIENDS+id+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        Toast.makeText(context, "Rejected the Request", Toast.LENGTH_SHORT).show();
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

                params.put("Authorization", "Token "+token);
                return params;
            }

        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(context);

//      Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

}
