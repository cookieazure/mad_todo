package com.android.master.mad.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.sync.RetrofitServiceGenerator;
import com.android.master.mad.todo.helper.Utility;
import com.android.master.mad.todo.sync.ITaskCrudOperations;
import com.android.master.mad.todo.sync.TaskSQLiteOperationService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by MISSLERT on 28.05.2016.
 * List activity for displaying, creation and quick editing task details.
 */
public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;
    static final int REQUEST_NEW_TASK = 1;
    static final int REQUEST_UPDATE_TASK = 2;
    static final int RESULT_DELETE = 3;
    private static final int REQUEST_READ_CONTACTS = 4;

    // Database & Webservice references
    private Boolean online;
    private ITaskCrudOperations webServiceConnector;
    private TaskSQLiteOperationService sqLiteConnector;

    // Sort order variables.
    private int sortOrder;
    static final int SORT_DATE_FIRST = 1;
    static final int SORT_FAV_FIRST = 2;

    // UI references.
    private TaskAdapter taskAdapter;
    // private int currentPosition = ListView.INVALID_POSITION;

    // Column names for task list view
//    private static final String[] TASK_COLUMNS = {
//            TaskContract.Task.TABLE_NAME + "." + TaskContract.Task._ID,
//            TaskContract.Task.COLUMN_NAME,
//            TaskContract.Task.COLUMN_DESC,
//            TaskContract.Task.COLUMN_DATE,
//            TaskContract.Task.COLUMN_DONE,
//            TaskContract.Task.COLUMN_FAV,
//            TaskContract.Task.COLUMN_CONTACTS
//    };

    // Indices for TASK_COLUMNS
    //static final int COL_TASK_ID = 0;
    static final int COL_TASK_NAME = 1;
    //static final int COL_TASK_DESC = 2;
    static final int COL_TASK_DATE = 3;
    static final int COL_TASK_DONE = 4;
    static final int COL_TASK_FAV= 5;
    //static final int COL_TASK_CONTACTS = 6;

    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, ": onCreate().");

        if (online == null) {
            if(savedInstanceState != null){
                online = savedInstanceState.getBoolean(getString(R.string.instance_web_service), false);
            } else {
                online = getIntent().getBooleanExtra(getString(R.string.intent_web_service), false);
            }
        }

        setContentView(R.layout.activity_task_list);
        Log.d(LOG_TAG, ": status online is " + online);
        // Read shared preferences for sort order
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        sortOrder = sharedPreferences.getInt(getString(R.string.shared_pref_sort_order), 1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_task_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        checkPermissions();
        setupSQLiteConnector();
        setupWebServiceConnector();

        ListView taskList = (ListView) findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(this, null, 0);
        taskList.setAdapter(taskAdapter);
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) taskAdapter.getItem(position);
                editTaskUsingDetailView(Utility.createTaskFromCursorWithPosition(cursor, position));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_task_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTaskUsingDetailView();
            }
        });
        getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        Log.v(LOG_TAG, ": onCreateOptionsMenu().");
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, ": onActivityResult().");
        // Check which request we're responding to
        if (requestCode == REQUEST_NEW_TASK) {
            // Make sure request was successful
            // Otherwise user navigated back or pressed delete (no update needed)
            if (resultCode == RESULT_OK) {
                Task task = data.getParcelableExtra(getString(R.string.intent_task));
                Log.v(LOG_TAG, ": insert task " + task.toString());
                insertTask(task);
            }
        } else if (requestCode == REQUEST_UPDATE_TASK){
            // Make sure request was successful or resulted in delete
            // Otherwise user navigated back (no update needed)
            if (resultCode == RESULT_OK) {
                Task task = data.getParcelableExtra(getString(R.string.intent_task));
                Log.v(LOG_TAG, ": update task " + task.toString());
                updateTask(task);
            } else if (resultCode == RESULT_DELETE) {
                Task task = data.getParcelableExtra(getString(R.string.intent_task));
                Log.v(LOG_TAG, ": delete task " + task.toString());
                deleteTask(task);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(getString(R.string.intent_web_service), online);
        super.onSaveInstanceState(savedInstanceState);
    }
    //===========================================
    // INPUT METHODS
    //===========================================
    public void onItemClickCheckbox(View view){
        Log.v(LOG_TAG, ": onItemClickCheckbox().");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, ": onOptionsItemSelected().");
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sort_item_date:
                setAndSaveSortOrder(SORT_DATE_FIRST);
                return true;
            case R.id.sort_item_fav:
                setAndSaveSortOrder(SORT_FAV_FIRST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createNewTaskUsingDetailView(){
        Log.v(LOG_TAG, ": createNewTaskUsingDetailView().");
        Task task = new Task();
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(getString(R.string.intent_task), task);
        startActivityForResult(intent, REQUEST_NEW_TASK);
    }

    public void editTaskUsingDetailView(Task task){
        Log.v(LOG_TAG, ": editTaskUsingDetailView().");
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(getString(R.string.intent_task), task);
        startActivityForResult(intent, REQUEST_UPDATE_TASK);
    }

    //===========================================
    // DATABASE / WEBSERVICE MODIFICATION METHODS
    //===========================================
    private void setupSQLiteConnector(){
        Log.v(LOG_TAG, ": setupSQLiteConnector().");
        if(sqLiteConnector == null) {
            sqLiteConnector = new TaskSQLiteOperationService(this);
        }
    }

    private void setupWebServiceConnector(){
        Log.v(LOG_TAG, ": setupWebServiceConnector().");
        if(online && webServiceConnector == null){
            webServiceConnector = RetrofitServiceGenerator.createService(ITaskCrudOperations.class);
        }
    }

    private void updateTask(Task task){
        Log.v(LOG_TAG, ": updateTask().");
        int result = sqLiteConnector.update(task.getId(), task);
        if(result != 0){
            Log.d(LOG_TAG, ": updated task " + task.toString());
            Log.d(LOG_TAG, ": status online is " + online);
            if(online){
                Call<Task> call = webServiceConnector.update(task.getId(),task);
                call.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, ": Webservice updated task successfully (" + response.body().toString() + ")");
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
        } else {
            Log.e(LOG_TAG, ": failed to update task " + task.toString());
        }
    }

    private void insertTask(Task task){
        Log.v(LOG_TAG, ": insertTask().");
        Uri taskUri = sqLiteConnector.insert(task);
        if(taskUri != null){
            Log.d(LOG_TAG, ": inserted new task " + task.toString());
            Log.d(LOG_TAG, ": status online is " + online);
            // Update task to reflect assigned SQLite ID before inserting into webservice
            task.setId(TaskContract.Task.getTaskIdFromUri(taskUri));
            if(online){
                Call<Task> call = webServiceConnector.insert(task);
                call.enqueue(new Callback<Task>() {
                    @Override
                    public void onResponse(Call<Task> call, Response<Task> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, ": Webservice inserted task successfully (" + response.body().toString() + ")");
                        } else {
                            Log.i(LOG_TAG, ": Error inserting into webservice, response not successful.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Task> call, Throwable t) {
                        Log.i(LOG_TAG, ": Error inserting into webservice - " + t.getMessage());
                    }
                });
            }
        } else {
            Log.e(LOG_TAG, ": failed to insert new task " + task.toString());
        }
    }

    private void deleteTask(Task task){
        Log.v(LOG_TAG, ": deleteTask().");
        int result = sqLiteConnector.delete(task.getId());
        if(result != 0){
            Log.d(LOG_TAG, ": deleted task " + task.toString());
            Log.d(LOG_TAG, ": status online is " + online);
            if(online){
                Call<Boolean> call = webServiceConnector.delete(task.getId());
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            Log.d(LOG_TAG, ": Webservice deleted task successfully (" + response.body().toString() + ")");
                        } else {
                            Log.i(LOG_TAG, ": Error deleting in webservice, response not successful.");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Log.i(LOG_TAG, ": Error deleting in webservice - " + t.getMessage());
                    }
                });
            }
        } else {
            Log.e(LOG_TAG, ": failed to delete task " + task.toString());
        }
    }

    //===========================================
    // CONTROLLER METHODS
    //===========================================
    private void checkPermissions() {
        if (checkSelfPermission(READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
    }

    private void setAndSaveSortOrder(int newSortOrder){
        Log.v(LOG_TAG, ": setAndSaveSortOrder().");
        sortOrder = newSortOrder;
        SharedPreferences sharedPreferences = this.getPreferences(MODE_PRIVATE);
        sharedPreferences.edit().putInt(getString(R.string.shared_pref_sort_order), sortOrder).apply();
        getSupportLoaderManager().restartLoader(TASK_LOADER, null, this);
    }

    //===========================================
    // LOADER METHODS
    //===========================================
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.v(LOG_TAG, ": onCreateLoader().");
        Uri taskUri = TaskContract.Task.CONTENT_URI;

        // Set sort order. Default is null
        String orderBy = null;
        if(sortOrder == SORT_DATE_FIRST){
            orderBy = TaskContract.Task.COLUMN_DONE + " DESC, "
                    + TaskContract.Task.COLUMN_DATE + " DESC, "
                    + TaskContract.Task.COLUMN_FAV + " DESC";

        } else if (sortOrder == SORT_FAV_FIRST){
            orderBy = TaskContract.Task.COLUMN_DONE + " DESC, "
                    + TaskContract.Task.COLUMN_FAV + " DESC, "
                    + TaskContract.Task.COLUMN_DATE + " DESC";
        }

        return new CursorLoader(this, taskUri, null, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, ": onLoadFinished().");
        taskAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, ": onLoaderReset().");
        taskAdapter.swapCursor(null);
    }

}
