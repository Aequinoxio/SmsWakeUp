package com.example.utente.smswakeup;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class WakeUpActivity extends AppCompatActivity {

    // Ritardo di un secondo l'attivazione del suono per evitare sovrapposizioni con quello di ricezione sms
    private Handler mHandler = new Handler();
    private Runnable runnable;
    private long delay;

    Ringtone ringtone;
    MediaPlayer mMediaPlayer= new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        delay=1000*sharedPreferences.getInt("secsWait",Integer.valueOf(getString(R.string.shared_secsWait)));

        // Aggiorno l'UI
        String sBody = getIntent().getStringExtra("com.example.utente.smswakeup.msgBody");
        String sNumber = getIntent().getStringExtra("com.example.utente.smswakeup.msgSendingNumber");
        // Se non ci sono i parametri nell'intent allora non sono stato chiamato dall'SmsReceiver e quindi esco subito
        if (sBody==null)
            return;

        TextView textView  = (TextView) findViewById(R.id.txtSendingNumber);
        textView.setText(sNumber);
        textView  = (TextView) findViewById(R.id.txtMsgBody);
        textView.setText(sBody);
        textView = (TextView) findViewById(R.id.textView8);
        textView.setText(String.valueOf(delay/1000));

        Button button = (Button) findViewById(R.id.btnStopSoundExit);
        button.setEnabled(false);

        // Ritardo il ringtone
        runnable= new Runnable() {
            @Override
            public void run() {
                Button button = (Button) findViewById(R.id.btnStopSoundExit);
                button.setEnabled(true);
                playRingTone();
            }
        };
        mHandler.postDelayed(runnable, delay);
    }

    private void playRingTone(){

        AudioManager audioManager;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        //audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_SHOW_UI);
        audioManager.setStreamVolume(
                AudioManager.STREAM_RING,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                AudioManager.FLAG_PLAY_SOUND
        );

        //(AudioManager.STREAM_RING,AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_ALLOW_RINGER_MODES);

//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            //          ringtone.setAudioAttributes();
//            ringtone.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            Uri alert =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer.setDataSource(this, alert);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSound(View v){
//        if (ringtone!= null && ringtone.isPlaying()) {
//            ringtone.stop();
//        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        this.finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //Log.d("OnPause","ok");
        // Se sto per uscire dall'activity rimuovo la callback per il suono
        stopSound(null); // Non uso la View
        mHandler.removeCallbacks(runnable);
    }
    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("OnStop", "onStop");
    }
}
