package com.android.master.mad.todo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MISSLERT on 31.05.2016.
 * CursorAdapter for handling and binding Tasks to the UI.
 */
public class TaskAdapter extends CursorAdapter {

    private final String LOG_TAG = TaskAdapter.class.getSimpleName();

    //===========================================
    // CONSTRUCTOR METHODS
    //===========================================
    public TaskAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    //===========================================
    // VIEW HOLDER
    //===========================================
    public static class ViewHolder {
        public final CheckBox done;
        public final TextView name;
        public final TextView date;
        public final CheckBox fav;

        public ViewHolder(View view) {
            done = (CheckBox) view.findViewById(R.id.task_item_check_done);
            name = (TextView) view.findViewById(R.id.task_item_name);
            date = (TextView) view.findViewById(R.id.task_item_expiry);
            fav = (CheckBox) view.findViewById(R.id.task_item_check_fav);
        }
    }

    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, ": newView().");
        View view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(R.id.viewHolder, viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, ": bindView().");

        view.setTag(R.id.position, cursor.getPosition());
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.viewHolder);

        viewHolder.done.setChecked(cursor.getInt(TaskListActivity.COL_TASK_DONE) != 0);
        viewHolder.name.setText(cursor.getString(TaskListActivity.COL_TASK_NAME));
        long expiry = cursor.getLong(TaskListActivity.COL_TASK_DATE);
        if (expiry == 0) {
            viewHolder.date.setText(null);
        } else {
            String date = new SimpleDateFormat(view.getResources().getString(R.string.simple_date_format)).format(new Date(expiry));
            viewHolder.date.setText(date);
        }
        viewHolder.fav.setChecked(cursor.getInt(TaskListActivity.COL_TASK_FAV) != 0);
        if (viewHolder.done.isChecked()) {
            viewHolder.name.setPaintFlags(viewHolder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            viewHolder.date.setPaintFlags(viewHolder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            viewHolder.name.setTextColor(Color.GRAY);
            viewHolder.date.setTextColor(Color.GRAY);
        } else {
            viewHolder.name.setPaintFlags(0);
            viewHolder.date.setPaintFlags(0);
            if (expiry != 0 && expiry < Calendar.getInstance().getTimeInMillis()) {
                viewHolder.name.setTextColor(Color.RED);
                viewHolder.date.setTextColor(Color.RED);
            } else {
                viewHolder.name.setTextColor(Color.BLACK);
                viewHolder.date.setTextColor(Color.BLACK);
            }
        }
    }

}
