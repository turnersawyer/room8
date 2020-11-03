package com.example.room8.ui.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.room8.R;
import com.example.room8.ui.notifications.todomvp3.data.ToDoItemRepository;
import com.example.room8.ui.notifications.todomvp3.todolistactivity.ToDoListActivity;
import com.example.room8.ui.notifications.todomvp3.todolistactivity.ToDoListFragment;
import com.example.room8.ui.notifications.todomvp3.todolistactivity.ToDoListPresenter;

import util.AppExecutors;

import static androidx.core.util.Preconditions.checkNotNull;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private ConstraintLayout mLayout;
    private ToDoListActivity mToDoListActivity;
    private ToDoListPresenter mToDoListPresenter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

//        mToDoListActivity = new ToDoListActivity();
//
//        Intent intent = new Intent(getActivity(), ToDoListActivity.class);
//        startActivity(intent);

        return root;

    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        //Set view to use the main activity layout - a content frame that holds a single fragment
        // XXXXX  setContentView(R.layout.fragment_notifications);
        //ToDoListFragment -- Main view for the ToDoListActivity
        ToDoListFragment toDoListFragment =
                (ToDoListFragment) getFragmentManager().findFragmentById(R.id.toDoListFragmentFrame);
        ToDoListFragment toDoListFragment1 = new ToDoListFragment();
        if (toDoListFragment == null) {
            // Create the fragment
            toDoListFragment = ToDoListFragment.newInstance();
            // Check that it is not null
            checkNotNull(toDoListFragment);
            // Populate the fragment into the activity
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.toDoListFragmentFrame, toDoListFragment);
            transaction.commit();
        }
        //Get an instance of the ToDoListPresenter
        //Parameters - ToDoListRepository - Instance of the toDoListRepository
        //toDoListFragment - the View to be communicated to by the presenter
        // ToDoListRepository needs a thread pool to execute database/network calls in other threads
        // ToDoListRepository needs the application context to be able to make calls to the ContentProvider
        mToDoListPresenter = new ToDoListPresenter(ToDoItemRepository.getInstance(new AppExecutors(),getContext()),toDoListFragment);
    }

    public void onResume() {
        super.onResume();
    }
}