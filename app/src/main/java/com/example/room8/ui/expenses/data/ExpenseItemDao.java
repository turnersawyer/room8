package com.example.room8.ui.expenses.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ExpenseItemDao {
    /**
     * Insert a expenseitem into the table
     * @return row ID for newly inserted data
     */
    @Insert
    long insert(ExpenseItem item);    /**
     * select all expenseitems
     * @return A {@link Cursor} of all expenseitems in the table
     */
    @Query("SELECT * FROM ExpenseItem")
    Cursor findAll();      /**
     * Delete a expenseitem by ID
     * @return A number of expenseitems deleted
     */
    @Query("DELETE FROM ExpenseItem WHERE id = :id ")
    int delete(long id);    /**
     * Update the expenseitem
     * @return A number of expenseitems updated
     */
    @Update
    int update(ExpenseItem expenseItem);
}