package com.android.master.mad.todo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
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
        } else{
            Log.i(LOG_TAG, "Web interface not available.");
            Toast.makeText(StartupActivity.this, "The web interface is not available. Only local storage is used.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isWebInterfaceAvailable(){
        Log.d(LOG_TAG, "Run isWebInterfaceAvailable().");
        checkWebInterface();
        return false;
    }

    private void checkWebInterface(){
        Log.d(LOG_TAG, "Run checkWebInterface().");
    }
}
