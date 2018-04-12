package com.bonlai.socialdiningapp.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.bonlai.socialdiningapp.R;
import com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity;

import static com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity.FROM_NOTIFICATION;
import static com.bonlai.socialdiningapp.detail.gathering.GatheringDetailActivity.GATHERING_ID;

/**
 * Created by Bon Lai on 11/4/2018.
 */

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int id = intent.getIntExtra("id", 0);
        String datetime=intent.getStringExtra("datetime");

        if (id != 0) {
            sendNotify(context, id,datetime);
        }
    }

    private void sendNotify(Context context, int id, String datetime) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("A gathering is about to start!");
        builder.setContentText("Remember to arrive at " +datetime);
        builder.setSmallIcon((R.drawable.ic_all_gathering));
        Intent notifyIntent = new Intent(context, GatheringDetailActivity.class);
        notifyIntent.putExtra(GATHERING_ID, id);
        notifyIntent.putExtra(FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, builder.build());
    }
}