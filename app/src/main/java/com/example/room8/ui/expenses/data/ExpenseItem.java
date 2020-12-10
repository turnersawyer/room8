package com.example.room8.ui.expenses.data;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * ExpenseItem class
 * Implements serializable for easy pass through between intents
 * Includes Room annotations for five columns for each of five private members
 */
@Entity
public class ExpenseItem implements Serializable {

    //Static strings for the column names usable by other classes
    public static final String EXPENSEITEM_ID = "id";
    public static final String EXPENSEITEM_DESCRIPTION = "description";
    public static final String EXPENSEITEM_AMOUNT = "amount";
    public static final String EXPENSEITEM_DATEPAYED = "datePayed";
    public static final String EXPENSEITEM_COMPLETED = "completed";


    @PrimaryKey(autoGenerate = true)
    private int idFake;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "datePayed")
    private long datePayed;

    @ColumnInfo(name = "completed")
    private boolean completed;

    //Following are getters and setters for all five member variables
    public int getIdFake() {
        return idFake;
    }

    public void setIdFake(int idFake) {
        this.idFake = idFake;
    }


    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }


    public long getDatePayed() {
        return datePayed;
    }

    public void setDatePayed(long datePayed) {
        this.datePayed = datePayed;
    }

    public boolean getCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    //Create a EXPENSEItem from a ContentValues object
    public static ExpenseItem fromContentValues(ContentValues contentValues){
        ExpenseItem item = new ExpenseItem();
        if(contentValues.containsKey(EXPENSEITEM_ID)){
            item.setId(contentValues.getAsString(EXPENSEITEM_ID));
        }
        if(contentValues.containsKey(EXPENSEITEM_DESCRIPTION)){
            item.setDescription(contentValues.getAsString(EXPENSEITEM_DESCRIPTION));
        }
        if(contentValues.containsKey(EXPENSEITEM_AMOUNT)){
            item.setAmount(contentValues.getAsString(EXPENSEITEM_AMOUNT));
        }
        if (contentValues.containsKey(EXPENSEITEM_DATEPAYED)){
            item.setDatePayed(contentValues.getAsLong(EXPENSEITEM_DATEPAYED));
        }
        if (contentValues.containsKey(EXPENSEITEM_COMPLETED)){
            item.setCompleted(contentValues.getAsBoolean(EXPENSEITEM_COMPLETED));
        }
        return item;
    }
}
