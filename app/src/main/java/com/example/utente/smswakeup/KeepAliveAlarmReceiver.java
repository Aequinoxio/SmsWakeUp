package com.example.utente.smswakeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by utente on 26/06/2016.
 *
 * Classe per impostare un allarme che ogni XX minuti si riattiva. Workaround per evitare che l'app venca cancellata
 * e non riceva più i broadcast.
 * TODO: Da fare se il permesso Broadcast_SMS per il receiver sms non funziona
 */
public class KeepAliveAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
