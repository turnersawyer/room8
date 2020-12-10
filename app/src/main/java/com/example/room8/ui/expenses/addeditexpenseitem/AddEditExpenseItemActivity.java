package com.example.room8.ui.expenses.addeditexpenseitem;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.room8.R;
import com.example.room8.ui.expenses.data.ExpenseItem;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddEditExpenseItemActivity extends AppCompatActivity {

    TextView expenseDescription, expenseAmount;
    ExpenseItem item;
    Button saveButton;
    TextView dueDate;
    DatePicker picker;
    Switch completedSwitch;
    Calendar calendar = Calendar.getInstance();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense_item);
        item = (ExpenseItem) getIntent().getSerializableExtra("item"); // Gets item sent to be edited
        context = this;

        Log.d("AddeditExpense", "Item Title: " + item.getDescription());

        /**
         * Methods to get values from item and set them accordingly
         */
        expenseDescription = findViewById(R.id.etExpenseDescription);
        expenseDescription.setText(item.getDescription());
        expenseAmount = findViewById(R.id.etItemPrice);
        expenseAmount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
        expenseAmount.setText(item.getAmount());
        saveButton = findViewById(R.id.btnSaveExpenseItem);
        dueDate = findViewById(R.id.dueDate);
        completedSwitch = findViewById(R.id.completedExpenseSwitch);
        completedSwitch.setChecked(item.getCompleted());
//        picker = findViewById(R.id.tvDatePicker);

        /**
         * If statement to make due date current date/time is ther currently is none
         */
        if (item.getDatePayed() == 0 || item.getDatePayed() == -1) {
            dueDate.setText(calendar.getTime().toString());
        } else {
            calendar.setTimeInMillis(item.getDatePayed());
            dueDate.setText(calendar.getTime().toString());
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
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
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
         * Method to set listener to save button
         * Also switches view
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateItem(); // Call to populate ExpenseItem being sent back
                Intent intent = getIntent();
                intent.putExtra("ExpenseItem", item);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Method to populate item being sent back
     */
    private void populateItem() {
        item.setDescription(expenseDescription.getText().toString());
        item.setAmount(expenseAmount.getText().toString());
        item.setDatePayed(calendar.getTimeInMillis());
        item.setCompleted(completedSwitch.isChecked());
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
}
