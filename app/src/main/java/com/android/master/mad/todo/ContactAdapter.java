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
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MISSLERT on 14.06.2016.
 * ContactAdapter for handling and binding contacts to the UI.
 */
public class ContactAdapter extends CursorAdapter{

    private final String LOG_TAG = ContactAdapter.class.getSimpleName();

    //===========================================
    // CONSTRUCTOR METHODS
    //===========================================
    public ContactAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    //===========================================
    // VIEW HOLDER
    //===========================================
    public static class ViewHolder {
        public final TextView name;
        public final ImageButton share;
        public final ImageButton delete;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.contact_item_name);
            share = (ImageButton) view.findViewById(R.id.contact_item_share);
            delete = (ImageButton) view.findViewById(R.id.contact_item_delete);
        }
    }

    //===========================================
    // LIFECYCLE METHODS
    //===========================================
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(LOG_TAG, ": newView().");
        View view = LayoutInflater.from(context).inflate(R.layout.task_detail_contact_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(R.id.viewHolder, viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.v(LOG_TAG, ": bindView().");

        view.setTag(R.id.position, cursor.getPosition());
        view.setTag(R.id.contact_id, cursor.getLong(TaskDetailActivity.COL_CONTACT_ID));

        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.viewHolder);
        viewHolder.name.setText(cursor.getString(TaskDetailActivity.COL_CONTACT_NAME));

    }

}
