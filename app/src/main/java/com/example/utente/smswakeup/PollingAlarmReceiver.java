package com.example.utente.smswakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import timber.log.Timber;

/**
 * Created by utente on 01/07/2016.
 */
public class PollingAlarmReceiver extends BroadcastReceiver {

    public static void startAlarm(Context context){
        Timber.tag("PollingAlarmReceiver");
        Timber.e(PollingAlarmReceiver.class.getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        resendAlarm(context);
    }

    public static void stopAlarm(Context context){
        Timber.tag("PollingAlarmReceiver");
        Timber.e(PollingAlarmReceiver.class.getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        Intent intent = new Intent(context,PollingAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ApplicationSettings.pollingID,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
    }

    private static void resendAlarm(Context context){
        Timber.tag("PollingAlarmReceiver");
        Timber.e(PollingAlarmReceiver.class.getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        Intent intent = new Intent(context,PollingAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ApplicationSettings.pollingID,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ApplicationSettings.pollingTime, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Timber.tag("PollingAlarmReceiver");
//        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        resendAlarm(context);
    }
}
