package com.android.master.mad.todo;

import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.android.master.mad.todo.data.Task;
import com.android.master.mad.todo.data.TaskContract;
import com.android.master.mad.todo.helper.RetrofitServiceGenerator;
import com.android.master.mad.todo.helper.TaskSQLiteOperationService;
import com.android.master.mad.todo.helper.Utility;
import com.android.master.mad.todo.sync.ITaskCrudOperations;

import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskListActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    // Database & Webservice references
    private boolean online;
    private ITaskCrudOperations webServiceConnector;
    private TaskSQLiteOperationService sqLiteConnector;

    // Sort order variables.
    private int sortOrder;
    static final int SORT_DATE_FIRST = 1;
    static final int SORT_FAV_FIRST = 2;

    // UI references.
    private TaskAdapter taskAdapter;
    private ListView taskList;
    private int currentPosition = ListView.INVALID_POSITION;

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


    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, " : onCreate().");
        online = getIntent().getBooleanExtra(getString(R.string.intent_web_service), false);
        setContentView(R.layout.activity_task_list);

        // Read shared preferences for sort order
        SharedPreferences sharedPreferences = this.getPreferences(this.MODE_PRIVATE);
        sortOrder = sharedPreferences.getInt(getString(R.string.shared_pref_sort_order), 1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_task_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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
                Toast.makeText(getApplicationContext(), "List item clicked (id " + position +")", Toast.LENGTH_LONG).show();
                Cursor cursor = (Cursor) taskAdapter.getItem(position);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_task_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "FAB (add) clicked.", Toast.LENGTH_LONG).show();
                Task task = new Task("New Task" +(new Random()).nextInt(100)+1, "Default description");
                task.setExpiry(Calendar.getInstance().getTimeInMillis());
                insertTask(task);
            }
        });
        getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
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

    //===========================================
    // INPUT METHODS
    //===========================================
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sort_item_date:
                setAndSaveSortOrder(SORT_DATE_FIRST);
                Toast.makeText(getApplicationContext(), "SORT BY DATE clicked.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.sort_item_fav:
                setAndSaveSortOrder(SORT_FAV_FIRST);
                Toast.makeText(getApplicationContext(), "SORT BY FAV clicked.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //===========================================
    // DATABASE / WEBSERVICE MODIFICATION METHODS
    //===========================================
    private void setupSQLiteConnector(){
        sqLiteConnector = new TaskSQLiteOperationService(this);
    }

    private void setupWebServiceConnector(){
        webServiceConnector = RetrofitServiceGenerator.createService(ITaskCrudOperations.class);
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
        sqLiteConnector.insert(task);
        Log.i(LOG_TAG, ": inserted new task " + task.toString());
    }

    private void delete(Task task){

    }

    //===========================================
    // SHARED PREFERENCES METHODS
    //===========================================
    private void setAndSaveSortOrder(int newSortOrder){
        sortOrder = newSortOrder;
        SharedPreferences sharedPreferences = this.getPreferences(this.MODE_PRIVATE);
        sharedPreferences.edit().putInt(getString(R.string.shared_pref_sort_order), sortOrder).commit();
        getSupportLoaderManager().restartLoader(TASK_LOADER, null, this);
    }

    //===========================================
    // LOADER METHODS
    //===========================================
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri taskUri = TaskContract.Task.CONTENT_URI;

        // Set sort order. Default is null
        String orderBy = null;
        if(sortOrder == SORT_DATE_FIRST){
            orderBy = TaskContract.Task.COLUMN_DONE + " DESC, "
                    + TaskContract.Task.COLUMN_DATE + " ASC, "
                    + TaskContract.Task.COLUMN_FAV + " DESC";

        } else if (sortOrder == SORT_FAV_FIRST){
            orderBy = TaskContract.Task.COLUMN_DONE + " DESC, "
                    + TaskContract.Task.COLUMN_FAV + " DESC, "
                    + TaskContract.Task.COLUMN_DATE + " ASC";
        }
        //TODO initialize sort order

        return new CursorLoader(this, taskUri, null, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        taskAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        taskAdapter.swapCursor(null);
    }

}
