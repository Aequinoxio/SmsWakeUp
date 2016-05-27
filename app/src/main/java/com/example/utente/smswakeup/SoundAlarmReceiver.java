package com.example.utente.smswakeup;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

public class SoundAlarmReceiver extends BroadcastReceiver {

    // MediaPlayer mMediaPlayer= new MediaPlayer();

    public SoundAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // playRingTone(context);

        context.startService(new Intent(context, SoundService.class));
    }
}
