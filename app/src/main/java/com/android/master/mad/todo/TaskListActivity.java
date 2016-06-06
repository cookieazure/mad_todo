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

public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    private TaskAdapter taskAdapter;
    private ListView taskList;

    private boolean online;
    private ITaskCrudOperations webServiceConnector;
    private TaskSQLiteOperationService sqLiteConnector;

    // Column names for task list view
    private static final String[] TASK_COLUMNS = {
            TaskContract.Task.TABLE_NAME + "." + TaskContract.Task._ID,
            TaskContract.Task.COLUMN_NAME,
            TaskContract.Task.COLUMN_DESC,
            TaskContract.Task.COLUMN_DATE,
            TaskContract.Task.COLUMN_DONE,
            TaskContract.Task.COLUMN_FAV,
            TaskContract.Task.COLUMN_CONTACTS
    };

    // Indices for TASK_COLUMNS
    static final int COL_TASK_ID = 0;
    static final int COL_TASK_NAME = 1;
    static final int COL_TASK_DESC = 2;
    static final int COL_TASK_DATE = 3;
    static final int COL_TASK_DONE = 4;
    static final int COL_TASK_FAV= 5;
    static final int COL_TASK_CONTACTS = 6;


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
        //addTestData();

        setupSQLiteConnector();
        if(online){
            setupWebServiceConnector();
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
