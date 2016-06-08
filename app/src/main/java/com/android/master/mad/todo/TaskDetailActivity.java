package com.android.master.mad.todo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.master.mad.todo.data.Task;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by MISSLERT on 07.06.2016.
 * Detail activity for displaying and editing task details.
 */
public class TaskDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = TaskDetailActivity.class.getSimpleName();

    private Task detailedTask;

    private CheckBox checkDone;
    private EditText editName;
    private CheckBox checkFav;

    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, ": onCreate().");
        setContentView(R.layout.activity_task_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_task_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        detailedTask = getIntent().getParcelableExtra(getString(R.string.intent_task));
        Log.i(LOG_TAG, detailedTask.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_task_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "FAB (save) clicked.", Toast.LENGTH_LONG).show();
                deliverTaskSave();
            }
        });

        checkDone = (CheckBox) findViewById(R.id.task_detail_check_done);
        checkDone.setChecked(detailedTask.isDone());
        editName = (EditText) findViewById(R.id.task_detail_name);
        editName.setText(detailedTask.getName());
        checkFav = (CheckBox) findViewById(R.id.task_detail_check_fav);
        checkFav.setChecked(detailedTask.isFavourite());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        Log.v(LOG_TAG, ": onCreateOptionsMenu().");
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    //===========================================
    // INPUT METHODS
    //===========================================
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle(R.string.message_delete_header);
                builder.setMessage(R.string.message_delete_task);
                builder.setPositiveButton(R.string.button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deliverTaskDelete();
                    }
                });
                builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    //===========================================
    // RESULT METHODS
    //===========================================
    private void deliverTaskSave(){
        if (detailedTask.getName() == null) {
            detailedTask.setName("New Task" + (new Random()).nextInt(100) + 1);
            detailedTask.setDescription("Default description");
        }
        detailedTask.setExpiry(Calendar.getInstance().getTimeInMillis());
        Intent returnIntent = new Intent(this, TaskDetailActivity.class);
        returnIntent.putExtra(getString(R.string.intent_task), detailedTask);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void deliverTaskDelete(){
        Intent returnIntent = new Intent(this, TaskDetailActivity.class);
        returnIntent.putExtra(getString(R.string.intent_task), detailedTask);
        setResult(TaskListActivity.RESULT_DELETE, returnIntent);
        finish();
    }

}
