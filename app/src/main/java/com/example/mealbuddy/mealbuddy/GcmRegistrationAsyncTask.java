package com.example.mealbuddy.mealbuddy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.mealbuddy.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by edreichua on 5/13/16.
 */
public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {

    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {

        // Register the device with the server
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://ultra-badge-131020.appspot.com/_ah/api");
            regService = builder.build();
        }

        String message = "";
        try {
            if (gcm == null) {
                // Set the GCM
                gcm = GoogleCloudMessaging.getInstance(context);
            }

            // Register the device with sender ID
            String regId = gcm.register(Globals.SENDER_ID);
            message = "Device registered";
            Log.d("Testing", "Message: " + message);

            // Execute the registration
            regService.register(regId).execute();

            // Initialise the regID in Globals
            Globals.regID = regId;

        } catch (IOException ex) {

            // Print error message if registration fails
            ex.printStackTrace();
            message = "Error connecting device";
        }
        return message;
    }

    @Override
    protected void onPostExecute(String msg) {

        // Print a toast to inform user on registration status
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

        // Insert a log, too
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }
}