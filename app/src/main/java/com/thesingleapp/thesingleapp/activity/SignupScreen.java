package com.thesingleapp.thesingleapp.activity;

import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.fragment.signup.Signup_Fragment;


public class SignupScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        replaceFragment(new Signup_Fragment());

        //check mobile version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            ActivityCompat.requestPermissions(SignupScreen.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.READ_SMS,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION},1 );

        }

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMan.beginTransaction();
        fragTrans.replace(R.id.linear, fragment, "MY_FRAGMENT");
        fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragTrans.commit();
    }
}
