package com.eis.transteinle.gigloungepoc;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by DerOlli on 28.11.14.
 */
public class MSGService extends IntentService {
    SharedPreferences prefs;
    NotificationCompat.Builder notification;
    NotificationManager manager;
    public MSGService() {
        super("MSGService");
        Log.d("MSGService", "msgservice start");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        prefs = getSharedPreferences("Chat", 0);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e("L2C","Error");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e("L2C","Error");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                if(!prefs.getString("CURRENT_ACTIVE","").equals(extras.getString("from"))) {
                    sendNotification(extras.getString("msg"), extras.getString("username"));
                }
                Log.i("TAG", "Received: " + extras.getString("msg"));
            }
        }
        MSGReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg,String username) {
        Bundle args = new Bundle();
        args.putString("username", username);
        args.putString("msg", msg);
        Intent chat = new Intent(this, ChatActivity.class);
        chat.putExtra("INFO", args);
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(username);
        notification.setContentText(msg);
        notification.setTicker("New Message !");
        notification.setSmallIcon(R.drawable.ic_launcher);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                chat, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }
}
