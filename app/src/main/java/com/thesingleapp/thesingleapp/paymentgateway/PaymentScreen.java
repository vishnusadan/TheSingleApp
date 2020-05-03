package com.thesingleapp.thesingleapp.paymentgateway;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.thesingleapp.thesingleapp.R;
import com.thesingleapp.thesingleapp.activity.LoginScreen;
import com.thesingleapp.thesingleapp.apilist.Api;
import com.thesingleapp.thesingleapp.commonservice.CommonMethod;
import com.thesingleapp.thesingleapp.userdata.UserDataModel;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaymentScreen extends AppCompatActivity implements View.OnClickListener {

    //The views
    private Button buttonPay;
    private TextView editTextAmount,noofdays_tv,validfrommonth,validfromyear,username_tv;
    private ImageView backbutton;

    // Creating Volley RequestQueue.
    RequestQueue requestQueue;

    int random;

    private StringRequest stringRequest;

    //Payment Amount
    private String paymentAmount,premiumid,orginaltime,orginalrandomnumber,orginaldate,paymentid,status,token,userid,noofdays,month,noofyear,username,premium;

    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextAmount = findViewById(R.id.editTextAmount);
        noofdays_tv = findViewById(R.id.noofdays);
        validfrommonth = findViewById(R.id.validfrommonth);
        validfromyear = findViewById(R.id.validfromyear);
        username_tv = findViewById(R.id.username);
        buttonPay = findViewById(R.id.buttonPay);
        backbutton = findViewById(R.id.backbutton);

        buttonPay.setOnClickListener(this);
        backbutton.setOnClickListener(this);

        Random random1 = new Random();
        // generate a random integer from 0 to 899, then add 100
        random= random1.nextInt((9999 - 100) + 1) + 10;

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String dateformat=String.valueOf(formatter.format(date));

        String[] dateformat1=dateformat.split("/");
        orginaldate=dateformat1[0];

        String[] timeformat=dateformat.split(" ");
        String timevalue=timeformat[1];
        orginaltime=timevalue.replaceAll(":","");

        orginalrandomnumber=orginaldate+orginaltime+random;


        Intent data = getIntent();
        paymentAmount = data.getStringExtra("Amount");
        premiumid = data.getStringExtra("Id");
        noofdays = data.getStringExtra("Noofdays");
        month = data.getStringExtra("Month");
        noofyear = data.getStringExtra("NoofYear");


        //get User data
        UserDataModel userdata = UserDataModel.getInstance();

        token = userdata.getToken();
        userid = userdata.getId();
        username = userdata.getUsername();
        premium = userdata.getPremium();
        editTextAmount.setText("$"+paymentAmount);
        username_tv.setText(username);
        noofdays_tv.setText(noofdays);
        validfrommonth.setText(month);
        validfromyear.setText(noofyear);


        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.backbutton:

                finish();

                break;

            case R.id.buttonPay:

                if (premium=="true")
                {

                    CommonMethod.showAlertDialog(PaymentScreen.this, "", "Your Account is Already Premium..! Please buy After premium validity Finished.", "", "OK", new CommonMethod.DialogClickListener() {
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
                    getPayment();
                }


                break;
        }

    }


    //Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);


    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    private void getPayment() {


        //Creating a paypalpayment
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(paymentAmount)), "USD", "The Single App Premium",
                PayPalPayment.PAYMENT_INTENT_SALE);

        //Creating Paypal Payment activity intent
        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        //Puting paypal payment to the intent
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
//                        Log.i("paymentExample", paymentDetails);

                        try {
                            JSONObject jsonDetails = new JSONObject(paymentDetails);

                            JSONObject jsondata = jsonDetails.getJSONObject("response");

                            paymentid = jsondata.getString("id");
                            status = jsondata.getString("state");

                            // payment
                            Payment();

                        } catch (JSONException e) {
                        }


                    } catch (JSONException e) {
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {

                startActivity(new Intent(PaymentScreen.this,ConfirmationActivity.class).putExtra("amount","0").putExtra("status","Payment Failed"));


            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {

                startActivity(new Intent(PaymentScreen.this,ConfirmationActivity.class).putExtra("amount","0").putExtra("status","Payment Failed"));
            }
        }
    }


    public void Payment(){

        //Show Progress Dailog
        CommonMethod.showpDialog(this,"Processing.. Don't Close The App..");

        // Creating string request with post method.
        stringRequest = new StringRequest(Request.Method.POST, Api.TRANSACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();

                        //Starting a new activity for the payment details and also putting the payment details with intent
                        startActivity(new Intent(PaymentScreen.this,ConfirmationActivity.class).putExtra("amount",paymentAmount).putExtra("status","Payment Success"));

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        //Hide Progress  Dailog
                        CommonMethod.hidepDialog();


                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                100,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        // Showing error message if something goes wrong.
                        CommonMethod.showToast(PaymentScreen.this,"Please Contact Customer Care");

                    }
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.

                params.put("status", status);
                params.put("amount", paymentAmount);
                params.put("mode_of_payment", "OTHER");
                params.put("transaction_id", paymentid);
                params.put("premium_id", premiumid);
                params.put("book_id", orginalrandomnumber);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("Authorization", "Token "+token);
                return params;
            }


        };

        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(PaymentScreen.this);

//             Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);

    }

}
