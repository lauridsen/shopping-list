package org.projects.shoppinglist;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Michael on 03-05-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(),remoteMessage.getNotification().getBody(),Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}