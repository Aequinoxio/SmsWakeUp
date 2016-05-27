package com.example.utente.smswakeup;

/**
 * Created by utente on 27/05/2016.
 */
public class ApplicationSettings {
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
}
