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
import com.thesingleapp.thesingleapp.model.Blocked_model;
import java.util.List;

public class BlockedAdapter extends RecyclerView.Adapter<BlockedAdapter.MessageHolder> {

    Blocked_model blockModel;
    private Context context;
    private List<Blocked_model> blockList;
    public  boolean showshimmer = true;

    public BlockedAdapter(Context context, List<Blocked_model> messageList) {
        this.blockList = messageList;
        this.context = context;
    }

    @Override
    public BlockedAdapter.MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blocked_list, parent, false);
        BlockedAdapter.MessageHolder viewHolder = new BlockedAdapter.MessageHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(BlockedAdapter.MessageHolder holder, int position) {
        blockModel = blockList.get(position);
        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else {
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.firstname.setBackground(null);
            holder.profilepic.setBackground(null);

            Resources resource= context.getResources();
            holder.firstname.setText(blockModel.getFirstName());

            holder.profilepic.setImageResource(R.drawable.single_image_dummy);
            holder.online.setImageResource(R.drawable.blocked_red);

            Glide.with(context)
                    .load(Api.IP +"media/"+blockModel.getProfilePic())
                    .placeholder(R.drawable.single_image_dummy)
                    .dontAnimate()
                    .centerCrop().into(holder.profilepic);
        }



    }

    @Override
    public int getItemCount() {

        return showshimmer ? blockList.size() : blockList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView firstname;
        public ImageView profilepic,online;
        ShimmerFrameLayout sh1;

        public MessageHolder(View itemView) {
            super(itemView);

            firstname = itemView.findViewById(R.id.tvfirstname);
            profilepic = itemView.findViewById(R.id.ivImage);
            online = itemView.findViewById(R.id.online);
            sh1= itemView.findViewById(R.id.parentShimmerLayout);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            if ((v == itemView)){
                Blocked_model viewedmeModel_s = blockList.get(getAdapterPosition());

                if ((v == itemView)){

                    Intent intent = new Intent(context, OthersProfile.class);

                    intent.putExtra("userid", viewedmeModel_s.getUserid());

                    context.startActivity(intent);
                }


            }

        }
    }
}
