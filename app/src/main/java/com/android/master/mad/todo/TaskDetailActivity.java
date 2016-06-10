package com.android.master.mad.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.android.master.mad.todo.data.Task;

import java.util.Calendar;

/**
 * Created by MISSLERT on 07.06.2016.
 * Detail activity for displaying and editing task details.
 */
public class TaskDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private final String LOG_TAG = TaskDetailActivity.class.getSimpleName();

    private Task detailedTask;
    private CheckBox checkDone;
    private EditText editName;
    private CheckBox checkFav;
    private EditText editDate;
    private EditText editTime;
    private ImageButton buttonReset;
    private EditText editDescription;

    private FragmentManager supportFragmentManager;

    private Calendar currentDateTime;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        detailedTask = getIntent().getParcelableExtra(getString(R.string.intent_task));
        Log.d(LOG_TAG, detailedTask.toString());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_task_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deliverTaskSave();
            }
        });

        checkDone = (CheckBox) findViewById(R.id.task_detail_check_done);
        checkDone.setChecked(detailedTask.isDone());
        editName = (EditText) findViewById(R.id.task_detail_name);
        editName.setText(detailedTask.getName());
        checkFav = (CheckBox) findViewById(R.id.task_detail_check_fav);
        checkFav.setChecked(detailedTask.isFavourite());

        editDate = (EditText) findViewById(R.id.task_detail_date);
        editDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(LOG_TAG, ": onTouch() date.");
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    callDatePicker();
                }
                return true;
            }
        });
        editTime = (EditText) findViewById(R.id.task_detail_time);
        editTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(LOG_TAG, ": onTouch() time.");
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    callTimePicker();
                }
                return true;
            }
        });
        buttonReset = (ImageButton) findViewById(R.id.task_detail_button_clear);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, ": onClick() reset button.");
                resetDateAndTime();
            }
        });
        currentDateTime = Calendar.getInstance();
        if(detailedTask.getExpiry() <= 0) {
            buttonReset.setVisibility(View.INVISIBLE);
        } else {

            currentDateTime.setTimeInMillis(detailedTask.getExpiry());
            Log.i(LOG_TAG, ": " + currentDateTime.get(Calendar.MONTH));
            setDateDisplay();
            setTimeDisplay();
        }

        editDescription = (EditText) findViewById(R.id.task_detail_description);
        editDescription.setText(detailedTask.getDescription());

        supportFragmentManager = getSupportFragmentManager();
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
                //builder.setTitle(R.string.message_delete_header);
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

    private void resetDateAndTime(){
        Log.v(LOG_TAG, ": resetDateAndTime().");
        editDate.setText(null);
        editTime.setText(null);
        buttonReset.setVisibility(View.INVISIBLE);
    }

    private void callDatePicker(){
        Log.v(LOG_TAG, ": callDatePicker().");
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(supportFragmentManager, "datePicker");
    }

    private void callTimePicker(){
        Log.v(LOG_TAG, ": callTimePicker().");
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(supportFragmentManager, "timePicker");
    }

    /**
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Log.v(LOG_TAG, ": onDateSet().");
        buttonReset.setVisibility(View.VISIBLE);
        currentDateTime.set(year, monthOfYear, dayOfMonth);
        setDateDisplay();
    }

    /**
     * Called when the user is done setting a new time and the dialog has
     * closed.
     *
     * @param view      the view associated with this listener
     * @param hourOfDay the hour that was set
     * @param minute    the minute that was set
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.v(LOG_TAG, ": onTimeSet().");
        buttonReset.setVisibility(View.VISIBLE);
        if(editDate.getText() == null){
            Calendar c = Calendar.getInstance();
            currentDateTime.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute );
            setDateDisplay();
        } else {
            currentDateTime.set(currentDateTime.get(Calendar.YEAR),currentDateTime.get(Calendar.MONTH), currentDateTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute );
        }
        setTimeDisplay();
    }

    private void setDateDisplay(){
        editDate.setText(currentDateTime.get(Calendar.DAY_OF_MONTH) + "." + (currentDateTime.get(Calendar.MONTH)+1) + "." + currentDateTime.get(Calendar.YEAR));
    }

    private void setTimeDisplay(){
        editTime.setText(currentDateTime.get(Calendar.HOUR_OF_DAY) + ":" + currentDateTime.get(Calendar.MINUTE));
    }

    //===========================================
    // RESULT METHODS
    //===========================================
    private void deliverTaskSave(){
        detailedTask.setDone(checkDone.isChecked());
        detailedTask.setName(editName.getText().toString());
        detailedTask.setFavourite(checkFav.isChecked());
        // TODO
        if (editDate.getText() == null || editDate.getText().equals("")) {
            Log.i(LOG_TAG, ": getText is null");
            detailedTask.setExpiry(0);
        } else {
            Log.i(LOG_TAG, ": getText is not null");
            detailedTask.setExpiry(currentDateTime.getTimeInMillis());
        }
        detailedTask.setDescription(editDescription.getText().toString());
        // TODO Contacts
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
