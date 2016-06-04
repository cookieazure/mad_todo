package com.android.master.mad.todo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.helper.TaskSQLiteOperationService;

public class TaskActivity extends AppCompatActivity{

    private final String LOG_TAG = TaskActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, " : onCreate().");
        setContentView(R.layout.activity_tasks);

        //TODO Datenabgleich.
        addTestData();

        if(findViewById(R.id.fragment_container) != null){
            if (savedInstanceState != null) {
                return;
            }
            TaskListFragment taskListFragment = new TaskListFragment();
            taskListFragment.setArguments(getIntent().getExtras());

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, taskListFragment).commit();

        }
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
