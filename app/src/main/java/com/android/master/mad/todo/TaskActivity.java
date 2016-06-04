package com.android.master.mad.todo;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.master.mad.todo.data.TaskContract;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskActivity.class.getSimpleName();

    private static final int TASK_LOADER = 1;

    private TaskAdapter taskAdapter;
    private ListView taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_task_list);

        taskList = (ListView) findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(this, null, 0);
        taskList.setAdapter(taskAdapter);

        getSupportLoaderManager().initLoader(TASK_LOADER, null, this);
    }

    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, ": onClick()");

        switch(view.getId()) {
            case R.id.task_item_check_done:
                ((CheckBox) view).toggle();
                break;
            case R.id.task_item_check_fav:
                ((CheckBox) view).toggle();
                break;
            case R.id.fragment_task_list_fab:
                Toast.makeText(TaskActivity.this, "FAB pressed.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.w(LOG_TAG, ": Checkbox interaction not possible in onClick().");
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
}
