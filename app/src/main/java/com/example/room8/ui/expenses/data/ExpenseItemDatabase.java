package com.example.room8.ui.expenses.data;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

//Room Database implementation
//Don't touch unless you know what you are doing.
@Database(entities = {ExpenseItem.class}, version = 1, exportSchema = false)
public abstract class ExpenseItemDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "expense_db";
    private static ExpenseItemDatabase INSTANCE;

    public static ExpenseItemDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, ExpenseItemDatabase.class,DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public abstract ExpenseItemDao getExpenseItemDao();

}
