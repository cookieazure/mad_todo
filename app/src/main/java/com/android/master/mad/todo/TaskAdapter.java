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

/**
 * Created by misslert on 31.05.2016.
 * CursorAdapter for handling and binding Tasks to the UI.
 */
public class TaskAdapter extends CursorAdapter {

    private final String LOG_TAG = TaskAdapter.class.getSimpleName();

    public TaskAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

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

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, " : newView().");
        View view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, " : bindView().");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.done.setChecked(cursor.getInt(TaskListActivity.COL_TASK_DONE) != 0);
        viewHolder.name.setText(cursor.getString(TaskListActivity.COL_TASK_NAME));
        viewHolder.date.setText(cursor.getString(TaskListActivity.COL_TASK_DATE));
        viewHolder.fav.setChecked(cursor.getInt(TaskListActivity.COL_TASK_FAV) != 0);
    }
}
