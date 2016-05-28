package com.example.utente.smswakeup;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.preference.PreferenceManager;

/**
 * Created by utente on 27/05/2016.
 */
public class ApplicationSettings {
    protected static int notificationID=987;
    protected static int pendingIntentRequestCode=791;
    protected static int alarmID=792;

    protected static String msgBody;
    protected static String msgFromNumber;
    protected static Boolean wakeUpOnlyFromNumber;
    protected static int secsWaitSound;

    private static ApplicationSettings ourInstance = new ApplicationSettings();

    private static boolean isServiceRunning=false;

    public static ApplicationSettings getInstance() {
        return ourInstance;
    }

    private ApplicationSettings() {

    }

    public static void setServiceRunning(){
        isServiceRunning=true;
    }
    public static void setServiceStopped(){
        isServiceRunning=false;
    }

    public static boolean isServiceRunning(){
        return isServiceRunning;
    }

    public static void loadSharedPreferences(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        msgBody= sharedPreferences.getString("msgBody",context.getString(R.string.msgWakeUp));
        msgFromNumber=sharedPreferences.getString("msgFromNumber",context.getString(R.string.msgFromNumber));
        wakeUpOnlyFromNumber=sharedPreferences.getBoolean("wakeUpOnlyFromNumber",false);

        secsWaitSound = Integer.valueOf(
                sharedPreferences.getString("secsWait",context.getString(R.string.shared_secsWait))
        );

        // Check se è la prima esecuzione
        boolean firstRun = sharedPreferences.getBoolean("firstRun", true);
        // Alla prima esecuzione salvo tutto
        if (firstRun){
            sharedPreferences.edit().putBoolean("firstRun", false).commit();
            saveSharedPreferences(context);
        }

    }

    public static void saveSharedPreferences(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("msgBody",msgBody);
        editor.putString("msgFromNumber",msgFromNumber);
        editor.putBoolean("wakeUpOnlyFromNumber",wakeUpOnlyFromNumber);
        editor.putString("secsWait",String.valueOf(secsWaitSound));
        editor.apply();
    }

    /**
     * Imposto tutti i parametri in un colpo solo. Se un parametro è null non lo modifico
     * Alla fine salvo tutto nelle shared preferences
     *
     * @param msgBodyT
     * @param msgFromNumberT
     * @param wakeUpOnlyFromNumberT
     * @param secsWaitSoundT
     */
    public static void setAllValues(Context context, String msgBodyT, String msgFromNumberT, Boolean wakeUpOnlyFromNumberT, Integer secsWaitSoundT){
        if (msgBodyT!=null)
            msgBody=msgBodyT;
        if (msgFromNumberT!=null)
            msgFromNumber=msgFromNumberT;
        if (wakeUpOnlyFromNumberT!=null)
            wakeUpOnlyFromNumber=wakeUpOnlyFromNumberT;
        if (secsWaitSoundT!=null)
            secsWaitSound=secsWaitSoundT;

        saveSharedPreferences(context);
    }

    // Ritornai valori caricati dalle shared prefs
    public static String getMsgBody(){return msgBody;}
    public static String getMsgFromNumber(){return msgFromNumber;}
    public static boolean getWakeUpOnlyFromNumber(){return wakeUpOnlyFromNumber;}
    public static int getSecsWaitSound(){return secsWaitSound;}

    // Ritorna le chiavi utilizzate nelle shared prefs
    public static String getMsgBodyKey(){return "msgBody";}
    public static String getMsgFromNumberKey(){return "msgFromNumber";}
    public static String getWakeUpOnlyFromNumber_Key(){return "wakeUpOnlyFromNumber";}
    public static String getSecsWaitSoundKey(){return "secsWait";}

}
