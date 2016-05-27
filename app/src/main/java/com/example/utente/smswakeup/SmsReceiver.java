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

    private int notificationID=987;
    private int pendingIntentRequestCode=791;
    private int alarmID=792;

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");
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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int secsWaitSound=sharedPreferences.getInt("secsWait",Integer.valueOf(context.getString(R.string.shared_secsWait)));

        // Imposto l'allarme
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SoundAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        secsWaitSound * 1000, alarmIntent);
    }

//    // Attivo l'ActivityWakeup
//    private void activateActionActivity(Context context,String msgBody, String msgNumber){
//        turnOnPhone(context);
//        turnOnNotification(context);
//
//        Intent open = new Intent(context, WakeUpActivity.class);
//        open.putExtra("com.example.utente.smswakeup.msgBody",msgBody);
//        open.putExtra("com.example.utente.smswakeup.msgSendingNumber",msgNumber);
//        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(open);
//    }

    // Verifico se devo attivarmi
    private boolean shouldBeActivated(Context context,String msgBody, String msgNumber){
        // Recupero le preferenze per capire come attivare il ringtone
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String msgBody_Shared=sharedPreferences.getString(context.getString(R.string.shared_msgBody),"");
        String msgNumber_Shared=sharedPreferences.getString(context.getString(R.string.shared_msgFromNumber),"");
        Boolean wakeUpOnNumber_Shared=sharedPreferences.getBoolean(context.getString(R.string.shared_wakeUpOnlyFromNumber),true);

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

//    // TODO: Non so se funziona. Ho impostato un altro metodo nel onCreate della wakeup activity
//    private void turnOnPhone(Context context){
//        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock((PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "SmsReceiverTag");
//        wl.acquire();
//    }

//    private void turnOnNotification(Context context){
//
//        // playRingTone(context);
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification myNotication;
//        //API level 11
//        Intent intent = new Intent(context, WakeUpActivity.class);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingIntentRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder builder = new Notification.Builder(context);
//
//        builder.setAutoCancel(true);
//        builder.setTicker(context.getString(R.string.notification_Ticker));
//        builder.setContentTitle(context.getString(R.string.notification_Title));
//        builder.setContentText(context.getString(R.string.notification_Text));
//        builder.setSmallIcon(R.drawable.ic_lightbulb_outline_black_18dp);
//        builder.setContentIntent(pendingIntent);
//        builder.setOngoing(true);
//        builder.setSubText(context.getString(R.string.notification_SubText));   //API level 16
//        builder.setNumber(100);
//
//        builder.setSound(Uri.parse(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM).getPath()));
//        builder.setOnlyAlertOnce(false);
//        // builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
//        myNotication= builder.build();
//
//        myNotication.flags |= Notification.FLAG_INSISTENT;
//        myNotication.flags |= Notification.VISIBILITY_PUBLIC;
//
//        manager.notify(notificationID, myNotication);
//
//                /*
//                //API level 8
//                Notification myNotification8 = new Notification(R.drawable.ic_launcher, "this is ticker text 8", System.currentTimeMillis());
//
//                Intent intent2 = new Intent(MainActivity.this, SecActivity.class);
//                PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 2, intent2, 0);
//                myNotification8.setLatestEventInfo(getApplicationContext(), "API level 8", "this is api 8 msg", pendingIntent2);
//                manager.notify(11, myNotification8);
//                */
//    }

//    // TODO: BRUTTO SOLO PER TEST COPIATO DA WAKEUPACTIVITY
//    private void playRingTone(Context context){
//
//        AudioManager audioManager;
//        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setMode(AudioManager.MODE_IN_CALL);
//        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//        audioManager.setSpeakerphoneOn(true);
//        //audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_SHOW_UI);
//        audioManager.setStreamVolume(
//                AudioManager.STREAM_RING,
//                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
//                AudioManager.FLAG_PLAY_SOUND
//        );
//
//        //(AudioManager.STREAM_RING,AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_ALLOW_RINGER_MODES);
//
////        Ringtone ringtone;
////
////        try {
////            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
////            ringtone = RingtoneManager.getRingtone(context, notification);
////            //          ringtone.setAudioAttributes();
////            ringtone.play();
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }

}
