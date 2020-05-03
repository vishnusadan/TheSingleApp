package com.thesingleapp.thesingleapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.thesingleapp.thesingleapp.R;


public class OrderDetailsFragment extends DialogFragment {

    private TextView bookid,amount,orderdate,ordertime,valid_upto_date,valid_upto_time,totaldays,paymentstatus;
    private String bookid_s,amount_s,orderdate_s,ordertime_s,valid_upto_date_s,valid_upto_time_s,totaldays_s,paymentstatus_s;
    private ImageButton submitbutton;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_dialog, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookid = view.findViewById(R.id.bookid);
        amount = view.findViewById(R.id.amount);
        orderdate = view.findViewById(R.id.orderdate);
        ordertime = view.findViewById(R.id.ordertime);
        valid_upto_date = view.findViewById(R.id.valid_upto_date);
        valid_upto_time = view.findViewById(R.id.valid_upto_time);
        totaldays = view.findViewById(R.id.totaldays);
        paymentstatus = view.findViewById(R.id.paymentstatus);
        submitbutton = view.findViewById(R.id.submitbutton);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();

            }
        });

        bookid.setText("Book Id : "+bookid_s);
        amount.setText("Amount : "+amount_s);
        orderdate.setText("Order Date : "+orderdate_s);
        ordertime.setText("Order Time : "+ordertime_s);
        valid_upto_date.setText("Valid Date : "+valid_upto_date_s);
        valid_upto_time.setText("Valid Time : "+valid_upto_time_s);
        totaldays.setText("Total Days : "+totaldays_s);
        paymentstatus.setText("Payment Status : "+paymentstatus_s);


    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setFullScreen = false;
        if (getArguments() != null) {
            setFullScreen = getArguments().getBoolean("fullScreen");

            bookid_s = getArguments().getString("bookid");
            amount_s = getArguments().getString("amount");
            orderdate_s = getArguments().getString("date");
            ordertime_s = getArguments().getString("time");
            totaldays_s = getArguments().getString("noofdays");
            paymentstatus_s = getArguments().getString("paymentstatus");
            valid_upto_date_s = getArguments().getString("validdate");
            valid_upto_time_s = getArguments().getString("validtime");
        }

        if (setFullScreen)
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface DialogListener {
        void onFinishEditDialog(String inputText);

    }


}

