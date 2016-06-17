package com.android.master.mad.todo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.helper.RetrofitServiceGenerator;
import com.android.master.mad.todo.helper.TaskSQLiteOperationService;
import com.android.master.mad.todo.helper.Utility;
import com.android.master.mad.todo.sync.IAsyncConnectionResponse;
import com.android.master.mad.todo.sync.IAsyncSyncResponse;
import com.android.master.mad.todo.sync.ITaskCrudOperations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by MISSLERT on 28.05.2016.
 * Startup activity showing a simple splash screen and checking connection to web interface.
 */
public class StartupActivity extends AppCompatActivity implements IAsyncConnectionResponse, IAsyncSyncResponse {

    private final String LOG_TAG = StartupActivity.class.getSimpleName();

    static final int LOGIN_REQUEST = 1;

    // UI references
    private ProgressDialog connectionDialog;

    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, ": onCreate().");
        checkWebInterfaceAvailability();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, ": onActivityResult().");
        // Check which request we're responding to
        if (requestCode == LOGIN_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                syncWithWebService();
            }
        }
    }

    //===========================================
    // CONNECTION METHODS
    //===========================================
    public void evaluateConnection(boolean result){
        Log.v(LOG_TAG, ": evaluateConnection().");
        if(result){
            Log.i(LOG_TAG, "Web interface available.");
            Intent intent = new Intent(this,LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else{
            Log.i(LOG_TAG, "Web interface not available.");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(R.string.message_title_webservice_unavailable);
            builder.setMessage(R.string.message_webservice_unavailable);
            builder.setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.v(LOG_TAG, " : AlertDialog onClick().");
                    Intent intent = new Intent(getApplicationContext(), TaskListActivity.class);
                    intent.putExtra(getString(R.string.intent_web_service), false);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void checkWebInterfaceAvailability(){
        Log.v(LOG_TAG, ": checkWebInterfaceAvailability().");
        if(checkConnection()){
            Log.i(LOG_TAG, "Connection is true, check server connection.");
            connectionDialog = new ProgressDialog(this);
            connectionDialog.setMessage(getString(R.string.checking_server_connection));
            connectionDialog.show();
            WebServiceConnection task = new WebServiceConnection(this);
            task.execute();
        }
    }

    private boolean checkConnection(){
        Log.v(LOG_TAG, ": checkConnection().");
        ConnectivityManager conManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = conManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    //===========================================
    // ASYNC WEBSERVICE CONNECTION
    //===========================================
    private class WebServiceConnection extends AsyncTask<Void, Void, Boolean> implements IAsyncConnectionResponse {

        private final String LOG_TAG = WebServiceConnection.class.getSimpleName();

        // DELEGATE
        IAsyncConnectionResponse delegate;

        // CONSTRUCTOR
        public WebServiceConnection(IAsyncConnectionResponse delegate){
            this.delegate = delegate;
        }

        // LIFECYCLE METHODS
        protected void onPreExecute(){
            Log.v(LOG_TAG, ": onPreExecute().");
        }

        protected void onPostExecute(Boolean result) {
            Log.v(LOG_TAG, ": onPostExecute().");
            connectionDialog.hide();
            evaluateConnection(result);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.v(LOG_TAG, ": doInBackground().");
            try {
                URL url = new URL(getResources().getString(R.string.base_url));

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("User-Agent", "Android Application");
                con.setRequestProperty("Connection", "close");
                con.setConnectTimeout(500);
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

        // INTERFACE METHODS
        @Override
        public void evaluateConnection(boolean result) {
            Log.v(LOG_TAG, ": evaluateConnection().");
            delegate.evaluateConnection(result);
        }
    }

    //===========================================
    // ASYNC WEBSERVICE SYNCHRONISATION
    //===========================================
    private class WebServiceSynchronisation extends AsyncTask<Void, Void, Boolean> implements IAsyncSyncResponse {

        private final String LOG_TAG = WebServiceSynchronisation.class.getSimpleName();

        // DELEGATE
        IAsyncSyncResponse delegate;

        // CONSTRUCTOR
        public WebServiceSynchronisation(IAsyncSyncResponse delegate){
            this.delegate = delegate;
        }

        // LIFECYCLE METHODS
        protected void onPreExecute(){
            Log.v(LOG_TAG, ": onPreExecute().");
        }

        protected void onPostExecute(Boolean result) {
            Log.v(LOG_TAG, ": onPostExecute().");
            connectionDialog.hide();
            onSyncResult(result);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.v(LOG_TAG, ": doInBackground().");

            // Setup services.
            TaskSQLiteOperationService sqLiteConnector = new TaskSQLiteOperationService(getApplicationContext());
            ITaskCrudOperations webServiceConnector = RetrofitServiceGenerator.createService(ITaskCrudOperations.class);

            // Get all tasks from web service for delete or bulkInsert.
            Call<List<Task>> callReadAll = webServiceConnector.readAll();
            List<Task> localTasks;
            List<Task> webServiceTasks = new ArrayList<>();
            try {
                webServiceTasks = callReadAll.execute().body();
            } catch (IOException e){
                Log.i(LOG_TAG, " : readAll() not successful.");
            }

            // If there is no local data, insert data from webservice.
            // Reload data from database
            Cursor cursor = sqLiteConnector.readAll();
            if(!cursor.moveToFirst()){
                sqLiteConnector.bulkInsert(webServiceTasks);
            }

            // Delete all tasks in webservice.
            Call<Boolean> callDelete;
            for (Task task: webServiceTasks) {
                callDelete = webServiceConnector.delete(task.getId());
                try {
                    Log.v(LOG_TAG, " : delete Task " + task.getId() + " status: " + callDelete.execute().body());
                } catch (IOException e){
                    Log.w(LOG_TAG, " delete for Task " + task.getId() + "failed.");
                }
            }

            // Read local database again and sync tasks to webservice.
            cursor = sqLiteConnector.readAll();
            localTasks = Utility.createTasksFromCursor(cursor);
            Call<Task> callInsert;
            for (Task task: localTasks) {
                callInsert = webServiceConnector.insert(task);
                try {
                    Log.v(LOG_TAG, " : insert Task " + task.getId() + " status: " + callInsert.execute().body());
                } catch (IOException e){
                    Log.w(LOG_TAG, " insert for Task " + task.getId() + "failed.");
                }
            }
            cursor.close();

            return true;
        }

        // INTERFACE METHODS
        @Override
        public void onSyncResult(boolean result) {
            Log.v(LOG_TAG, ": onSyncResult().");
            delegate.onSyncResult(result);
        }
    }

        @Override
    public void onSyncResult(boolean result) {
        Log.v(LOG_TAG, ": onSyncResult().");
        // Check sync result
        if (result) {
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.putExtra(getString(R.string.intent_web_service), true);
            startActivity(intent);
            finish();
        }
    }

    private void syncWithWebService(){
        Log.v(LOG_TAG, ": syncWithWebService().");
        connectionDialog = new ProgressDialog(this);
        connectionDialog.setMessage(getString(R.string.syncing_with_server));
        connectionDialog.show();
        WebServiceSynchronisation task = new WebServiceSynchronisation(this);
        task.execute();
    }


}




