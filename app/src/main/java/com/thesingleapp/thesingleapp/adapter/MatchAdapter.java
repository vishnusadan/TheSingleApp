package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.OthersProfile;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.MatchData;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter< MatchViewHolder > {

    private Context mContext;
    private List<MatchData> mMatchList;
    public  boolean showshimmer = true;
    public String Imageurl = "null";

    public MatchAdapter(Context mContext, List< MatchData > mFlowerList) {
        this.mContext = mContext;
        this.mMatchList = mFlowerList;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.matchview_row_item, parent, false);
        return new MatchViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final MatchViewHolder holder, int position) {

        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else {
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.mImage.setBackground(null);
            holder.mTitle.setBackground(null);
            holder.tvcity.setBackground(null);


            holder.mImage.setImageResource(R.drawable.single_image_dummy);
            holder.premium.setImageResource(R.drawable.star_gold_icon);
            holder.statusonline.setImageResource(R.drawable.inactive_home);
            holder.imageViewbck.setImageResource(R.drawable.gradient_bt);

            holder.mTitle.setText(mMatchList.get(position).getFirstName());

            if(mMatchList.get(position).getPremium().equals("false")){

                holder.premium.setVisibility(View.INVISIBLE);

            }else {

                holder.premium.setVisibility(View.VISIBLE);
            }

            if(mMatchList.get(position).getCity().equals("null")){

                holder.tvcity.setText("");

            }else {

                holder.tvcity.setText(mMatchList.get(position).getCity());

            }
            // comment after update api online status
            if(mMatchList.get(position).getStatus().equals("true")){

                holder.statusonline.setImageResource(R.drawable.active_home);

            }else {

                holder.statusonline.setImageResource(R.drawable.inactive_home);

            }


            String valueofimg = mMatchList.get(position).getProfilePic();

            if(Imageurl.equals(valueofimg)||valueofimg==null) {

                Glide.with(mContext)
                        .load("http://thesingle.zunamelt.com/static/accounts/single_image_dummy.png")
                        .placeholder(R.drawable.single_image_dummy)
                        .centerCrop().into(holder.mImage);

            }else{

                Glide.with(mContext)
                        .load(Api.IP + mMatchList.get(position).getProfilePic())
                        .placeholder(R.drawable.single_image_dummy)
                        .centerCrop().into(holder.mImage);

                Imageurl = mMatchList.get(position).getProfilePic();

            }



            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent mIntent = new Intent(mContext, OthersProfile.class);
                    mIntent.putExtra("userid", mMatchList.get(holder.getAdapterPosition()).getUserid());
                    mContext.startActivity(mIntent);

                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return showshimmer ? mMatchList.size() : mMatchList.size();
    }
}

class MatchViewHolder extends RecyclerView.ViewHolder {

    ImageView mImage,statusonline,premium,imageViewbck;
    TextView mTitle,tvcity;
    CardView mCardView;
    ShimmerFrameLayout sh1;
    MatchViewHolder(View itemView) {
        super(itemView);

        premium = itemView.findViewById(R.id.premium);
        mImage = itemView.findViewById(R.id.ivImage);
        statusonline = itemView.findViewById(R.id.heart);
        mTitle = itemView.findViewById(R.id.tvTitle);
        tvcity = itemView.findViewById(R.id.tvcity);
        mCardView = itemView.findViewById(R.id.cardview);
        sh1= itemView.findViewById(R.id.parentShimmerLayout);
        imageViewbck = itemView.findViewById(R.id.imageViewbck);

    }
}