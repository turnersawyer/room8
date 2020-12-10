package com.example.room8.ui.expenses.expenselistactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;

import com.example.room8.R;
import com.example.room8.ui.expenses.addeditexpenseitem.AddEditExpenseItemActivity;
import com.example.room8.ui.expenses.data.ExpenseItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * ExpenseListFragment implements the ExpenseListContract.View class.
 * Populates into ExpenseListActivity content frame
 */
public class ExpenseListFragment extends Fragment implements ExpenseListContract.View {

    // Presenter instance for view
    private ExpenseListContract.Presenter mPresenter;
    // Inner class instance for ListView adapter
    private ExpenseItemsAdapter mExpenseItemsAdapter;

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ExpenseListFragment.
     */
    // Expense: Rename and change types and number of parameters
    public static ExpenseListFragment newInstance() {
        ExpenseListFragment fragment = new ExpenseListFragment();
        return fragment;
    }

    /**
     * When fragment is created, create new instance of ExpenseItemsAdapter with empty ArrayList and static ExpenseItemsListener
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExpenseItemsAdapter = new ExpenseItemsAdapter(new ArrayList<ExpenseItem>(0),mExpenseItemsListener, mDeleteItemsListener, mCompleteListener);
    }

    /**
     * start presenter during onResume
     * Ideally coupled with stopping during onPause (not needed here)
     */
    @Override
    public void onResume(){
        super.onResume();
        Log.d("FRAGMENT", "IN RESUME");
        mPresenter.start();
    }

    /**
     * onCreateView inflates the fragment, finds the ListView and Button, returns the root view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_expense_list, container, false);

        // Set up tasks view
        ListView listView = root.findViewById(R.id.rvExpenseList);
        listView.setAdapter(mExpenseItemsAdapter);
        //Find button and set onClickMethod to add a New ExpenseItem
        root.findViewById(R.id.btnNewExpense).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewExpenseItem();
            }
        });
        return root;
    }

    /**
     * set the presenter for this view
     * @param presenter - the ExpenseListContract.presenter instance
     */
    @Override
    public void setPresenter(ExpenseListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Replace the items in the ExpenseItemsAdapter
     * @param expenseItemList - List of ExpenseItems
     */
    @Override
    public void showExpenseItems(List<ExpenseItem> expenseItemList) {
        mExpenseItemsAdapter.replaceData(expenseItemList);
    }

    /**
     * Create intent to start ACTIVITY TO BE IMPLEMENTED!
     * Start the activity for result - callback is onActivityResult
     * @param item - Item to be added/modified
     * @param requestCode - Integer code referencing whether a ExpenseItem is being added or edited
     */
    @Override
    public void showAddEditExpenseItem(ExpenseItem item, int requestCode) {
        Intent intent = new Intent(this.getContext(), AddEditExpenseItemActivity.class);
        intent.putExtra("item", item);
        startActivityForResult(intent, requestCode);
    }

    /**
     * callback function for startActivityForResult
     * Data intent should contain a ExpenseItem
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check to make sure data object has a expenseItem
        try {
            if(data.hasExtra("ExpenseItem")) {
                mPresenter.result(requestCode, resultCode,(ExpenseItem)data.getSerializableExtra("ExpenseItem") );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * instance of ExpenseItemsListener with onExpenseItemClick function
     */
    ExpenseItemsListener mExpenseItemsListener = new ExpenseItemsListener() {
        @Override
        public void onExpenseItemClick(ExpenseItem clickedExpenseItem) {
            Log.d("FRAGMENT","Open ExpenseItem Details");
            //Grab item from the ListView click and pass to presenter
            mPresenter.showExistingExpenseItem(clickedExpenseItem);
        }
    };
    /**
     * instance of DeleteItemsListener with onDeleteItemClick function
     */
    DeleteItemsListener mDeleteItemsListener = new DeleteItemsListener() {
        @Override
        public void onDeleteItemClick(ExpenseItem clickedExpenseItem) {
            Log.d("FRAGMENT","Delete ExpenseItem");
            //Grab item from the ListView click and pass to presenter
            mPresenter.deleteExpenseItem(clickedExpenseItem);
        }
    };
    /**
     * instance of CompleteItemListener with onCompleteClick function
     */
    CompleteItemListener mCompleteListener = new CompleteItemListener() {
        @Override
        public void onCompleteItemClick(ExpenseItem clickedExpenseItem) {
            Log.d("FRAGMENT","Complete ToDoItem");
            //Grab item from the ListView click and pass to presenter
            clickedExpenseItem.setCompleted(!clickedExpenseItem.getCompleted());
            mPresenter.updateExpenseItem(clickedExpenseItem);
        }
    };


    /**
     * Adapter for ListView to show ExpenseItems
     */
    public static class ExpenseItemsAdapter extends BaseAdapter {

        //List of all ExpenseItems
        private List<ExpenseItem> mExpenseItems;
        // Listener for onItemClick events
        private ExpenseItemsListener mItemListener;
        //Listener for deleteItem events
        private DeleteItemsListener mDeleteListener;
        //Listener for deleteItem events
        private CompleteItemListener mCompleteListener;

        /**
         * Constructor for the adapter
         * @param expenseItems - List of initial items
         * @param itemListener - onItemClick listener
         */
        public ExpenseItemsAdapter(List<ExpenseItem> expenseItems, ExpenseItemsListener itemListener,
                                   DeleteItemsListener deleteItemListener, CompleteItemListener completeItemListener) {
            setList(expenseItems);
            mItemListener = itemListener;
            mDeleteListener = deleteItemListener;
            mCompleteListener = completeItemListener;
        }

        /**
         * replace expenseItems list with new list
         * @param expenseItems
         */
        public void replaceData(List<ExpenseItem> expenseItems) {
            setList(expenseItems);
            notifyDataSetChanged();

        }

        @SuppressLint("RestrictedApi")
        private void setList(List<ExpenseItem> expenseItems) {
            mExpenseItems = checkNotNull(expenseItems);
        }

        @Override
        public int getCount() {
            return mExpenseItems.size();
        }

        @Override
        public ExpenseItem getItem(int i) {
            return mExpenseItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Get a View based on an index and viewgroup and populate
         * @param i
         * @param view
         * @param viewGroup
         * @return
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.expense_item_layout, viewGroup, false);
            }

            //get the ExpenseItem associated with a given view
            //used in the OnItemClick callback
            final ExpenseItem expenseItem = getItem(i);

            final TextView descriptionTV = rowView.findViewById(R.id.etExpenseDescription);
            descriptionTV.setText(expenseItem.getDescription());

            TextView amountTV = rowView.findViewById(R.id.etExpenseAmount);
            amountTV.setText("$" + expenseItem.getAmount());

            ToggleButton completeButton = rowView.findViewById(R.id.completeExpenseButton);
            completeButton.setChecked(expenseItem.getCompleted());

            TextView dueDateTV = rowView.findViewById(R.id.etItemDatePayed);
            dueDateTV.setText("Due Date: " + DateFormat.format("MMMM dd yyyy", expenseItem.getDatePayed()));

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            final long currentDate = cal.getTimeInMillis();

            if (expenseItem.getDatePayed() < currentDate + 86400000 && expenseItem.getCompleted() == false) {
                descriptionTV.setTextColor(Color.RED);
            } else {
                descriptionTV.setTextColor(Color.BLACK);
            }

            /**
             * Sets listener to delete button
             */
            ImageButton deleteButton = rowView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set delete listener
                    mDeleteListener.onDeleteItemClick(expenseItem);
                }
            });

            /**
             * Sets listener to item click
             */
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set onItemClick listener
                    mItemListener.onExpenseItemClick(expenseItem);
                }
            });

            /**
             * Sets listener to complete click
             */
            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set complete listener
                    mCompleteListener.onCompleteItemClick(expenseItem);
                    if (expenseItem.getDatePayed() < currentDate + 86400000 && !expenseItem.getCompleted()) {
                        descriptionTV.setTextColor(Color.RED);
                    } else {
                        descriptionTV.setTextColor(Color.BLACK);
                    }
                }
            });



            return rowView;
        }
    }

    public interface ExpenseItemsListener {
        void onExpenseItemClick(ExpenseItem clickedExpenseItem);
    }

    public interface DeleteItemsListener {
        void onDeleteItemClick(ExpenseItem clickedExpenseItem);
    }

    public interface CompleteItemListener {
        void onCompleteItemClick(ExpenseItem clickedExpenseItem);
    }



}