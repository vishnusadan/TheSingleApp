package com.thesingleapp.thesingleapp.fragment.SideNavigation.connections;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.tab.Requests.Friends_Fragment;
import com.thesingleapp.thesingleapp.tab.Requests.Requests_Fragment;
import com.thesingleapp.thesingleapp.tab.flirt.FlirtedMe_Fragment;
import com.thesingleapp.thesingleapp.tab.flirt.IHadFlirt_Fragment;


public class Request_Fragment extends Fragment implements View.OnClickListener{
    private Button tab1,tab2;

    private long mLastClickTime = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View requests =inflater.inflate(R.layout.fragment_request,container,false);

        tab1 = requests.findViewById(R.id.request);
        tab2 = requests.findViewById(R.id.requested);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);


        //To show default fragment
        replaceFragment(new Friends_Fragment());

        return requests;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.request:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                tab1.setClickable(false);
                if(tab2.isClickable())
                {

                }
                else
                {
                    tab2.setClickable(true);
                }
                //To show Flirted me fragment
                replaceFragment(new  Friends_Fragment());

                tab1.setTextColor(getResources().getColor(R.color.white));
                tab2.setTextColor(getResources().getColor(R.color.black));
                tab1.setBackgroundResource(R.drawable.gradient_lr);
                tab2.setBackgroundColor(getResources().getColor(R.color.white));


                break;

            case R.id.requested:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                tab2.setClickable(false);

                if(tab1.isClickable())
                {

                }
                else
                {
                    tab1.setClickable(true);
                }
                //To show i had flirt fragment
                replaceFragment(new Requests_Fragment());

                tab1.setTextColor(getResources().getColor(R.color.black));
                tab2.setTextColor(getResources().getColor(R.color.white));
                tab1.setBackgroundColor(getResources().getColor(R.color.white));
                tab2.setBackgroundResource(R.drawable.gradient_lr);

                break;
        }
    }


    private void replaceFragment(Fragment fragment) {

        FragmentManager fragMan = getActivity().getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMan.beginTransaction();
        fragTrans.replace(R.id.linearrequest, fragment, "MY_FRAGMENT");
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }
}
