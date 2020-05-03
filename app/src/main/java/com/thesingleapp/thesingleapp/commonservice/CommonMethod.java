package com.thesingleapp.thesingleapp.commonservice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.thesingleapp.thesingleapp.R;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonMethod {

    // Progress dialog
    public static ProgressDialog pDialog;
    private Context context;


    public CommonMethod(Context context){
        this.context = context;
    }



    //Show Progress Bar
    public static void showpDialog(Activity act,String message) {

        //Progress Dailog
        pDialog = new ProgressDialog(act);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);

        if (!pDialog.isShowing())
            pDialog.show();
    }

    //Hide Progress Bar
    public static void hidepDialog() {


        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //Get Current date
    public static String getCurrentDate(){
        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObj = new Date();
        String newDateStr = curFormater.format(dateObj);
        return newDateStr;
    }

        public static void showToast(Context mContext,String message){
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }


    public static void showAlertDialog(final Context context, String title, String message, String positiveBtnText, String negativeBtnText, final DialogClickListener dialogClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        if (title != null && title.length() > 0)
            alertDialogBuilder.setTitle(title);
        if (message != null && message.length() > 0)
            alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(positiveBtnText, null);

        if (negativeBtnText != null) {
            alertDialogBuilder.setNegativeButton(negativeBtnText, null);
        }
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final Button okBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                okBtn.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogClickListener.dialogOkBtnClicked("");
                        alertDialog.dismiss();

                    }
                });
                Button cancelBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancelBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                cancelBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogClickListener.dialogNoBtnClicked("");
                        alertDialog.dismiss();
                    }
                });

            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public interface DialogClickListener {
        void dialogOkBtnClicked(String value);
        void dialogNoBtnClicked(String value);
    }

}
