package com.chiri.riberappprototype.notification_service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chiri.riberappprototype.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Chiri on 26/11/2015.
 */
public class AlarmSetter extends BroadcastReceiver {
    public static final String ACTION_INIT = "com.chiri.ribera.ACTION_INIT";
    private static final String TAG = "AlarmSetter";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(ACTION_INIT )|| intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

            Log.i(TAG, "Iniciado. " + intent.getAction());

            //Setea la alarma
            Calendar cal = new GregorianCalendar();
            Intent alarmIntent = new Intent(context, NotifsQueryService.class);
            PendingIntent pintent = PendingIntent.getService(context, 0, alarmIntent, 0);
            AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, context.getResources().getInteger(R.integer.check_notifs_ms), pintent);

            Log.i(TAG, "Set " + context.getResources().getInteger(R.integer.check_notifs_ms));

        }
    }
}
