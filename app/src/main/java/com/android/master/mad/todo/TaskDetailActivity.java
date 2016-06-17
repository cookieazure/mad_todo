package com.android.master.mad.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.master.mad.todo.data.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by MISSLERT on 07.06.2016.
 * Detail activity for displaying and editing task details.
 */
public class TaskDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
                                                                        TimePickerDialog.OnTimeSetListener,
                                                                        LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = TaskDetailActivity.class.getSimpleName();

    private static final int CONTACT_LOADER = 2;
    private ContactAdapter contactAdapter;

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

    static final int COL_CONTACT_ID = 0;
    static final int COL_CONTACT_LOOKUP = 1;
    static final int COL_CONTACT_NAME_PRIM = 2;

    private static final int REQUEST_PICK_CONTACT= 7;

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

        if(detailedTask.getExpiry() <= 0) {
            buttonReset.setVisibility(View.INVISIBLE);
        } else {
            currentDateTime = Calendar.getInstance();
            currentDateTime.setTimeInMillis(detailedTask.getExpiry());
            Log.i(LOG_TAG, ": " + currentDateTime.get(Calendar.MONTH));
            setDateDisplay();
            setTimeDisplay();
        }

        editDescription = (EditText) findViewById(R.id.task_detail_description);
        editDescription.setText(detailedTask.getDescription());

        ImageButton buttonAddContact = (ImageButton) findViewById(R.id.task_detail_button_add);
        buttonAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, ": onClick() add contact button.");
                callContactPicker();
            }
        });

        ListView contactList = (ListView) findViewById(R.id.contact_list);
        contactAdapter = new ContactAdapter(this, null, 0);
        contactList.setAdapter(contactAdapter);

        if(detailedTask.hasContacts()){
            getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        Log.v(LOG_TAG, ": onCreateContextMenu().");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact, menu);

        View parent = (View) view.getParent();
        long contactId = (long)  parent.getTag(R.id.contact_id);
        String lookup = (String)  parent.getTag(R.id.lookup);

        Uri contactUri = Contacts.getLookupUri(contactId, lookup);

        Intent showContactIntent = new Intent(Intent.ACTION_VIEW, contactUri);
        menu.findItem(R.id.contact_show).setIntent(showContactIntent);

        String contactPhone = isContactCallable(contactId);
        if(contactPhone == null){
            menu.findItem(R.id.contact_text).setVisible(false);
        } else {
            updateDetailedTask();
            Intent textContactIntent = new Intent(Intent.ACTION_SENDTO);
            textContactIntent.setData(Uri.parse("smsto:"+contactPhone));
            textContactIntent.putExtra("sms_body", detailedTask.getName() + ": " + detailedTask.getDescription());
            menu.findItem(R.id.contact_text).setIntent(textContactIntent);
        }
        String contactMail = isContactMailable(contactId);
        if(contactMail == null){
            menu.findItem(R.id.contact_mail).setVisible(false);
        } else {
            updateDetailedTask();
            Intent mailContactIntent = new Intent(Intent.ACTION_SEND);
            mailContactIntent.setType("*/*");
            mailContactIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(contactMail)} );
            mailContactIntent.putExtra(Intent.EXTRA_SUBJECT, detailedTask.getName());
            mailContactIntent.putExtra(Intent.EXTRA_TEXT, detailedTask.getDescription());
            menu.findItem(R.id.contact_mail).setIntent(mailContactIntent);
        }
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

    public void onItemClickContact(View view){
        Log.v(LOG_TAG, ": onItemClickContact().");

        View parent = (View) view.getParent();
        long cursorId = (long)  parent.getTag(R.id.contact_id);

        if(view.getId() == R.id.contact_item_share){
            registerForContextMenu(view);
            openContextMenu(view);
        } else if (view.getId() == R.id.contact_item_delete){
            detailedTask.removeContact(String.valueOf(cursorId));
            getSupportLoaderManager().restartLoader(CONTACT_LOADER, null, this);
        }

    }

    //===========================================
    // CONTROLLER METHODS
    //===========================================
    private void resetDateAndTime(){
        Log.v(LOG_TAG, ": resetDateAndTime().");
        currentDateTime = null;
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

    private void callContactPicker(){
        Log.v(LOG_TAG, ": callContactPicker().");
        Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }

    private void setDateDisplay(){
        editDate.setText( new SimpleDateFormat(getString(R.string.simple_date_format)).format(new Date(currentDateTime.getTimeInMillis())) );
    }

    private void setTimeDisplay(){
        editTime.setText( new SimpleDateFormat(getString(R.string.simple_time_format)).format(new Date(currentDateTime.getTimeInMillis())) );
    }

    private String isContactCallable(long contactId){
        String contactPhone = null;

        Uri contactUri = Phone.CONTENT_URI;
        // Define projection
        String[] projection = new String[] {
                Contacts._ID,
                Contacts.LOOKUP_KEY,
                Contacts.Data.MIMETYPE,
                Contacts.Data.DATA1,
                Contacts.Data.DATA2
        };
        // Define selection and set selection arguments
        String selection = Data.CONTACT_ID + " = ? AND "
                + Data.MIMETYPE + " = ? AND "
                + Data.DATA2 + " = ?";
        String[] selectionArgs = {String.valueOf(contactId), Phone.CONTENT_ITEM_TYPE, String.valueOf(Phone.TYPE_MOBILE)};
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, selection, selectionArgs, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            contactPhone = cursor.getString(cursor.getColumnIndex(Contacts.Data.DATA1));
        }
        cursor.close();
        return contactPhone;
    }

    private String isContactMailable(long contactId){
        String contactMail = null;

        Uri contactUri = Email.CONTENT_URI;
        // Define projection
        String[] projection = new String[] {
                Contacts._ID,
                Contacts.LOOKUP_KEY,
                Contacts.Data.MIMETYPE,
                Contacts.Data.DATA1
        };
        // Define selection and set selection arguments
        String selection = Data.CONTACT_ID + " = ? AND "
                            + Data.MIMETYPE + " = ?";
        String[] selectionArgs = {String.valueOf(contactId), Email.CONTENT_ITEM_TYPE};
        Cursor cursor = getContentResolver()
                .query(contactUri, projection, selection, selectionArgs, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            contactMail = cursor.getString(cursor.getColumnIndex(Contacts.Data.DATA1));
        }
        cursor.close();
        return contactMail;
    }

    private void updateDetailedTask(){
        Log.v(LOG_TAG, ": updateDetailedTask().");
        detailedTask.setDone(checkDone.isChecked());
        detailedTask.setName(editName.getText().toString());
        detailedTask.setFavourite(checkFav.isChecked());
        if (currentDateTime == null) {
            Log.v(LOG_TAG, ": currentDateTime is null.");
            detailedTask.setExpiry(0);
        } else {
            detailedTask.setExpiry(currentDateTime.getTimeInMillis());
        }
        detailedTask.setDescription(editDescription.getText().toString());
        // contacts are updated on the fly, no further changes necessary.
    }

    //===========================================
    // RESULT METHODS
    //===========================================
    private void deliverTaskSave(){
        Log.v(LOG_TAG, ": deliverTaskSave().");
        updateDetailedTask();
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
        if(currentDateTime == null) currentDateTime = Calendar.getInstance();
        currentDateTime.set(year, monthOfYear, dayOfMonth, 0, 0);
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
        if(currentDateTime == null){
            currentDateTime = Calendar.getInstance();
            Calendar c = Calendar.getInstance();
            currentDateTime.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute );
            setDateDisplay();
        } else {
            currentDateTime.set(currentDateTime.get(Calendar.YEAR),currentDateTime.get(Calendar.MONTH), currentDateTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute );
        }
        setTimeDisplay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, ": onActivityResult().");
        // Check which request we're responding to
        if (requestCode == REQUEST_PICK_CONTACT) {
            // Make sure request was successful
            if (resultCode == RESULT_OK) {
                // Add contact id to task
                Uri contactUri = data.getData();
                String[] projection = new String[] {
                        Contacts._ID,
                };
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                detailedTask.addContact(String.valueOf(cursor.getLong(COL_CONTACT_ID)));
                cursor.close();
                getSupportLoaderManager().restartLoader(CONTACT_LOADER, null, this);
            }
        }
    }

    //===========================================
    // LOADER METHODS
    //===========================================
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.v(LOG_TAG, ": onCreateLoader().");

        Uri contactUri = Contacts.CONTENT_URI;

        // Prepare contacts
        ArrayList<String> contacts = detailedTask.getContacts();

        // Define projection
        String[] projection = new String[] {
                Contacts._ID,
                Contacts.LOOKUP_KEY,
                Contacts.DISPLAY_NAME_PRIMARY
        };

        // Define selection and set selection arguments
        String selection = Contacts._ID + " in (" +
                TextUtils.join(",", Collections.nCopies(contacts.size(), "?")) +
                ")";
        String[] selectionArgs = contacts.toArray(new String[contacts.size()]);

        // Define sort order
        String orderBy = Data.DISPLAY_NAME_PRIMARY;

        return new CursorLoader(this, contactUri, projection, selection, selectionArgs, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.v(LOG_TAG, ": onLoadFinished().");
        contactAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.v(LOG_TAG, ": onLoaderReset().");
        contactAdapter.swapCursor(null);
    }

}
