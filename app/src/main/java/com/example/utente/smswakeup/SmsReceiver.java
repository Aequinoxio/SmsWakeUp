package com.example.utente.smswakeup;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String address1 = null;
        String msgBody = null;

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    address1 = messages[i].getOriginatingAddress();
                    msgBody = messages[i].getMessageBody();
                }

                // Apro la maschera solo se il messaggio è quello corretto
                if (shouldBeActivated(context, msgBody, address1)) {
                   // activateActionActivity(context, msgBody, address1);
                    activateActionAlarm(context, msgBody, address1);
                }
            }
        }
    }

    private void activateActionAlarm (Context context,String msgBody, String msgNumber){


        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int secsWaitSound=ApplicationSettings.getSecsWaitSound();

                //sharedPreferences.getInt("secsWait",Integer.valueOf(context.getString(R.string.shared_secsWait)));

        // Imposto l'allarme
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SoundAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, ApplicationSettings.alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        secsWaitSound * 1000, alarmIntent);
    }

    // Verifico se devo attivarmi
    private boolean shouldBeActivated(Context context,String msgBody, String msgNumber){
        // Recupero le preferenze per capire come attivare il ringtone
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String msgBody_Shared=sharedPreferences.getString(context.getString(R.string.shared_msgBody),"");
        String msgNumber_Shared=sharedPreferences.getString(context.getString(R.string.shared_msgFromNumber),"");
        Boolean wakeUpOnNumber_Shared=sharedPreferences.getBoolean(context.getString(R.string.shared_wakeUpOnlyFromNumber),true);

        // Sveglia bambocci. Se ricevo questo sms mi attivo indipendentemente dai settings
        if (msgBody.equals(ApplicationSettings.SvegliaBambocci())) {
            return true;
        }

        if (wakeUpOnNumber_Shared){
            if (msgNumber.equals(msgNumber_Shared) && msgBody.equals(msgBody_Shared)){
                return true;
            } else {
                return false;
            }
        } else if (msgBody.equals(msgBody_Shared)){
            return true;
        }

        return false;
    }
}
