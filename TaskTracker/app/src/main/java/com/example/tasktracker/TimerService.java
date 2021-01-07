package com.example.tasktracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.tasktracker.App.CHANNEL_ID;

public class TimerService extends android.app.Service {

    private int id = 1; // id to identify which notification, only need one value for one notification

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get values for task info from intent
        String name = intent.getStringExtra("taskName");
        String time = intent.getStringExtra("timerExtra");

        // intent for opening main Tasks activity when clicking on notification
        Intent openTasks = new Intent(this, MainActivity.class);
        // PendingIntent allows other applications to handle doing this, I believe
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, openTasks, 0);

        // build notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(name)
                    .setContentText(time)
                    .setSmallIcon(R.drawable.ic_time)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent)
                .build();

        // start the service and pass the notification
        startForeground(id, notification);

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
