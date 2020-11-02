package com.example.room8.ui.notifications.todomvp3.addedittodoitem;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

// import com.example.room8.ui.notifications.todomvp3.R;
import com.example.room8.R;
import com.example.room8.ui.notifications.todomvp3.data.ToDoItem;

public class AddEditToDoItemActivity extends AppCompatActivity {

    TextView todoTitle, todoBody;
    ToDoItem item;
    Button saveButton;
    Switch completedSwitch;
    TextView dueDate, dueTime;
    Calendar calendar = Calendar.getInstance();
    Context context;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_to_do_item);
        item = (ToDoItem) getIntent().getSerializableExtra("item"); // Gets item sent to be edited
        context = this;

        /**
         * Methods to get values from item and set them accordingly
         */
        todoTitle = findViewById(R.id.etItemTitle);
        todoTitle.setText(item.getTitle());
        todoBody = findViewById(R.id.etItemContent);
        todoBody.setText(item.getContent());
        saveButton = findViewById(R.id.btnSaveToDoItem);
        completedSwitch = findViewById(R.id.completedSwitch);
        completedSwitch.setChecked(item.getCompleted());
        dueDate = findViewById(R.id.dueDate);
        dueTime = findViewById(R.id.dueTime);
        /**
         * If statement to make due date current date/time is ther currently is none
         */
        if (item.getDueDate() == 0 || item.getDueDate() == -1) {
            dueDate.setText(calendar.getTime().toString());
            dueTime.setText(DateFormat.format("h:mmaa", calendar));
        } else {
            calendar.setTimeInMillis(item.getDueDate());
            dueDate.setText(calendar.getTime().toString());
            dueTime.setText(DateFormat.format("h:mmaa", calendar));
        }

        /**
         * Instantiates new date picker and sets on date change listener
         * Also sets due date field to new value
         */
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(year, month, day);
                dueDate.setText(calendar.getTime().toString());
            }
        };

        /**
         * Instantiates new time picker and sets on time change listener
         * Also sets due time field to new value
         */
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(calendar.HOUR_OF_DAY, hour);
                calendar.set(calendar.MINUTE, minute);
                dueTime.setText(DateFormat.format("h:mmaa", calendar));
            }
        };


        /**
         * Sets on click listener for due date field
         */
        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        /**
         * Sets on click listener for due time field
         */
        dueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(context, time, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),false).show();
            }
        });

        /**
         * Method to set listener to save button
         * Also switches view
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateItem(); // Call to populate todoItem being sent back
                Intent intent = getIntent();
                intent.putExtra("ToDoItem", item);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Method to populate item being sent back
     */
    private void populateItem() {
        item.setTitle(todoTitle.getText().toString());
        item.setContent(todoBody.getText().toString());
        item.setCompleted(completedSwitch.isChecked());
        item.setDueDate(calendar.getTimeInMillis());
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
