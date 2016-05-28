package com.example.utente.smswakeup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String msgBody;
    String msgFromNumber;
    boolean wakeUpOnlyFromNumber;
    int  secsWaitSound;  // secondi di attesa prima di suonare il ringtone

    private int SETTINGS_RESULTCODE=9876;

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

        MenuItem settingsMenuItem = menu.findItem(R.id.action_svegliaBambocci);
        SpannableString s = new SpannableString(settingsMenuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.CYAN), 0, s.length(), 0);
        s.setSpan(new BackgroundColorSpan(Color.RED), 0, s.length(), 0);
        settingsMenuItem.setTitle(s);


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

        if (id==R.id.action_svegliaBambocci){
            svegliaBambocci();
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

    // Sveglio un amico indipendentemente dai suoi settings
    public void btnSvegliaBambocci (View view){

        svegliaBambocci();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.msgTitleSvegliaBambocci));
//
//// Set up the input
//        final EditText input = new EditText(this);
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_PHONE);
//        builder.setView(input);
//
//// Set up the buttons
//        builder.setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String mBamboNumber = input.getText().toString();
//                inviaBamboSms(mBamboNumber);
//            }
//        });
//        builder.setNegativeButton(getString(R.string.dialog_Cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
    }

    private void svegliaBambocci(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.msgTitleSvegliaBambocci));

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mBamboNumber = input.getText().toString();
                inviaBamboSms(mBamboNumber);
            }
        });
        builder.setNegativeButton(getString(R.string.dialog_Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostro immediatamente la softkeyboard
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();

//        input.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    /**
     * Invio un sms per vegliare il destinatario senza che venga applicato alcun filtro
     *
     * @param mBamboNumber Numero del destinatario da svegliare
     */
    private void inviaBamboSms(String mBamboNumber){

        // Filtri per gli intent Sent e Delivered
        String SENT = "com.example.utente.smswakeup.SMS_SENT";
        String DELIVERED = "com.example.utente.smswakeup.SMS_DELIVERED";

        // Request code
        int SMS_SENT_BROADCAST_CODE=1101;
        int SMS_DELI_BROADCAST_CODE=1102;

        // Prepato i broadcast per l'invio degli sms
        PendingIntent sentPI = PendingIntent.getBroadcast(this, SMS_SENT_BROADCAST_CODE,
                new Intent(SENT), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, SMS_DELI_BROADCAST_CODE,
                new Intent(DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_sent),
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_genericError),
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_noService),
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_noPDU),
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_noRadio),
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_delivered),
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), getString(R.string.sms_notDelivered),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(mBamboNumber, null, ApplicationSettings.SvegliaBambocci(), sentPI, deliveredPI);

    }
}
