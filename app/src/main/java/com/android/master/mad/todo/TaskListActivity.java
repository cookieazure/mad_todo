package com.android.master.mad.todo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.helper.TaskSQLiteOperationService;

public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    private TaskAdapter taskAdapter;
    private ListView taskList;
    private boolean online;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, " : onCreate().");
        setContentView(R.layout.activity_task_list);
        online = getIntent().getBooleanExtra(getString(R.string.intent_web_service), false);
        //TODO Datenabgleich.
        addTestData();

        taskList = (ListView) findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(this, null, 0);
        taskList.setAdapter(taskAdapter);

        getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
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
        }

    }

}
