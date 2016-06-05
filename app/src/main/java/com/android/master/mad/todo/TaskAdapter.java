package com.android.master.mad.todo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.master.mad.todo.data.TaskContract;

/**
 * Created by misslert on 31.05.2016.
 * CursorAdapter for handling and binding Tasks to the UI.
 */
public class TaskAdapter extends CursorAdapter {

    private final String LOG_TAG = TaskAdapter.class.getSimpleName();

    public TaskAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, " : newView().");

        View view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, " : bindView().");

        CheckBox done = (CheckBox) view.findViewById(R.id.task_item_check_done);
        TextView name = (TextView) view.findViewById(R.id.task_item_name);
        TextView text = (TextView) view.findViewById(R.id.task_item_expiry);
        CheckBox fav = (CheckBox) view.findViewById(R.id.task_item_check_fav);

        done.setChecked(cursor.getInt(cursor.getColumnIndex(TaskContract.Task.COLUMN_DONE)) != 0);
        name.setText(cursor.getString(cursor.getColumnIndex(TaskContract.Task.COLUMN_NAME)));
        text.setText(cursor.getString(cursor.getColumnIndex(TaskContract.Task.COLUMN_DATE)));
        fav.setChecked(cursor.getInt(cursor.getColumnIndex(TaskContract.Task.COLUMN_FAV)) != 0);

    }
}
