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
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.OthersProfile;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.model.Comments_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ConnectionsHolder> {

    Comments_model commentsModel;
    private Context context;
    private List<Comments_model> commentList;

    public CommentsAdapter(Context context, List<Comments_model> commentList) {
        this.commentList = commentList;
        this.context = context;
    }

    @Override
    public CommentsAdapter.ConnectionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comments_list, parent, false);
        CommentsAdapter.ConnectionsHolder viewHolder = new CommentsAdapter.ConnectionsHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(CommentsAdapter.ConnectionsHolder holder, int position) {
        commentsModel = commentList.get(position);

        Resources resource= context.getResources();
        holder.firstname.setText(commentsModel.getFirstName());
        holder.place.setText(commentsModel.getMsg());

        Glide.with(context)
                .load(Api.IP +"media/"+commentsModel.getProfilePic())
                .placeholder(R.drawable.single_image_dummy)
                .dontAnimate()
                .centerCrop().into(holder.profilepic);
        
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ConnectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView firstname,place;
        public ImageView profilepic;

        public ConnectionsHolder(View itemView) {
            super(itemView);

            firstname = itemView.findViewById(R.id.tvfirstname);
            place = itemView.findViewById(R.id.tvcity);
            profilepic = itemView.findViewById(R.id.ivImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //get User data
            UserDataModel data = UserDataModel.getInstance();

             String userid = data.getId();

            if ((v == itemView)){
                Comments_model commentsModel_s = commentList.get(getAdapterPosition());

                if ((v == itemView)){

                    if (userid.equals(commentsModel_s.getUserid())){


                    }else {

                        Intent intent = new Intent(context, OthersProfile.class);

                        intent.putExtra("userid", commentsModel_s.getUserid());

                        context.startActivity(intent);
                    }
                }


            }

        }
    }
}
