package com.android.master.mad.todo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private final String LOG_TAG = TaskActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        View recyclerView = findViewById(R.id.task_list);
        assert recyclerView != null;
        //TODO
        ((RecyclerView)recyclerView).setAdapter(null);
    }

    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, ": onClick()");
        boolean checked = ((CheckBox) view).isChecked();

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
