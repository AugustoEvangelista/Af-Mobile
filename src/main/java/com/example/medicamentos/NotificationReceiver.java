package com.example.medicamentos;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.core.app.NotificationCompat;
public class NotificationReceiver extends BroadcastReceiver {


    private static final String CHANNEL_ID = "med_channel";


    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getStringExtra("name");
        String id = intent.getStringExtra("id");


        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Med Reminders", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(ch);
        }


        Intent it = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder nb = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.getString(R.string.take_medicine))
                .setContentText(name)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pi)
                .setAutoCancel(true);


        nm.notify(id.hashCode(), nb.build());
    }
}