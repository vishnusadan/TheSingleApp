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
import com.thesingleapp.thesingleapp.activity.ChatScreen;
import com.thesingleapp.thesingleapp.activity.OthersProfile;
import com.thesingleapp.thesingleapp.activity.PremiumScreen;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Message_model;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    Message_model messageModel;
    private Context context;
    private List<Message_model> messageList;
    public String userpremiumtype;

    public MessageAdapter(Context context, List<Message_model> messageList) {
        this.messageList = messageList;
        this.context = context;

    }

    @Override
    public MessageAdapter.MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inbox_list, parent, false);
        MessageAdapter.MessageHolder viewHolder = new MessageAdapter.MessageHolder(v);

        //get User data
        UserDataModel data = UserDataModel.getInstance();
        userpremiumtype = data.getPremium();

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(MessageAdapter.MessageHolder holder, int position) {
        messageModel = messageList.get(position);

        Resources resource= context.getResources();
        holder.firstname.setText(messageModel.getFirstName());
        holder.msg.setText(messageModel.getMsg());
        String premium = messageModel.getpremium();

        if (messageModel.getCountvalue().equals("0")){

            holder.unreadcount.setVisibility(View.INVISIBLE);

        }else {

            holder.unreadcount.setVisibility(View.VISIBLE);
            holder.unreadcount.setText(messageModel.getCountvalue());

        }

        String time = messageModel.getDate();
        String gettime = time.substring(13,22);

        holder.date.setText(gettime);

        if (premium.equals("true"))
        {
            holder.premium.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.premium.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(Api.IP +"media/"+messageModel.getProfilePic())
                .placeholder(R.drawable.single_image_dummy)
                .dontAnimate()
                .centerCrop().into(holder.profilepic);


            if(messageModel.getStatus().equals("true")){

                holder.online.setImageResource(R.drawable.active_home);

            }else {

                holder.online.setImageResource(R.drawable.inactive_home);

            }



    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView firstname,msg,date,unreadcount;
        public ImageView profilepic,online,premium;

        public MessageHolder(View itemView) {
            super(itemView);

            firstname = itemView.findViewById(R.id.tvfirstname);
            msg = itemView.findViewById(R.id.tvcity);
            date = itemView.findViewById(R.id.tvtime);
            unreadcount = itemView.findViewById(R.id.unreadcount);
            profilepic = itemView.findViewById(R.id.ivImage);
            online = itemView.findViewById(R.id.online);
            premium = itemView.findViewById(R.id.starImage);

            itemView.setOnClickListener(this);
            profilepic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            Message_model viewedmeModel_s = messageList.get(getAdapterPosition());

            if ((v == profilepic)){

                Intent intent = new Intent(context, OthersProfile.class);

                intent.putExtra("userid", viewedmeModel_s.getUserid());

                context.startActivity(intent);

            }
            if ((v == itemView)){


                //done for premium user can only message
                if (userpremiumtype.equals("true")) {


                    if (viewedmeModel_s.getFriendonlyvalue().equals("true")){

                       if(viewedmeModel_s.getFriendvalue().equals("true")){

                           if ((v == itemView)) {

                           Intent intent = new Intent(context, ChatScreen.class);

                           intent.putExtra("UserId", viewedmeModel_s.getUserid());

                           context.startActivity(intent);

                           }

                        }else {

                           CommonMethod.showAlertDialog(context, "Friend", "You Want To Be Friend To Message", " ", "Ok", new CommonMethod.DialogClickListener() {
                                       @Override
                                       public void dialogOkBtnClicked(String value) {

                                       }

                                       @Override
                                       public void dialogNoBtnClicked(String value) {

                                       }
                                   }
                           );

                       }

                    }else {


                        if ((v == itemView)) {

                            Intent intent = new Intent(context, ChatScreen.class);

                            intent.putExtra("UserId", viewedmeModel_s.getUserid());

                            context.startActivity(intent);

                        }

                    } } else {

                        CommonMethod.showAlertDialog(context, "Premium", "You Want To Be Premium User To Message", "Be Premium", "Cancel", new CommonMethod.DialogClickListener() {
                                    @Override
                                    public void dialogOkBtnClicked(String value) {

                                        Intent intent = new Intent(context, PremiumScreen.class);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void dialogNoBtnClicked(String value) {

                                    }
                                }
                        );

                    }
            }

        }
    }
}
