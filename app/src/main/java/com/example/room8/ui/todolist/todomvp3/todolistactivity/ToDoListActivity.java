package com.example.room8.ui.todolist.todomvp3.todolistactivity;

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
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;

import static androidx.core.util.Preconditions.checkNotNull;

public class ToDoListActivity extends Fragment {

    private ToDoListPresenter mToDoListPresenter;
    // Presenter instance for view
    private ToDoListContract.Presenter mPresenter;
    // private ToDoListFragment toDoListFragment;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        return root;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("ONCREATE", "INCREATE");
        View v = getView();
        //ToDoListFragment -- Main view for the ToDoListActivity
        refreshThis();
    }

    @SuppressLint("RestrictedApi")
    public void refreshThis() {
        ToDoListFragment toDoListFragment =
                (ToDoListFragment) getFragmentManager().findFragmentById(R.id.toDoListFragmentFrame);
        if (toDoListFragment == null) {
            // Create the fragment
            toDoListFragment = ToDoListFragment.newInstance();
            // Check that it is not null
            checkNotNull(toDoListFragment);
            // Populate the fragment into the activity
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.toDoListFragmentFrame, toDoListFragment);
            transaction.commit();
        } else {
            toDoListFragment = ToDoListFragment.newInstance();
            checkNotNull(toDoListFragment);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.toDoListFragmentFrame, toDoListFragment);
            // transaction.setReorderingAllowed(false);
            transaction.commit();
        }
        mToDoListPresenter = new ToDoListPresenter(new ToDoItemRepository(), toDoListFragment);
    }
}