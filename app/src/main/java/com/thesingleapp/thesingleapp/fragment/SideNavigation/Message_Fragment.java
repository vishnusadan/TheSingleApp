package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.tab.Message.Blocked_Fragment;
import com.thesingleapp.thesingleapp.tab.Message.Friends_Fragment;
import com.thesingleapp.thesingleapp.tab.Message.Message_Tab_Fragment;

public class Message_Fragment extends Fragment implements View.OnClickListener {

    private LinearLayout tab1,tab3;
    private ImageView messageicon,blockedicon;
    private TextView messagetv,blockedtv;
    private long mLastClickTime = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View message =inflater.inflate(R.layout.fragment_message,container,false);

        tab1 = message.findViewById(R.id.tab1);

        tab3 = message.findViewById(R.id.tab3);

        messageicon = message.findViewById(R.id.messageicon);

        blockedicon = message.findViewById(R.id.blockicon);

        messagetv = message.findViewById(R.id.tv_message);

        blockedtv = message.findViewById(R.id.tv_blocked);

        tab1.setOnClickListener(this);

        tab3.setOnClickListener(this);

        //default fragment
        replaceFragment(new Message_Tab_Fragment());

        return message;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.tab1:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                tab1.setClickable(false);

                if(tab3.isClickable())
                {

                }
                else
                {
                    tab3.setClickable(true);
                }
                //Message tab fragment
                replaceFragment(new Message_Tab_Fragment());

                tab1.setBackgroundResource(R.drawable.gradient_lr);
                messageicon.setBackgroundResource(R.drawable.message_white);
                messagetv.setTextColor(getResources().getColor(R.color.white));

                tab3.setBackgroundColor(getResources().getColor(R.color.white));
                blockedicon.setBackgroundResource(R.drawable.blocked_gray);
                blockedtv.setTextColor(getResources().getColor(R.color.gray));

                break;

            case R.id.tab3:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                tab3.setClickable(false);

                if(tab1.isClickable())
                {

                }
                else
                {
                    tab1.setClickable(true);
                }
                //friends tab fragment
                replaceFragment(new Blocked_Fragment());

                tab1.setBackgroundColor(getResources().getColor(R.color.white));
                messageicon.setBackgroundResource(R.drawable.message_grey);
                messagetv.setTextColor(getResources().getColor(R.color.gray));

                tab3.setBackgroundResource(R.drawable.gradient_lr);
                blockedicon.setBackgroundResource(R.drawable.blocked_white);
                blockedtv.setTextColor(getResources().getColor(R.color.white));

                break;
        }
    }
    private void replaceFragment(Fragment fragment) {

        FragmentManager fragMan = getActivity().getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMan.beginTransaction();
        fragTrans.replace(R.id.linearmsg, fragment, "MY_FRAGMENT");
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }
}

