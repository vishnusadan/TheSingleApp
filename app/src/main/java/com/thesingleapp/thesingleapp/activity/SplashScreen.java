package com.thesingleapp.thesingleapp.activity;


import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.adapter.SlidingImage_Adapter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import me.relex.circleindicator.CircleIndicator;


public class SplashScreen extends AppCompatActivity implements View.OnClickListener {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.spalshone,R.drawable.splashtwo,R.drawable.splashthree};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private TextView loginbtn,content;
    private ImageView skip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        loginbtn = findViewById(R.id.login);
        skip= findViewById(R.id.next);
        content = findViewById(R.id.contentsplash);
        mPager = findViewById(R.id.pager);
        //Method for only once show this activity when install
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);


        if (isFirstRun.equals(true)) {
            //Slide Images Method
            slideimage();
        }else {
            startActivity(new Intent(SplashScreen.this, LoginScreen.class));
            finishAffinity();
        }
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();
        loginbtn.setOnClickListener(this);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {

                if(i==0)
                {
                    content.setText(R.string.splashone);
                }
                if(i==1)
                {
                    content.setText(R.string.splashtwo);
                }
                if(i==2)
                {
                    content.setText(R.string.splashthree);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    private void slideimage() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager.setAdapter(new SlidingImage_Adapter(SplashScreen.this,XMENArray));
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3500, 3500);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.login:

                startActivity(new Intent(SplashScreen.this,LoginScreen.class));
                finish();

                break;

        }
    }

}
