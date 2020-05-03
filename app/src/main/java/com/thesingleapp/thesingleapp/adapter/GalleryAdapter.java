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

public class  GalleryAdapter extends RecyclerView.Adapter< GalleryViewHolder > {

   private Context mContext;
   private List<Galley_model> mMatchList;
    public  boolean showshimmer = true;

   public GalleryAdapter(Context mContext, List< Galley_model > mFlowerList) {
        this.mContext = mContext;
        this.mMatchList = mFlowerList;
        }

        @Override
        public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_row_item, parent, false);
        return new GalleryViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(final GalleryViewHolder holder,final int position) {

            if(showshimmer)
            {
                holder.sh1.startShimmer();
            }
            else {
                holder.sh1.stopShimmer();
                holder.sh1.setShimmer(null);
                holder.mTitle.setBackground(null);
                holder.tvcomments.setBackground(null);
                holder.mImage.setBackground(null);


                holder.mTitle.setText(mMatchList.get(position).getLikescount()+" Likes");

                holder.tvcomments.setText(mMatchList.get(position).getCommentscount()+" Comments");
                holder.mImage.setImageResource(R.drawable.single_image_dummy);
                holder.heart.setImageResource(R.drawable.fav_pink_icon);
                holder.comment.setImageResource(R.drawable.comment_pink);

                if (mMatchList.get(position).getProfilePic().contains(Api.IP)) {

                    Glide.with(mContext)
                            .load(mMatchList.get(position).getProfilePic())
                            .placeholder(R.drawable.single_image_dummy)
                            .centerCrop().into(holder.mImage);
                }else {

                    Glide.with(mContext)
                            .load(Api.IP+mMatchList.get(position).getProfilePic())
                            .placeholder(R.drawable.single_image_dummy)
                            .centerCrop().into(holder.mImage);

                }



                holder.mImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(mContext, Comments_Screen.class);
                        mIntent.putExtra("imageid",mMatchList.get(position).getId());
                        mIntent.putExtra("image",mMatchList.get(position).getProfilePic());
                        mIntent.putExtra("likescount",mMatchList.get(position).getLikescount());
                        mIntent.putExtra("commentscount",mMatchList.get(position).getCommentscount());
                        mIntent.putExtra("otheruserid",mMatchList.get(position).getOtheruserid());
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(mIntent);
                    }
                });
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(mContext, Comments_Screen.class);
                        mIntent.putExtra("imageid",mMatchList.get(position).getId());
                        mIntent.putExtra("image",mMatchList.get(position).getProfilePic());
                        mIntent.putExtra("likescount",mMatchList.get(position).getLikescount());
                        mIntent.putExtra("commentscount",mMatchList.get(position).getCommentscount());
                        mIntent.putExtra("otheruserid",mMatchList.get(position).getOtheruserid());
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(mIntent);
                    }
                });

                holder.heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(mContext, Comments_Screen.class);
                        mIntent.putExtra("imageid",mMatchList.get(position).getId());
                        mIntent.putExtra("image",mMatchList.get(position).getProfilePic());
                        mIntent.putExtra("likescount",mMatchList.get(position).getLikescount());
                        mIntent.putExtra("commentscount",mMatchList.get(position).getCommentscount());
                        mIntent.putExtra("otheruserid",mMatchList.get(position).getOtheruserid());
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(mIntent);
                    }
                });


                holder.tvcomments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent mIntent = new Intent(mContext, Comments_Screen.class);
                        mIntent.putExtra("imageid",mMatchList.get(position).getId());
                        mIntent.putExtra("image",mMatchList.get(position).getProfilePic());
                        mIntent.putExtra("likescount",mMatchList.get(position).getLikescount());
                        mIntent.putExtra("commentscount",mMatchList.get(position).getCommentscount());
                        mIntent.putExtra("otheruserid",mMatchList.get(position).getOtheruserid());
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    class GalleryViewHolder extends RecyclerView.ViewHolder {

    ImageView mImage,comment,heart;
    TextView mTitle,tvcomments;
    CardView mCardView;
        ShimmerFrameLayout sh1;
        GalleryViewHolder(View itemView) {
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