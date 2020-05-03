package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.model.Premium_model;
import com.thesingleapp.thesingleapp.paymentgateway.PaymentScreen;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PremiumAdapter extends RecyclerView.Adapter<PremiumAdapter.ConnectionsHolder> {

    Premium_model premiumModel;
    private Context context;
    private List<Premium_model> premiumModelList;
    public  String month = "";
    public  boolean showshimmer = true;

    public PremiumAdapter(Context context, List<Premium_model> viewedmeModelList) {
        this.premiumModelList = viewedmeModelList;
        this.context = context;

    }

    @Override
    public PremiumAdapter.ConnectionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.premium_list, parent, false);
        PremiumAdapter.ConnectionsHolder viewHolder = new PremiumAdapter.ConnectionsHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(PremiumAdapter.ConnectionsHolder holder, int position) {
        premiumModel = premiumModelList.get(position);
        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else {
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.sh1.setBackground(null);


            holder.dollar.setImageResource(R.drawable.dollor_white);
            holder.dollarlast.setImageResource(R.drawable.dollor_white_big);


            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();
            if (Integer.parseInt(premiumModel.getMonth()) >= 0 && (Integer.parseInt(premiumModel.getMonth()) <= 11 )){
                month = months[(Integer.parseInt(premiumModel.getMonth())-1)];
            }

            Resources resource= context.getResources();
            holder.permonth.setText("For "+premiumModel.getNoofdays()+" days "+" Valid From " +month+" "+premiumModel.getYear());
            holder.amount.setText(premiumModel.getAmount());


            if (position%4 == 0){
                holder.mainlayout.setBackgroundResource(R.drawable.premium_first_cell);
            } else if (position%4 == 1){
                holder.mainlayout.setBackgroundResource(R.drawable.premium_third_cell);
            } else if (position%4 == 2){
                holder.mainlayout.setBackgroundResource(R.drawable.premium_second_cell);
            }else {
                holder.mainlayout.setBackgroundResource(R.drawable.premium_third_cell);

            }
        }


    }

    @Override
    public int getItemCount() {
        return showshimmer ? premiumModelList.size() : premiumModelList.size();
    }

    public class ConnectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView permonth,amount;
        public ImageView profilepic,dollar,dollarlast;
        public ConstraintLayout mainlayout;
        ShimmerFrameLayout sh1;
        String[] current;
        String currentmonth,currentyear;

        public ConnectionsHolder(View itemView) {
            super(itemView);

            permonth = itemView.findViewById(R.id.permonth);
            amount = itemView.findViewById(R.id.amount);
            mainlayout = itemView.findViewById(R.id.mainlay);
            dollar = itemView.findViewById(R.id.dollar);
            dollarlast = itemView.findViewById(R.id.dollorlast);
            sh1= itemView.findViewById(R.id.parentShimmerLayout);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate= formatter.format(date);
            current=strDate.split("/");

            currentmonth=current[1];

            currentyear=current[2];

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

                if ((v == itemView)){
                    Premium_model premiumeModel_s = premiumModelList.get(getAdapterPosition());


                        if (Integer.parseInt(currentmonth) < Integer.parseInt(premiumeModel_s.getMonth())+1 || Integer.parseInt(currentyear) < Integer.parseInt(premiumeModel_s.getYear()))
                        {
                            CommonMethod.showAlertDialog(context, "Premium", "You are Too advanced to buy this plan wait for it..!",  "",  "Ok", new CommonMethod.DialogClickListener() {
                                        @Override
                                        public void dialogOkBtnClicked(String value) {



                                        }
                                        @Override
                                        public void dialogNoBtnClicked(String value) {

                                        }
                                    }
                            );
                        }
                        else
                        {
                        Intent intent = new Intent(context, PaymentScreen.class);
                        intent.putExtra("Id", premiumeModel_s.getId());
                        intent.putExtra("Amount", premiumeModel_s.getAmount());
                        intent.putExtra("Noofdays", premiumeModel_s.getNoofdays());
                        intent.putExtra("Month", month);
                        intent.putExtra("NoofYear", premiumeModel_s.getYear());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }


            }


        }

    }

}
