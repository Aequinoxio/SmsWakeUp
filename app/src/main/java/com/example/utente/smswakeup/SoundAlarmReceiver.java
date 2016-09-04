package com.example.utente.smswakeup;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import timber.log.Timber;

public class SoundAlarmReceiver extends BroadcastReceiver {

    public SoundAlarmReceiver() {
        Timber.tag("SoundAlarmReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.tag("SoundAlarmReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        context.startService(new Intent(context, SoundService.class));
    }

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        Timber.tag("SoundAlarmReceiver");
        Timber.e(this.getClass().getSimpleName()+" - "+Thread.currentThread().getStackTrace()[2].getMethodName());

        return super.peekService(myContext, service);
    }
}
