package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.model.Help_model;
import com.thesingleapp.thesingleapp.userdata.UserData;
import java.util.List;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpHolder> {

    Help_model helpModel;
    private Context context;
    private List<Help_model> helpModelList;

    public HelpAdapter(Context context, List<Help_model> helpModelList) {
        this.helpModelList = helpModelList;
        this.context = context;

    }

    @Override
    public HelpAdapter.HelpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_row_list, parent, false);
        HelpAdapter.HelpHolder viewHolder = new HelpAdapter.HelpHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(HelpAdapter.HelpHolder holder, int position) {
        helpModel = helpModelList.get(position);

        Resources resource= context.getResources();
        holder.help.setText(helpModel.getHelp());
        holder.answer.setText(helpModel.getAnswer());

    }

    @Override
    public int getItemCount() {
        return helpModelList.size();
    }

    public class HelpHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView help,answer;

        ImageButton show;

        public HelpHolder(View itemView) {
            super(itemView);


            help = itemView.findViewById(R.id.help);
            answer = itemView.findViewById(R.id.answer);
            show = itemView.findViewById(R.id.show);

            answer.setVisibility(View.GONE);
            show.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


                if ((v == show)){

                    if(UserData.value.equals("0")) {

                        answer.setVisibility(View.VISIBLE);

                        UserData.value = "1";

                        show.setBackgroundResource(R.drawable.downarrow_pink);

                    }else {

                        answer.setVisibility(View.GONE);

                        UserData.value = "0";

                        show.setBackgroundResource(R.drawable.rightarrow_pink);

                    }

            }

        }
    }
}
