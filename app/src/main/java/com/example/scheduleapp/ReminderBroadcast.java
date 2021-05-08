package com.example.scheduleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    ListDate listDate = ListDate.getInstance();
    final Handler mHandler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myNotify")
                .setSmallIcon(R.drawable.logo_round)
                .setContentTitle("Todo")
                .setContentText("You have an on-going activity!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() { listDate.setAlarmForNextActivity(); }
        }, 1000 * 60);

    }
}
