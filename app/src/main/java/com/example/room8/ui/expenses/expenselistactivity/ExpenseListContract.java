package com.example.room8.ui.expenses.expenselistactivity;

import com.example.room8.ui.expenses.data.ExpenseItem;

import java.util.List;

/**
 * ExpenseListContract
 * Two inner interfaces, a View and a Presenter for the ExpenseListActivity
 */
public interface ExpenseListContract {

    interface View{
        /**
         * setPresenter - sets the presenter associated with a View
         * @param presenter - the ExpenseListContract.presenter instance
         */
        void setPresenter(Presenter presenter);

        /**
         * showExpenseItems - takes a list of expenseItems and populates a ListView
         * @param expenseItemList - List of ExpenseItems
         */
        void showExpenseItems(List<ExpenseItem> expenseItemList);

        /**
         * showAddEditExpenseItem - Creates an intent object to launch add or edit to do item activity
         * @param item - Item to be added/modified
         * @param requestCode - Integer code referencing whether a ExpenseItem is being added or edited
         */
        void showAddEditExpenseItem(ExpenseItem item, int requestCode);
    }

    interface Presenter{
        /**
         * loadExpenseItems - Loads all ExpenseItems from the ExpenseItemsRepository
         */
        void loadExpenseItems();

        /**
         * start -- All procedures that need to be started
         * Ideally, should be coupled with a stop if any running tasks need to be destroyed.
         */
        void start();

        /**
         * addNewExpenseItem -- Create a new ExpenseItem with stub values
         * Calls showAddEditExpenseItem with created item and adding item integer
         */
        void addNewExpenseItem();

        /**
         * showExistingExpenseItem -- Edit an existing expenseItem
         * Calls showAddEditExpenseItem with existing item and editing item integer
         * @param item - Item to be edited
         */
        void showExistingExpenseItem(ExpenseItem item);

        /**
         * updateExpenseItem -- Item to be updated in the dataRepository
         * @param item -- ExpenseItem to be updated in the ExpenseItemRepository
         */
        void updateExpenseItem(ExpenseItem item);

        /**
         * result -- Passthrough from View
         * Takes the requestCode, resultCode, and the returned ExpenseItem from a call to showAddEditExpenseItem
         * on an OK result, and either creates or updates item in the repository
         * @param requestCode -- Integer code identifying whether it was an update or edit call
         * @param resultCode -- Integer code identifying the result from the Intent
         * @param item -- ExpenseItem returned from the AddEditExpenseItemActivity
         */
        void result(int requestCode, int resultCode, ExpenseItem item);

        void deleteExpenseItem(ExpenseItem item);
    }

}
