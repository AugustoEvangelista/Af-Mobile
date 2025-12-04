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

        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "Lembretes de Medicamentos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            ch.enableVibration(true);
            nm.createNotificationChannel(ch);
        }

        // Abrir o app ao tocar na notificação
        Intent it = new Intent(context, MainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pi = PendingIntent.getActivity(
                context,
                id.hashCode(),
                it,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder nb =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(context.getString(R.string.take_medicine))
                        .setContentText(name)
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .setAutoCancel(true)
                        .setContentIntent(pi)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[]{200, 200, 200});

        nm.notify(id.hashCode(), nb.build());
    }
}
