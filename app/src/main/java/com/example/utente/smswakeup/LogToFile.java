package com.example.utente.smswakeup;

/**
 * Created by utente on 16/06/2016.
 */

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Not a real crash reporting library! */
public final class LogToFile {
    public static void log(int priority, String tag, String message) {
        // TODO add log entry to circular buffer.
        salvaDati(String.valueOf(priority)+ " - " + tag +" - " + message);
    }

    public static void logWarning(Throwable t) {
        // TODO report non-fatal warning.
    }

    public static void logError(Throwable t) {
        // TODO report non-fatal error.
    }

    private LogToFile() {
        throw new AssertionError("No instances.");
    }

    private static void salvaDati(String stringa) {
        if (!ApplicationSettings.isLoggingActivated()){
            return;
        }

        File dataFile = ApplicationSettings.getFileSalvataggio();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dataFile.getAbsolutePath(), true);
        } catch (FileNotFoundException fnfe) {
            // Non dovrebbe mai essere lanciata a meno di cose strane durante l'esecuzione del servizio es. cancellazione file
            // tramite gestione file e da root
            fnfe.printStackTrace();
        }

        try {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ITALY);
            fileOutputStream.write(simpleDateFormat.format(date).getBytes());
            fileOutputStream.write(" - ".getBytes());
            fileOutputStream.write(stringa.getBytes());
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.close();
        } catch (NullPointerException e) {
            // Non dovrei mai arrivarci a meno di cose strane fatte sul telefono come root
            e.printStackTrace();
        } catch (IOException ioe){
            // Non dovrei mai arrivarci a meno di cose strane fatte sul telefono come root
            ioe.printStackTrace();
        }
        finally {

        }
    }
}
