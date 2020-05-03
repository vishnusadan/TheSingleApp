package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.Comments_Screen;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.Galley_model;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileViewHolder > {

    private Context mContext;
    private List<Galley_model> mMatchList;
    public  boolean showshimmer = true;

    public ProfileAdapter(Context mContext, List< Galley_model > mFlowerList) {
        this.mContext = mContext;
        this.mMatchList = mFlowerList;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_row_item, parent, false);
        return new ProfileViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final ProfileViewHolder holder, int position) {
        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else {
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.sh1.setBackground(null);


            Glide.with(mContext)
                    .load(Api.IP+mMatchList.get(position).getProfilePic())
                    .placeholder(R.drawable.single_image_dummy)
                    .centerCrop().into(holder.mImage);


            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mIntent = new Intent(mContext, Comments_Screen.class);
                    mContext.startActivity(mIntent);
                }
            });

            holder.tvcomments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mIntent = new Intent(mContext, Comments_Screen.class);
                    mContext.startActivity(mIntent);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return mMatchList.size();
    }
}

class ProfileViewHolder extends RecyclerView.ViewHolder {

    ImageView mImage,comment,heart;
    TextView mTitle,tvcomments;
    CardView mCardView;
    ShimmerFrameLayout sh1;

    ProfileViewHolder(View itemView) {
        super(itemView);

        mImage = itemView.findViewById(R.id.ivImage);
        comment = itemView.findViewById(R.id.comment);
        heart = itemView.findViewById(R.id.heart);
        mTitle = itemView.findViewById(R.id.tvTitle);
        tvcomments = itemView.findViewById(R.id.tvcity);
        mCardView = itemView.findViewById(R.id.cardview);
        sh1= itemView.findViewById(R.id.parentShimmerLayout);

    }
}