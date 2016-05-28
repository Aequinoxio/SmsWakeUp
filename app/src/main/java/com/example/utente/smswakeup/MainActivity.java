package com.example.utente.smswakeup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String msgBody;
    String msgFromNumber;
    boolean wakeUpOnlyFromNumber;
    int  secsWaitSound;  // secondi di attesa prima di suonare il ringtone

    private int SETTINGS_RESULTCODE=9876;

    NumberPicker np;

    // handler for received Intents for the "AggiornaInterfaccia" event
    private BroadcastReceiver mMessageFromServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Aggiorno l'interfaccia
            updateUI();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SETTINGS_RESULTCODE)
        {
            // Ricarico le preferences e aggiorno l'interfaccia
            loadSharedPreferences();
            updateUI();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSharedPreferences();

        updateUI();
    }

    // TODO: Vedere se è utile disabilitarlo. Per ora non va, lo status è 0 e nessuna delle condizioni è soddisfatta
    public void registerDeregisterReceiver(View v){
        Context context= v.getContext();
        ComponentName component = new ComponentName(context, SmsReceiver.class);

        int status = context.getPackageManager().getComponentEnabledSetting(component);
        if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            //Disable
            context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
        } else if(status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            //Enable
            context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            String s = getString(R.string.app_name) +" - Ver. " + BuildConfig.VERSION_NAME ;
            s+="\nby "+ getString(R.string.Autore);
            s+="\n\n"+getString(R.string.descrizione);
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.action_about)
                    .setMessage(s)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
            return true;
        }

        if (id==R.id.action_settings){
            Intent intentSettings= new Intent(getApplicationContext(),SimpleSettingsActivity.class);
            startActivityForResult(intentSettings, SETTINGS_RESULTCODE);
        }

        return super.onOptionsItemSelected(item);
    }


    // Salvo tutto nelle shared di default
    private void saveSharedPreferences(){
        ApplicationSettings.setAllValues(getApplicationContext(), msgBody, msgFromNumber, wakeUpOnlyFromNumber, secsWaitSound);
    }

    // Recupero lo stato dalle shared di default
    private void loadSharedPreferences(){
        ApplicationSettings.loadSharedPreferences(getApplicationContext());
        msgBody=ApplicationSettings.getMsgBody();
        msgFromNumber=ApplicationSettings.getMsgFromNumber();
        wakeUpOnlyFromNumber=ApplicationSettings.getWakeUpOnlyFromNumber();
        secsWaitSound=ApplicationSettings.getSecsWaitSound();
    }

    private void updateUI(){
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(msgBody);

        textView = (TextView) findViewById(R.id.textView3);
        textView.setText(msgFromNumber);

        textView = (TextView) findViewById(R.id.txtSecsWait);
        textView.setText(String.valueOf(secsWaitSound));

        textView = (TextView) findViewById(R.id.txtLockNumber);
        textView.setText(
                (wakeUpOnlyFromNumber)?getString(R.string.Si):getString(R.string.No)
        );

//        if (wakeUpOnlyFromNumber){
//            relativeLayout.setVisibility(View.VISIBLE);
//        }else {
//            relativeLayout.setVisibility(View.INVISIBLE);
//            Toast toast = Toast.makeText(this.getApplicationContext(),
//                    getString(R.string.warning_numberLock),
//                    Toast.LENGTH_LONG
//            );
//            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//            if( v != null) v.setGravity(Gravity.CENTER);
//            toast.show();
//        }

        Button button = (Button) findViewById(R.id.btnStopSoundService);

        if (ApplicationSettings.isServiceRunning() ){
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

//    public void hideKeyboard(View view) {
//        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }

    public void btnStopSoundService(View view){
        stopService(new Intent(getApplicationContext(),SoundService.class));
        Button button = (Button) findViewById(R.id.btnStopSoundService);
        button.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registro il receiver locale
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageFromServiceReceiver,
                new IntentFilter(this.getString(R.string.intent_AggiornaInterfaccia)));

        updateUI();
    }
}
