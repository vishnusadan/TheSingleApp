package com.thesingleapp.thesingleapp.Notification.Services;

import android.content.SharedPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class MyFirebaseInstanceService extends FirebaseInstanceIdService {

    private static final String TAG="MyFirebaseInstanceServi";
    //Login save in share preference
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken =FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        /* If you want to send messages to this application instance or manage this apps subscriptions on the server side, send the Instance ID token to your app server.*/

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {


        // Shared prefference intiated
        loginPreferences = getSharedPreferences("firebasetoken", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        if (refreshedToken.equals("")) {


        }else {

            loginPrefsEditor.putString("firebasetoken", refreshedToken);
            loginPrefsEditor.commit();

        }

    }
}
