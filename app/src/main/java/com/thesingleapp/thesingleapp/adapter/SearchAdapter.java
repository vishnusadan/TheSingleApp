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
import com.thesingleapp.thesingleapp.activity.OthersProfile;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.SearchData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

  private Context mContext;
  private List<SearchData> mSearchList;
  private ArrayList<SearchData> arraylist;
  public  boolean showshimmer = true;
  public String Imageurl = "";

  public SearchAdapter(Context mContext, List<SearchData> mSearchList) {
    this.mContext = mContext;
    this.mSearchList = mSearchList;
    this.arraylist = new ArrayList<SearchData>();
    this.arraylist.addAll(mSearchList);
  }

  @Override
  public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.matchview_row_item, parent, false);
    return new SearchViewHolder(mView);
  }

  @Override
  public void onBindViewHolder(final SearchViewHolder holder, int position) {


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


      holder.mTitle.setText(mSearchList.get(position).getFirstName());


      holder.premium.setImageResource(R.drawable.star_gold_icon);
      holder.statusonline.setImageResource(R.drawable.inactive_home);



      if(mSearchList.get(position).getPremium().equals("false")){

        holder.premium.setVisibility(View.INVISIBLE);

      }else {

        holder.premium.setVisibility(View.VISIBLE);

      }

      if(mSearchList.get(position).getCity().equals("null")){

        holder.tvcity.setText("");

      }else {

        holder.tvcity.setText(mSearchList.get(position).getCity());

      }
      // comment after update api online status
      if(mSearchList.get(position).getStatus().equals("true")){

        holder.statusonline.setImageResource(R.drawable.active_home);

      }else {

        holder.statusonline.setImageResource(R.drawable.inactive_home);

      }


      String valueofimg = mSearchList.get(position).getProfilePic();

      if(Imageurl.equals(valueofimg)||valueofimg==null) {

        Glide.with(mContext)
                .load("http://thesingle.zunamelt.com/static/accounts/single_image_dummy.png")
                .placeholder(R.drawable.single_image_dummy)
                .centerCrop().into(holder.mImage);


      }else {

        Glide.with(mContext)
                .load(Api.IP + mSearchList.get(position).getProfilePic())
                .placeholder(R.drawable.single_image_dummy)
                .centerCrop().into(holder.mImage);

        Imageurl = mSearchList.get(position).getProfilePic();
      }



      holder.mCardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

          Intent mIntent = new Intent(mContext, OthersProfile.class);
          mIntent.putExtra("userid", mSearchList.get(holder.getAdapterPosition()).getUserid());
          mContext.startActivity(mIntent);

        }
      });
    }

  }

  @Override
  public int getItemCount() {
    return showshimmer ? mSearchList.size() : mSearchList.size();
  }


  // Filter Class
  public void filter(String charText) {
    charText = charText.toLowerCase(Locale.getDefault());
    mSearchList.clear();
    if (charText.length() == 0) {
      mSearchList.addAll(arraylist);
    } else {
      for (SearchData wp : arraylist) {
        if (wp.getFirstName().toLowerCase(Locale.getDefault())
          .contains(charText)) {
          mSearchList.add(wp);
        }
        if (wp.getCity().toLowerCase(Locale.getDefault())
          .contains(charText)) {
          mSearchList.add(wp);
        }
      }
    }
    notifyDataSetChanged();
  }


class SearchViewHolder extends RecyclerView.ViewHolder {

  ImageView mImage,statusonline,premium;
  TextView mTitle,tvcity;
  CardView mCardView;
  ShimmerFrameLayout sh1;

  SearchViewHolder(View itemView) {
    super(itemView);

    premium = itemView.findViewById(R.id.premium);
    mImage = itemView.findViewById(R.id.ivImage);
    mTitle = itemView.findViewById(R.id.tvTitle);
    mCardView = itemView.findViewById(R.id.cardview);
    statusonline = itemView.findViewById(R.id.heart);
    sh1= itemView.findViewById(R.id.parentShimmerLayout);
    tvcity = itemView.findViewById(R.id.tvcity);

  }
}
}
