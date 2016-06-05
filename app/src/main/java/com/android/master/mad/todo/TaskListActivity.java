package com.android.master.mad.todo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.helper.RetrofitServiceGenerator;
import com.android.master.mad.todo.helper.TaskSQLiteOperationService;
import com.android.master.mad.todo.sync.ITaskCrudOperations;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    private TaskAdapter taskAdapter;
    private ListView taskList;

    private boolean online;
    private ITaskCrudOperations webServiceConnector;
    private TaskSQLiteOperationService sqLiteConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, " : onCreate().");
        online = getIntent().getBooleanExtra(getString(R.string.intent_web_service), false);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_task_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //TODO remove
        addTestData();

        setupSQLiteConnector();
        if(online){
            setupWebServiceConnector();
            //TODO move to Login Activity.
            //syncWithWebService();
        }

        taskList = (ListView) findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(this, null, 0);
        taskList.setAdapter(taskAdapter);

        getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
    }

    private void setupSQLiteConnector(){
        sqLiteConnector = new TaskSQLiteOperationService(this);
    }

    private void setupWebServiceConnector(){
        webServiceConnector = RetrofitServiceGenerator.createService(ITaskCrudOperations.class);
    }

    private void syncWithWebService(){
        Cursor cursor = sqLiteConnector.readAll();
        if(!cursor.moveToFirst()){
            Call<List<Task>> call = webServiceConnector.readAll();
            call.enqueue(new Callback<List<Task>>() {
                @Override
                public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                    if (response.isSuccessful()) {
                        Log.i(LOG_TAG, " : Sync complete.");
                        sqLiteConnector.bulkInsert(response.body());
                    } else {
                        Log.i(LOG_TAG, " : Sync not successful.");
                    }
                }

                @Override
                public void onFailure(Call<List<Task>> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Log.i(LOG_TAG, " - Retrofit error: " + t.getMessage());
                }
            });
        }
        cursor.close();
    }

    @Override
    public void onPause(){
        super.onPause();
        webServiceConnector = null;
        sqLiteConnector = null;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(sqLiteConnector == null) {setupSQLiteConnector();}
        if(online){
            if(webServiceConnector == null) {setupWebServiceConnector();}
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri taskUri = TaskContract.Task.CONTENT_URI;

        //TODO initialize sort order

        return new CursorLoader(this, taskUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        taskAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        taskAdapter.swapCursor(null);
    }






    private void addTestData(){
        TaskSQLiteOperationService databaseOperations = new TaskSQLiteOperationService(this);
        Cursor cursor = databaseOperations.readAll();
        if(!cursor.moveToFirst()){
            Task testItem;
            testItem = new Task("Name 1", "Description");
            Uri returnUri = databaseOperations.insert(testItem);
            Log.d(LOG_TAG, returnUri.toString());
            testItem = new Task("Name 2", "Description");
            returnUri = databaseOperations.insert(testItem);
            Log.d(LOG_TAG, returnUri.toString());
            testItem = new Task("Name 3", "Description");
            returnUri = databaseOperations.insert(testItem);
            Log.d(LOG_TAG, returnUri.toString());
            testItem = new Task("Name 4", "Description");
            returnUri = databaseOperations.insert(testItem);
            Log.d(LOG_TAG, returnUri.toString());
            testItem = new Task("Name 5", "Description");
            returnUri = databaseOperations.insert(testItem);
            Log.d(LOG_TAG, returnUri.toString());
        } else {
//            int reutnr = databaseOperations.delete(0);

        }
        cursor.close();

    }

}
