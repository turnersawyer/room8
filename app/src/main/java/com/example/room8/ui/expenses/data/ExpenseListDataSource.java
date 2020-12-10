package com.example.room8.ui.expenses.data;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Interface for any implementation of a ExpenseListDataSource
 * (Currently only have one - a local ContentProvider based implementation (@ExpenseItemRepository)
 */
public interface ExpenseListDataSource {

    void deleteExpenseItem(@NonNull String id);

    /**
     * LoadExpenseItemsCallback interface
     * Example of how to implement callback functions depending on the result of functions in interfaces
     * Currently, onDataNotAvailable is not implemented
     */
    interface LoadExpenseItemsCallback {

        void onExpenseItemsLoaded(List<ExpenseItem> expenseItems);

        void onDataNotAvailable();
    }

    /**
     * GetExpenseItemsCallback interface
     * Not currently implementd
     */
    interface GetExpenseItemCallback {

        void onExpenseItemLoaded(ExpenseItem task);

        void onDataNotAvailable();
    }

    /**
     * getExpenseItems loads all ExpenseItems, calls either success or failure fuction above
     * @param callback - Callback function
     */
    void getExpenseItems(@NonNull LoadExpenseItemsCallback callback);

    /**
     * getExpenseItem - Get a single ExpenseItem - currently not implemented
     * @param datePayed - String of the current ItemID to be retrieved
     * @param callback - Callback function
     */

    void getExpenseItemsDate(@NonNull Long datePayed, @NonNull LoadExpenseItemsCallback callback);

    /**
     * SaveExpenseItem saves a expenseItem to the database - No callback (should be implemented for
     * remote databases)
     * @param expenseItem
     */
    void saveExpenseItem(@NonNull final ExpenseItem expenseItem);

    /**
     * CreateExpenseItem adds a expenseItem to the database - No callback (should be implemented for
     * remote databases)
     * @param expenseItem
     */
    void createExpenseItem(@NonNull ExpenseItem expenseItem);

}
