package com.thesingleapp.thesingleapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.fragment.OrderDetailsFragment;
import com.thesingleapp.thesingleapp.model.Orders_model;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ConnectionsHolder> {

    Orders_model orderModel;
    private Context context;
    private List<Orders_model> orderModelList;
    public static FragmentManager fragmentManager;
    public  boolean showshimmer = true;

    public OrdersAdapter(Context context, List<Orders_model> ordersModelList) {
        this.orderModelList = ordersModelList;
        this.context = context;

    }

    @Override
    public OrdersAdapter.ConnectionsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_list, parent, false);
        OrdersAdapter.ConnectionsHolder viewHolder = new OrdersAdapter.ConnectionsHolder(v);

        return viewHolder;
    }
    @Override
    public void onBindViewHolder(OrdersAdapter.ConnectionsHolder holder, int position) {
        orderModel = orderModelList.get(position);
        if(showshimmer)
        {
            holder.sh1.startShimmer();
        }
        else {
            holder.sh1.stopShimmer();
            holder.sh1.setShimmer(null);
            holder.sh1.setBackground(null);
            Resources resource= context.getResources();


            holder.dollar.setImageResource(R.drawable.dollor_white);
            holder.dateicon.setImageResource(R.drawable.date_white);
            holder.dollorlast.setImageResource(R.drawable.dollor_white_big);
            holder.clockicon.setImageResource(R.drawable.clock_white);


            holder.bookid.setText("Book ID "+orderModel.getReferenceno());
            holder.amount.setText(orderModel.getAmount());
            holder.date.setText(orderModel.getDayofpayment());
            holder.time.setText(orderModel.getTimeofpayment());
            holder.permonth.setText("For "+orderModel.getNoofdays()+" Days");

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
        return showshimmer ? orderModelList.size() : orderModelList.size();
    }

    public class ConnectionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView amount,date,time,permonth,bookid;
        public ConstraintLayout mainlayout;
        ShimmerFrameLayout sh1;
        public ImageView dateicon,dollar,dollorlast,clockicon;

        public ConnectionsHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            time = itemView.findViewById(R.id.time);
            permonth = itemView.findViewById(R.id.permonth);
            bookid = itemView.findViewById(R.id.bookid);
            sh1= itemView.findViewById(R.id.parentShimmerLayout);
            dateicon = itemView.findViewById(R.id.dateicon);
            dollar = itemView.findViewById(R.id.dollar);
            dollorlast = itemView.findViewById(R.id.dollorlast);
            clockicon = itemView.findViewById(R.id.clockicon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            if ((v == itemView)){
                Orders_model ordersModel_s = orderModelList.get(getAdapterPosition());

                if ((v == itemView)){

                    OrderDetailsFragment dialogFragment = new OrderDetailsFragment();

                    Bundle bundle = new Bundle();
                    bundle.putBoolean("notAlertDialog", true);
                    bundle.putString("bookid", ordersModel_s.getReferenceno());
                    bundle.putString("amount", ordersModel_s.getAmount());
                    bundle.putString("date", ordersModel_s.getDayofpayment());
                    bundle.putString("time", ordersModel_s.getTimeofpayment());
                    bundle.putString("noofdays", ordersModel_s.getNoofdays());
                    bundle.putString("paymentstatus", ordersModel_s.getPaymentstatus());
                    bundle.putString("validdate", ordersModel_s.getDateofvalid());
                    bundle.putString("validtime", ordersModel_s.getTimeofvalid());

                    dialogFragment.setArguments(bundle);

                    FragmentTransaction ft = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                    Fragment prev = ((FragmentActivity)context).getSupportFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    dialogFragment.show(ft, "dialog");

                }

            }

        }
    }
}
