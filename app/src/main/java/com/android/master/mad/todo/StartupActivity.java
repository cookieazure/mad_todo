package com.android.master.mad.todo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Cookie on 28.05.2016.
 * Startup activity showing a simple splash screen and checking connection to web interface.
 */
public class StartupActivity extends AppCompatActivity implements IAsyncResponse{

    private final String LOG_TAG = StartupActivity.class.getSimpleName();
    private ProgressDialog connectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "Splash screen initialized.");
        checkWebInterfaceAvailability();
    }

    public void evaluateConnection(boolean result){
        Log.d(LOG_TAG, ": evaluateConnection().");
        if(result){
            Log.i(LOG_TAG, "Web interface available.");
            //Intent intent = new Intent(this,LoginActivity.class);
            //startActivity(intent);
        } else{
            Log.i(LOG_TAG, "Web interface not available.");
            Toast.makeText(StartupActivity.this, "The web interface is not available. Only local storage is used.", Toast.LENGTH_LONG).show();
//            Intent intent Intent= new Intent(this, TaskActivity.class);
//            intent.putExtra("WebInterface", false);
//            startActivity(intent);
        }
    }

    private void checkWebInterfaceAvailability(){
        Log.d(LOG_TAG, ": checkWebInterfaceAvailability().");
        if(checkConnection()){
            Log.i(LOG_TAG, "Connection is true, check server connection.");
            connectionDialog = new ProgressDialog(this);
            connectionDialog.setMessage("Checking server connection.");
            connectionDialog.show();
            WebServiceConnection task = new WebServiceConnection(this);
            task.execute();
        }
    }

    private boolean checkConnection(){
        Log.d(LOG_TAG, ": checkConnection().");
        ConnectivityManager conManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conManager.getActiveNetworkInfo();
        Log.d(LOG_TAG, "Connection is: " + (activeNetwork != null) + " : " + activeNetwork.isConnectedOrConnecting());
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private class WebServiceConnection extends AsyncTask<Void, Void, Boolean> implements IAsyncResponse {

        IAsyncResponse delegate;

        public WebServiceConnection(IAsyncResponse delegate){
            this.delegate = delegate;
        }
        @Override
        public void evaluateConnection(boolean result) {
            Log.d(LOG_TAG, ": evaluateConnection().");
            delegate.evaluateConnection(result);
        }

        protected void onPreExecute(){
            Log.d(LOG_TAG, ": onPreExecute().");
        }
        protected void onPostExecute(Boolean result) {
            Log.d(LOG_TAG, ": onPostExecute().");
            connectionDialog.hide();
            evaluateConnection(result);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(LOG_TAG, ": doInBackground().");
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
            return false;
        }


    }
}
