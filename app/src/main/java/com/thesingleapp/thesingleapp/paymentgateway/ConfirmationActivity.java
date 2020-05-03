package com.thesingleapp.thesingleapp.paymentgateway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.HomeScreen;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;


public class ConfirmationActivity extends AppCompatActivity {

    public TextView statustv,msgtv;
    /** Called when the activity is first created. */
    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        statustv = findViewById(R.id.textView6);
        msgtv = findViewById(R.id.textView8);

        //Getting Intent
        Intent intent = getIntent();

        String amount = intent.getStringExtra("amount");
        String status = intent.getStringExtra("status");

        UserDataModel data = UserDataModel.getInstance();


        if(status.equals("Payment Success")) {
            data.setPremium("true");
            statustv.setText(status);
            msgtv.setText("Your Payment of $" + amount + " has been processed successfully.");

        }else {

            statustv.setText(status);
            msgtv.setText("");

        }

        // animation
        StartSplash();

    }

    private void StartSplash() {


        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 1000) {
                        sleep(30);
                        waited += 30;
                    }

                    startActivity(new Intent(ConfirmationActivity.this, HomeScreen.class));
                    finish();

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                }

            }
        };
        splashTread.start();
    }


}
