package com.android.master.mad.todo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Startup activity showing a simple splash screen and checking connection to web interface.
 */
public class StartupActivity extends AppCompatActivity {

    private final String LOG_TAG = StartupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Splash screen initialized.");


    }
}
