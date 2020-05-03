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
import com.thesingleapp.thesingleapp.model.Notification_model;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ConnectionsHolder> {

    Notification_model notificationModel;
    private Context context;
    private List<Notification_model> notificationList;
    public  boolean showshimmer = true;


    public NotificationAdapter(Context context, List<Notification_model> notificationList) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @Override
    public NotificationAdapter.ConnectionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list, parent, false);
        NotificationAdapter.ConnectionsHolder viewHolder = new NotificationAdapter.ConnectionsHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(NotificationAdapter.ConnectionsHolder holder, int position) {
        notificationModel = notificationList.get(position);


        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else
        {
            //Hide shimmer
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.date.setBackground(null);
            holder.time.setBackground(null);
            holder.profilepic.setBackground(null);
            holder.place.setBackground(null);


            holder.profilepic.setImageResource(R.drawable.single_image_dummy);
            holder.upremium.setImageResource(R.drawable.star_gold_icon);
            holder.uonline.setImageResource(R.drawable.inactive_round);

            Resources resource= context.getResources();
            holder.place.setText(notificationModel.getMsg());
            String upremium = notificationModel.getUpremium();
            String uonline = notificationModel.getUonline();
            String date = notificationModel.getdate();

            if (date.equals("null")) {

                holder.date.setText("");
                holder.time.setText("");

            } else {

                String[] datearray = date.split("T");

                String date1 = datearray[0];
                String gettime = datearray[1];

                holder.date.setText(date1);
                holder.time.setText(gettime);

            }
            if (upremium.equals("true"))
            {
                holder.upremium.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.upremium.setVisibility(View.INVISIBLE);
            }
            if (uonline.equals("true"))
            {
                holder.uonline.setImageResource(R.drawable.active_icon);
            }
            else
            {
                holder.uonline.setImageResource(R.drawable.inactive_round);
            }

            Glide.with(context)
                    .load(Api.IP +"media/"+notificationModel.getProfilePic())
                    .placeholder(R.drawable.single_image_dummy)
                    .dontAnimate()
                    .centerCrop().into(holder.profilepic);
        }


    }

    @Override
    public int getItemCount() {
        return showshimmer ? notificationList.size() : notificationList.size();
    }

    public class ConnectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView firstname,place,date,time;
        public ImageView profilepic,upremium,uonline;
        ShimmerFrameLayout sh1;

        public ConnectionsHolder(View itemView) {
            super(itemView);

            place = itemView.findViewById(R.id.tvcity);
            profilepic = itemView.findViewById(R.id.ivImage);
            upremium = itemView.findViewById(R.id.starImage);
            uonline = itemView.findViewById(R.id.onoff);
            sh1= itemView.findViewById(R.id.parentShimmerLayout);
            date = itemView.findViewById(R.id.tvdate);
            time = itemView.findViewById(R.id.tvtime);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            if ((v == itemView)){
                Notification_model notificationModel_s = notificationList.get(getAdapterPosition());

                if ((v == itemView)){

                    Intent intent = new Intent(context, OthersProfile.class);

                    intent.putExtra("userid", notificationModel_s.getUserid());

                    context.startActivity(intent);
                }

            }

        }
    }
}
