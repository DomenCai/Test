package com.domencai.puzzle.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.domencai.puzzle.R;

/**
 * Created by Domen、on 2018/4/2.
 */

public class NotificationActivity extends AppCompatActivity {

    private static final String ACTION_NOTIFICATION_CLICKED = "CLICKED";
    private static final int NOTIFICATIONS_ID = 0x101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        registerReceiver(new TestBroadcast(), new IntentFilter(ACTION_NOTIFICATION_CLICKED));
    }

    public void sendNormal(View view) {
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this,
                NOTIFICATIONS_ID,
                new Intent(ACTION_NOTIFICATION_CLICKED),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setContentIntent(contentIntent);

        assert mNotifyMgr != null;
        mNotifyMgr.notify(NOTIFICATIONS_ID, mBuilder.build());
    }

    private class TestBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "点击通知", Toast.LENGTH_SHORT).show();
        }
    }
}
