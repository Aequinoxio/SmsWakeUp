package com.example.utente.smswakeup;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.location.Location;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by utente on 27/05/2016.
 */
public class ApplicationSettings {
    protected static int notificationID=987;
    protected static int pendingIntentRequestCode=791;
    protected static int alarmID=792;

    // Riavvio allarme per tenere sveglia l'applicazione in memoria
    protected static int pollingID=793;
    protected static final String pollingAction="com.example.utente.smswakeup.PollingAlarmReceiver";
    protected static final int pollingTime=60*5*1000; // 5 minuti

    protected static Context mContext;

    protected static String msgBody;
    protected static String msgFromNumber;
    protected static Boolean wakeUpOnlyFromNumber;
    protected static int secsWaitSound;
    protected static boolean logActionsToFile=false;

    private static File fileSalvataggio=null;

    private static ApplicationSettings ourInstance = new ApplicationSettings();

    // Se ricevo un sms di questo tipo mi sveglio comunque indipendentemente dai settings
    private static final String SvegliaBambocci="a7be9537881739aa2eaa0c120f7871b97e9b53b58145c72b4532e4fe9852d94f4fc9dec656e69d8be504510ddc546fc0384441558fdd6ca0d2de63a1ce337d0e";

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
        logActionsToFile=sharedPreferences.getBoolean("logActionsToFile",false);

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

    public static boolean isLoggingActivated(){
        return logActionsToFile;
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

    public static String SvegliaBambocci(){ return SvegliaBambocci;}


    public static void setFileSalvataggio(Context context){
        // TODO: Costante estensione cablata
        String filenameLog="logfileSmsWakeUp.txt";
        // TODO: rendere il file parametrico per es. a livello giornaliero o far scegliere all'utente
        // fileSalvataggio = new File(context.getFilesDir(), "pippo.txt");
        // Creo una dir sulla SD
        File sd = new File(Environment.getExternalStorageDirectory() + "/SmsWakeUp");

        // Provo a creare la directory sulla sd
        boolean successCreaDir = true;
        if (!sd.exists()) {
            successCreaDir = sd.mkdir();
        }

        // Se non riesco a creare la directory metto tutto nella subdir dell'App
        if (!successCreaDir)
            fileSalvataggio = new File(context.getFilesDir(), filenameLog);
        else
            fileSalvataggio = new File(sd,filenameLog);

        if (!fileSalvataggio.exists()) {
            try {
                fileSalvataggio.createNewFile();
            } catch (IOException ioe) {
                Log.e("Errore", "Creazione file - " + fileSalvataggio.getAbsolutePath() + " - non riuscita");
            }
        }
        fileSalvataggio.setReadable(true,false);
    }

    public static File getFileSalvataggio(){
    /* TODO: Workaround per evitare un nullpointer exception quanto adnroid, in sovraccarico e con poca memoria,
        dealloca e reinizializza il singleton(!!!)
     */
        if (fileSalvataggio==null)
            setFileSalvataggio(SmsWakeUpApplication.getMyContext());

        return fileSalvataggio;
    }
}
