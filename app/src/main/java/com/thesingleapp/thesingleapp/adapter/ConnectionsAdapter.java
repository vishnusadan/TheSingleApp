package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.OthersProfile;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.Connections_model;
import java.util.List;

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ConnectionsHolder> {

    Connections_model viewedModel;
    private Context context;
    private List<Connections_model> viewedmeModelList;
    public  boolean showshimmer = true;

    public ConnectionsAdapter(Context context, List<Connections_model> viewedmeModelList) {
        this.viewedmeModelList = viewedmeModelList;
        this.context = context;

    }

    @Override
    public ConnectionsAdapter.ConnectionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list, parent, false);
        ConnectionsAdapter.ConnectionsHolder viewHolder = new ConnectionsAdapter.ConnectionsHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ConnectionsAdapter.ConnectionsHolder holder, int position) {
        viewedModel = viewedmeModelList.get(position);

        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else
        {
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.profilepic.setBackground(null);
            holder.firstname.setBackground(null);
            holder.place.setBackground(null);
            holder.tvtime.setBackground(null);
            holder.tvdate.setBackground(null);

            holder.profilepic.setImageResource(R.drawable.single_image_dummy);
            holder.premium.setImageResource(R.drawable.star_gold_icon);
            holder.online.setImageResource(R.drawable.inactive_round);
            holder.nearby.setImageResource(R.drawable.nearby_v_icon);

            Resources resource = context.getResources();
            holder.firstname.setText(viewedModel.getFirstName());
            holder.place.setText(viewedModel.getCity());
            String time = viewedModel.getDate();
            String premium = viewedModel.getUpremium();
            String onlineoffline = viewedModel.getOnline();

            if (time.equals("null")) {

                holder.tvdate.setText("");
                holder.tvtime.setText("");

            } else {

                String[] datearray = time.split("T");

                String date = datearray[0];
                String gettime = datearray[1];

                holder.tvdate.setText(date);
                holder.tvtime.setText(gettime);

            }
            if (premium.equals("true"))
            {
                holder.premium.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.premium.setVisibility(View.INVISIBLE);
            }
            if (onlineoffline.equals("true"))
            {
                holder.online.setImageResource(R.drawable.active_icon);
            }
            else
            {
                holder.online.setImageResource(R.drawable.inactive_round);
            }


            Glide.with(context)
                    .load(Api.IP +"media/"+viewedModel.getProfilePic())
                    .placeholder(R.drawable.single_image_dummy)
                    .dontAnimate()
                    .centerCrop().into(holder.profilepic);
        }

        
    }

    @Override
    public int getItemCount() {
        return showshimmer ? viewedmeModelList.size() : viewedmeModelList.size();
    }

    public class ConnectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView firstname,place,tvtime,tvdate;
        public ImageView profilepic,premium,online,nearby;
        ShimmerFrameLayout sh1;

        public ConnectionsHolder(View itemView) {
            super(itemView);

            firstname = itemView.findViewById(R.id.tvfirstname);
            place = itemView.findViewById(R.id.tvcity);
            profilepic = itemView.findViewById(R.id.ivImage);
            tvtime = itemView.findViewById(R.id.tvtime);
            tvdate = itemView.findViewById(R.id.tvdate);
            premium = itemView.findViewById(R.id.starImage);
            online = itemView.findViewById(R.id.onoff);
            nearby = itemView.findViewById(R.id.place);
            sh1= itemView.findViewById(R.id.parentShimmerLayout);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if ((v == itemView)){
                Connections_model viewedmeModel_s = viewedmeModelList.get(getAdapterPosition());

                if ((v == itemView)){

                    Intent intent = new Intent(context, OthersProfile.class);

                    intent.putExtra("userid", viewedmeModel_s.getUserid());

                    context.startActivity(intent);
                }

            }

        }
    }
}
