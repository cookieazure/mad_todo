package com.android.master.mad.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
                deliverNewActivity();
            }
        });

    }

    private void deliverNewActivity(){
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
        }
        return super.onOptionsItemSelected(item);
    }
}
