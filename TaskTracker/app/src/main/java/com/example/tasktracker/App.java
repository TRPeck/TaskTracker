package com.example.tasktracker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


/* Class to handle Notification Channel. Notification Channel necessary for handling notifications
* on Android Oreo and greater */
public class App extends android.app.Application {
    public static final String CHANNEL_ID = "serviceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // if build version is greater or equal to Android Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
