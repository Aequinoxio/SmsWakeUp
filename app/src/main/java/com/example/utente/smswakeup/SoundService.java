package com.example.utente.smswakeup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class SoundService extends Service {

    MediaPlayer mMediaPlayer= new MediaPlayer();
    private int notificationID=987;
    private int pendingIntentRequestCode=791;
    Notification myNotication;


    public SoundService() {
    }

    private void aggiornaMainActivity(){
        Intent intent = new Intent("AggiornaInterfaccia");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        stopSound();
        ApplicationSettings.setServiceStopped();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);

        startSound();
        ApplicationSettings.setServiceRunning();

        aggiornaMainActivity();

        return Service.START_STICKY;
    }

        @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return null;
            //throw new UnsupportedOperationException("Not yet implemented");

    }

    private void startSound(){

        setNotification(getApplicationContext());
        playRingTone(getApplicationContext());
        startForeground(notificationID,myNotication);
    }

    private void stopSound(){

        stopRingTone();
        stopForeground(true);
    }


    private void setNotification(Context context){

        // playRingTone(context);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //API level 11
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingIntentRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        manager.notify(notificationID, myNotication);
    }

    private void playRingTone(Context context){

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
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
    }

}
