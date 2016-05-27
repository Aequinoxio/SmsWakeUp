package com.example.utente.smswakeup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity {

    String msgBody;
    String msgFromNumber;
    boolean wakeUpOnlyFromNumber;
    int  secsWaitSound;  // Millisecondi di attesa prima di suonare il ringtone

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSharedPreferences();

        EditText editText = (EditText)findViewById(R.id.editText);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    EditText editText = (EditText) findViewById(R.id.editText);
                    TextView textView = (TextView) findViewById(R.id.textView);
                    Button button = (Button) findViewById(R.id.btnAcceptMsgBody);

                    editText.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);

                }
            }
        });

        editText = (EditText)findViewById(R.id.editText2);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                    EditText editText = (EditText) findViewById(R.id.editText2);
                    TextView textView = (TextView) findViewById(R.id.textView3);
                    Button button = (Button) findViewById(R.id.btnAcceptNumberBody);

                    editText.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);

                }
            }
        });

        np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(3);
        np.setMaxValue(10);

        np.setWrapSelectorWheel(false);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                secsWaitSound=newVal;
                saveSharedPreferences();
            }
        });

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

        return super.onOptionsItemSelected(item);
    }

    public void cboxMsgLockToggle(View view){
        CheckBox checkBox = (CheckBox) findViewById(R.id.cboxLockNumber);
        wakeUpOnlyFromNumber=checkBox.isChecked();
        saveSharedPreferences();

        updateUI();
    }

//    public void txtChangeMsgWakeUp(View view){
//        Context context=view.getContext();
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Title");
//        // I'm using fragment here so I'm using getView() to provide ViewGroup
//        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
//        View viewInflated = LayoutInflater.from(context).inflate(R.layout.text_inpu_password, (ViewGroup) getView(), false);
//        // Set up the input
//        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        builder.setView(viewInflated);
//
//        // Set up the buttons
//        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                m_Text = input.getText().toString();
//            }
//        });
//        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }

    public void txtChangeMsgWakeUp(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView textView = (TextView) findViewById(R.id.textView);
        Button button = (Button) findViewById(R.id.btnAcceptMsgBody);

        editText.setText(msgBody);

        editText.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        editText.requestFocus();
    }

    public void btnChangeMsgWakeUp(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView textView = (TextView) findViewById(R.id.textView);
        Button button = (Button) findViewById(R.id.btnAcceptMsgBody);

        String s = editText.getText().toString();

        editText.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(s);
        msgBody=s;
        saveSharedPreferences();
    }

    public void txtChangeNumberWakeUp(View view){
        EditText editText = (EditText) findViewById(R.id.editText2);
        TextView textView = (TextView) findViewById(R.id.textView3);
        Button button = (Button) findViewById(R.id.btnAcceptNumberBody);

        editText.setText(msgFromNumber);

        editText.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        editText.requestFocus();
    }

    public void btnChangeNumberWakeUp(View view){
        EditText editText = (EditText) findViewById(R.id.editText2);
        TextView textView = (TextView) findViewById(R.id.textView3);
        Button button = (Button) findViewById(R.id.btnAcceptNumberBody);

        String s = editText.getText().toString();

        editText.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(s);
        msgFromNumber=s;
        saveSharedPreferences();
    }

    // Salvo tutto nelle shared di default
    private void saveSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("msgBody",msgBody);
        editor.putString("msgFromNumber",msgFromNumber);
        editor.putBoolean("wakeUpOnlyFromNumber",wakeUpOnlyFromNumber);
        editor.putInt("secsWait",secsWaitSound);
        editor.apply();

    }

    // Recupero lo stato dalle shared di default
    private void loadSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        msgBody= sharedPreferences.getString("msgBody",getString(R.string.msgWakeUp));
        msgFromNumber=sharedPreferences.getString("msgFromNumber",getString(R.string.msgFromNumber));
        wakeUpOnlyFromNumber=sharedPreferences.getBoolean("wakeUpOnlyFromNumber",false);
        secsWaitSound=sharedPreferences.getInt("secsWait",Integer.valueOf(getString(R.string.shared_secsWait)));
    }

    private void updateUI(){
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(msgBody);

        textView = (TextView) findViewById(R.id.textView3);
        textView.setText(msgFromNumber);

        np.setValue(secsWaitSound);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout2);

        CheckBox checkBox = (CheckBox)findViewById(R.id.cboxLockNumber);

        checkBox.setChecked(wakeUpOnlyFromNumber);

        if (wakeUpOnlyFromNumber){
            relativeLayout.setVisibility(View.VISIBLE);
        }else {
            relativeLayout.setVisibility(View.INVISIBLE);
//            Toast toast = Toast.makeText(this.getApplicationContext(),
//                    getString(R.string.warning_numberLock),
//                    Toast.LENGTH_LONG
//            );
//            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//            if( v != null) v.setGravity(Gravity.CENTER);
//            toast.show();
        }

        Button button = (Button) findViewById(R.id.btnStopSoundService);

        if (ApplicationSettings.isServiceRunning()){
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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
                new IntentFilter("AggiornaInterfaccia"));

        updateUI();
    }
}
