package com.example.room8.ui.todolist.todomvp3.todolistactivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

//import com.example.room8.ui.notifications.todomvp3.R;
import com.example.room8.R;

public class AlarmNotification extends BroadcastReceiver {

    String ALARM_CHANNEL_ID = "alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder alarm_builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("TITLE")
                .setContentText("Due today: " + intent.getStringExtra("todoTitle"))
                .setChannelId(ALARM_CHANNEL_ID);

        String todoTitle = intent.getStringExtra("todoTitle");
        Intent resultIntent = new Intent(context, ToDoListActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ToDoListActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm_builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, alarm_builder.build());
        Log.d("ALARM", "TRIGGERED");

        Toast.makeText(context, "Due today: " + todoTitle, Toast.LENGTH_LONG).show();
    }
}
