package com.thesingleapp.thesingleapp.fragment.SideNavigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.tab.Premium.Orders_tab_Fragment;
import com.thesingleapp.thesingleapp.tab.Premium.Premium_tab_Fragment;

public class Premium_Fragment extends Fragment implements View.OnClickListener {

    private Button tab1,tab2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View premium =inflater.inflate(R.layout.fragment_premium,container,false);

        tab1 = premium.findViewById(R.id.tab1);
        tab2 = premium.findViewById(R.id.tab2);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);

        //To show default fragment
        replaceFragment(new Premium_tab_Fragment());

        return premium;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tab1:

                //To show Premium fragment
                replaceFragment(new Premium_tab_Fragment());
                tab1.setClickable(false);
                if(tab2.isClickable())
                {

                }
                else
                {
                    tab2.setClickable(true);
                }
                tab1.setTextColor(getResources().getColor(R.color.white));
                tab2.setTextColor(getResources().getColor(R.color.black));
                tab1.setBackgroundResource(R.drawable.gradient_lr);
                tab2.setBackgroundColor(getResources().getColor(R.color.white));

                break;

            case R.id.tab2:

                //To show Orders fragment
                replaceFragment(new Orders_tab_Fragment());
                tab2.setClickable(false);

                if(tab1.isClickable())
                {

                }
                else
                {
                    tab1.setClickable(true);
                }
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
        fragTrans.replace(R.id.linearpremium, fragment, "MY_FRAGMENT");
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }

}