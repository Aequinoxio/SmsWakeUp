package com.example.utente.smswakeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import timber.log.Timber;

public class SoundService extends Service {

    MediaPlayer mMediaPlayer= new MediaPlayer();
    Notification myNotication;


    public SoundService() {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

    }

    private void aggiornaMainActivity(){
        Intent intent = new Intent(this.getString(R.string.intent_AggiornaInterfaccia));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate(){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        stopSound();
        ApplicationSettings.setServiceStopped();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        super.onStartCommand(intent,flags,startId);

        startSound();
        ApplicationSettings.setServiceRunning();

        aggiornaMainActivity();

        return Service.START_STICKY;
    }

        @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

       return null;
            //throw new UnsupportedOperationException("Not yet implemented");

    }

    private void startSound(){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        setNotification(getApplicationContext());
        playRingTone(getApplicationContext());
        startForeground(ApplicationSettings.notificationID,myNotication);
    }

    private void stopSound(){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        stopRingTone();
        stopForeground(true);
    }


    private void setNotification(Context context){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        // playRingTone(context);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //API level 11
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,ApplicationSettings.pendingIntentRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setAutoCancel(true);
        builder.setTicker(context.getString(R.string.notification_Ticker));
        builder.setContentTitle(context.getString(R.string.notification_Title));
        builder.setContentText(context.getString(R.string.notification_Text));
        builder.setSmallIcon(R.drawable.ic_lightbulb_outline_black_18dp);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setSubText(context.getString(R.string.notification_SubText));   //API level 16
        builder.setNumber(100);

        builder.setSound(Uri.parse(RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM).getPath()));
        builder.setOnlyAlertOnce(false);
        // builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        myNotication= builder.build();

        myNotication.flags |= Notification.FLAG_INSISTENT;
        myNotication.flags |= Notification.VISIBILITY_PUBLIC;

        manager.notify(ApplicationSettings.notificationID, myNotication);
    }

    private void playRingTone(Context context){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        AudioManager audioManager;

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        //audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(
                AudioManager.STREAM_RING,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                AudioManager.FLAG_PLAY_SOUND
        );

        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer.setDataSource(context, alert);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRingTone(){
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }
/////////////////////////////


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onLowMemory();
    }

    @Override
    public void onRebind(Intent intent) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onRebind(intent);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onStart(intent, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTrimMemory(int level) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Timber.tag("SoundService");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
        return super.onUnbind(intent);
    }
}
