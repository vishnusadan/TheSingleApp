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
import com.thesingleapp.thesingleapp.tab.favorite.FavoritedMe_Fragment;
import com.thesingleapp.thesingleapp.tab.favorite.MyFavorite_Fragment;

public class Favorites_Fragment extends Fragment implements View.OnClickListener {

    private Button tab1,tab2;

    private long mLastClickTime = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View favorites =inflater.inflate(R.layout.fragment_favorites,container,false);

        tab1 = favorites.findViewById(R.id.tab1);
        tab2 = favorites.findViewById(R.id.tab2);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);

        //To show default fragment
        replaceFragment(new FavoritedMe_Fragment());

        return favorites;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {


            case R.id.tab1:

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
                //To show Favorited me fragment
                replaceFragment(new FavoritedMe_Fragment());

                tab1.setTextColor(getResources().getColor(R.color.white));
                tab2.setTextColor(getResources().getColor(R.color.black));
                tab1.setBackgroundResource(R.drawable.gradient_lr);
                tab2.setBackgroundColor(getResources().getColor(R.color.white));


                break;

            case R.id.tab2:

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
                //To show My fav fragment
                replaceFragment(new MyFavorite_Fragment());

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
        fragTrans.replace(R.id.linearfav, fragment, "MY_FRAGMENT");
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }

}
