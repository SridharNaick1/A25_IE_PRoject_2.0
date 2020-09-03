package com.example.bottomnavigation;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.bottomnavigation.App.CHANNEL_ID;

public class CollectionDayNotificationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int input = intent.getIntExtra("collectiondays",5);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Carbon Centric")
                .setContentText("Tomorrow is garbage collection day!")
                .setSmallIcon(R.drawable.ic_appliance2)
                .setContentIntent(pendingIntent)
                .build();

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        if(input != 5){
            if(input == today - 1){
                startForeground(1, notification);
            }
        }
        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
