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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import timber.log.Timber;

public class SmsReceiver extends BroadcastReceiver {

    public SmsReceiver() {
       // Timber.tag("SmsReceiver");
        Timber.tag("SmsReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Timber.tag("SmsReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        String address1 = null;
        String msgBody = null;

        Bundle bundle = intent.getExtras();

        String action = intent.getAction();

        if (action==null) action="";
        Timber.tag("SmsReceiverAction");
//        Log.d("SmsReceived", action);
//        Log.d("SmsReceived-const",Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
//        Log.d("SmsReceived-const", Intent.ACTION_BOOT_COMPLETED);
        Timber.e("SmsReceived-const1: " + String.valueOf(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        Timber.e("SmsReceived-const2: " + String.valueOf(Intent.ACTION_BOOT_COMPLETED));
        Timber.e("SmsReceived-action: " + action);

        // Al boot imposto un allarme che si autorilancia ogni XX minuti
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            PollingAlarmReceiver.stopAlarm(context);
            PollingAlarmReceiver.startAlarm(context);
        }

        if (action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            Timber.e("SmsReceived: Action processing SMS");

            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                Timber.tag("SmsReceiverAction");
                Timber.e("SmsReceived: Bundle is NOT null");

//            /// DEBUG
//            for (String key : bundle.keySet()) {
//                Object value = bundle.get(key);
//                Log.d("**** PDU ****", String.format("%s %s (%s)", key,
//                        value.toString(), value.getClass().getName()));
//            }
//            /// DEBUG

                if (pdus != null) {
                    Timber.tag("SmsReceiverAction");
                    Timber.e("SmsReceived: PDU is NOT null");

                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = bundle.getString("format");
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                            Timber.tag("SmsReceiverAction");
                            Timber.e("Format: "+format);
                        }
                        else {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }

                        // messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        address1 = messages[i].getOriginatingAddress();
                        msgBody = messages[i].getMessageBody();
                        Timber.tag("SmsReceiverAction");
                        Timber.e("SMS From: "+address1+ "\nSMS Msg:"+ msgBody+"\n");
                    }

                    // Apro la maschera solo se il messaggio è quello corretto
                    if (shouldBeActivated(context, msgBody, address1)) {
                        // activateActionActivity(context, msgBody, address1);
                        activateActionAlarm(context, msgBody, address1);
                    }
                }
            }
        } else{  // Reimposto l'allarme per ogni action ricevuta
            PollingAlarmReceiver.stopAlarm(context);
            PollingAlarmReceiver.startAlarm(context);
        }
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        Timber.tag("SmsReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        return super.peekService(myContext, service);
    }

    private void activateActionAlarm (Context context, String msgBody, String msgNumber){
        Timber.tag("SmsReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

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
