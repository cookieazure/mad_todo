package com.android.master.mad.todo;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.master.mad.todo.data.TaskContract;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LOG_TAG = TaskActivity.class.getSimpleName();

    private TaskAdapter taskAdapter;
    private ListView taskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskList = (ListView) findViewById(R.id.task_list);

        Uri taskUri = TaskContract.Task.CONTENT_URI;
        Cursor cursor = getContentResolver().query(taskUri, null, null, null, null);

        taskAdapter = new TaskAdapter(this, cursor, 0);
        taskList.setAdapter(taskAdapter);
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
            case R.id.task_list_fab:
                Toast.makeText(TaskActivity.this, "FAB pressed.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.w(LOG_TAG, ": Checkbox interaction not possible in onClick().");
        }
    }
}
