package com.android.master.mad.todo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Cookie on 28.05.2016.
 * Startup activity showing a simple splash screen and checking connection to web interface.
 */
public class StartupActivity extends AppCompatActivity {

    private final String LOG_TAG = StartupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Splash screen initialized.");

        if(isWebInterfaceAvailable()){
            Log.i(LOG_TAG, "Web interface available.");
            //Intent intent = new Intent(this,LoginActivity.class);
            //startActivity(intent);
        } else{
            Log.i(LOG_TAG, "Web interface not available.");
            Toast.makeText(StartupActivity.this, "The web interface is not available. Only local storage is used.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, TaskActivity.class);
            intent.putExtra("WebInterface", false);
            startActivity(intent);
        }
    }

    private boolean isWebInterfaceAvailable(){
        Log.d(LOG_TAG, "Run isWebInterfaceAvailable().");

        if(checkConnection()){
            try {
                URL url = new URL(getResources().getString(R.string.base_url));

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "Android Application");
                con.setRequestProperty("Connection", "close");
                con.setConnectTimeout(5000);
                con.connect();

                if (con.getResponseCode() == 200) {
                    Log.i(LOG_TAG, ": response code 200.");
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean checkConnection(){
        Log.d(LOG_TAG, "Run checkWebInterface().");

        ConnectivityManager conManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
