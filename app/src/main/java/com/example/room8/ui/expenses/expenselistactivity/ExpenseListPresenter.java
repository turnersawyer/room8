package com.example.room8.ui.expenses.expenselistactivity;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.room8.ui.expenses.data.ExpenseItem;
import com.example.room8.ui.expenses.data.ExpenseItemRepository;
import com.example.room8.ui.expenses.data.ExpenseListDataSource;

import java.util.List;

/**
 * ExpenseListPresenter -- Implements the Presenter interface from ExpenseListContract Presenter
 */
public class ExpenseListPresenter implements ExpenseListContract.Presenter {

    //Data repository instance
    //Currently has a memory leak -- need to refactor context passing
    private static ExpenseItemRepository mExpenseItemRepository;
    //View instance
    private final ExpenseListContract.View mExpenseItemView;

    // Integer request codes for creating or updating through the result method
    private static final int CREATE_EXPENSE_REQUEST = 0;
    private static final int UPDATE_EXPENSE_REQUEST = 1;

    /**
     * ExpenseListPresenter constructor
     * @param expenseItemRepository - Data repository instance
     * @param expenseItemView - ExpenseListContract.View instance
     */
    public ExpenseListPresenter(@NonNull ExpenseItemRepository expenseItemRepository, @NonNull ExpenseListContract.View expenseItemView){
        mExpenseItemRepository = expenseItemRepository;
        mExpenseItemView = expenseItemView;
        //Make sure to pass the presenter into the view!
        mExpenseItemView.setPresenter(this);
    }

    @Override
    public void start(){
        //Load all expenseItems
        loadExpenseItems();
    }


    @Override
    public void addNewExpenseItem() {
        //Create stub ExpenseItem with temporary data
        ExpenseItem item = new ExpenseItem();
        item.setDescription("Description");
        item.setAmount("0.00");
        item.setDatePayed(0);
        item.setCompleted(false);
        item.setId(null);
        //Show AddEditExpenseItemActivity with a create request and temporary item
        mExpenseItemView.showAddEditExpenseItem(item,CREATE_EXPENSE_REQUEST);
    }

    @Override
    public void showExistingExpenseItem(ExpenseItem item) {
        //Show AddEditExpenseItemActivity with a edit request, passing through an item
       Log.d("ExpenseListPresenter", "Expense: Show Existing ExpenseItem");
       mExpenseItemView.showAddEditExpenseItem(item,UPDATE_EXPENSE_REQUEST);
    }

    @Override
    public void result(int requestCode, int resultCode, ExpenseItem item) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_EXPENSE_REQUEST){
                createExpenseItem(item);
            }else if(requestCode == UPDATE_EXPENSE_REQUEST){
                updateExpenseItem(item);
            }else{
                Log.e("ExpensePresenter", "No such request!");
            }
        }
    }

    /**
     * Create ExpenseItem in repository from ExpenseItem and reload data
     * @param item - item to be placed in the data repository
     */
    private void createExpenseItem(ExpenseItem item){
        try {
            mExpenseItemRepository.createExpenseItem(item);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update ExpenseItem in repository from ExpenseItem and reload data
     * @param item -- ExpenseItem to be updated in the ExpenseItemRepository
     */
    @Override
    public void updateExpenseItem(ExpenseItem item){
        Log.d("ExpenseListPresenter", "Expense: Update Item");
        try {
            mExpenseItemRepository.saveExpenseItem(item);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loadExpenseItems -- loads all items from ExpenseItemRepository
     * Two callbacks -- success/failure
     */
    @Override
    public void loadExpenseItems(){
        Log.d("ExpenseListPresenter","Loading ExpenseItems");
        mExpenseItemRepository.getExpenseItems(new ExpenseListDataSource.LoadExpenseItemsCallback() {
            @Override
            public void onExpenseItemsLoaded(List<ExpenseItem> expenseItems) {
                Log.d("PRESENTER","Loaded");
                Log.d("PRESENTER", String.valueOf(expenseItems.size()));
                ////////////////////////////////////
                // Remove the following lines
                // Just for demonstration
                ////////////////////////////////////
                if (expenseItems.size() == 0){

                }
                mExpenseItemView.showExpenseItems(expenseItems);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Not Loaded");
            }
        });
    }

    @Override
    public void deleteExpenseItem(ExpenseItem item){
        Log.d("ExpenseListPresenter", "Expense: Delete Item");
        try {
            mExpenseItemRepository.deleteExpenseItem(item.getId());
        } catch(Exception e) {
            e.printStackTrace();
        }
        loadExpenseItems();
    }
}
