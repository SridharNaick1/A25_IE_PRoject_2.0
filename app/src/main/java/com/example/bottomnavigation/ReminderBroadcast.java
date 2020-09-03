package com.example.bottomnavigation;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.bottomnavigation.App.CHANNEL_ID;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyCollectionDay")
                .setSmallIcon(R.drawable.ic_appliance2)
                .setContentText("Tomorrow is garbage collection day!")
                .setContentTitle("Carbon Centric")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200,builder.build());
    }
}
