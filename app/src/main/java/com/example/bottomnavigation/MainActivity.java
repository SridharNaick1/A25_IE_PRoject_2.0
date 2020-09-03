package com.example.bottomnavigation;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemBackgroundResource(R.color.test_second_background);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_calculator, R.id.navigation_classification,R.id.navigation_general,R.id.navigation_settings)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        NavigationUI.setupWithNavController(navView, navController);
        createNotificationChannel();
        checkCollectionDay();
    }

    public void checkCollectionDay(){
        Intent notificationIntent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,
                0, notificationIntent, 0);

        AlarmManager alarmManager =(AlarmManager) getSystemService(ALARM_SERVICE);
        long timeForNow = System.currentTimeMillis();
        long tenSeconds = 1000 * 10;

        SharedPreferences sharedPref = this.getSharedPreferences("collectionDay",Context.MODE_PRIVATE);
        int collectionDay = sharedPref.getInt("CollectionDays",5);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        if(collectionDay != 5){
            if(collectionDay == today - 1){
                alarmManager.set(AlarmManager.RTC_WAKEUP,timeForNow + tenSeconds,pendingIntent );
            }
        }
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "CollectionDayReminderChannel";
            String description = "Channel for Collection Day Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyCollectionDay",name,importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
