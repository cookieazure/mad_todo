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
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.helper.RetrofitServiceGenerator;
import com.android.master.mad.todo.helper.TaskSQLiteOperationService;
import com.android.master.mad.todo.helper.Utility;
import com.android.master.mad.todo.sync.ITaskCrudOperations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    private TaskAdapter taskAdapter;
    private ListView taskList;
    private int currentPosition = ListView.INVALID_POSITION;
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
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) taskAdapter.getItem(position);
            }
        });
        getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
    }

    public void onItemClickCheckbox(View view){
        View parent = (View) view.getParent();
        int cursorPosition = (int)  parent.getTag(R.id.position);

        Task task = Utility.createTaskFromCursorWithPosition(taskAdapter.getCursor(), cursorPosition);

        if(view.getId() == R.id.task_item_check_done){
            task.setDone( ((CheckBox) view).isChecked() );
        } else if (view.getId() == R.id.task_item_check_fav){
            task.setFavourite( ((CheckBox) view).isChecked() );
        }

        updateTask(task);
    }

    private void updateTask(Task task){
        sqLiteConnector.update(task.getId(), task);
        if(online){
            Call<Task> call = webServiceConnector.update(task.getId(),task);
            call.enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Log.d(LOG_TAG, ": Webservice updated successfully (" + response.body().toString() + ")");
                    } else {
                        Log.i(LOG_TAG, ": Error updating webservice, response not successful.");
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Log.i(LOG_TAG, ": Error updating webservice - " + t.getMessage());
                }
            });
        }
    }

    private void insertTask(Task task){

    }

    private void delete(Task task){

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
