package com.example.room8.ui.expenses.expenselistactivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.room8.R;
import com.example.room8.ui.expenses.data.ExpenseItemRepository;

import static androidx.core.util.Preconditions.checkNotNull;

public class ExpenseListActivity extends Fragment {

    private ExpenseListPresenter mExpenseListPresenter;
    // Presenter instance for view
    private ExpenseListContract.Presenter mPresenter;
    // private ExpenseListFragment ExpenseListFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_expense, container, false);
        return root;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("ONCREATE", "INCREATE");
        View v = getView();
        //ExpenseListFragment -- Main view for the ExpenseListActivity
        refreshThis();
    }

    @SuppressLint("RestrictedApi")
    public void refreshThis() {
        ExpenseListFragment expenseListFragment =
                (ExpenseListFragment) getFragmentManager().findFragmentById(R.id.expenseListFragmentFrame);
        if (expenseListFragment == null) {
            // Create the fragment
            expenseListFragment = ExpenseListFragment.newInstance();
            // Check that it is not null
            checkNotNull(expenseListFragment);
            // Populate the fragment into the activity
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.expenseListFragmentFrame, expenseListFragment);
            transaction.commit();
        } else {
            expenseListFragment = ExpenseListFragment.newInstance();
            checkNotNull(expenseListFragment);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.expenseListFragmentFrame, expenseListFragment);
            // transaction.setReorderingAllowed(false);
            transaction.commit();
        }
        mExpenseListPresenter = new ExpenseListPresenter(new ExpenseItemRepository(), expenseListFragment);
    }
}