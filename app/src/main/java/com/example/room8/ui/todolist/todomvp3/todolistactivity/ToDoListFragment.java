package com.example.room8.ui.todolist.todomvp3.todolistactivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Intent;
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
import com.example.room8.ui.todolist.todomvp3.addedittodoitem.AddEditToDoItemActivity;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * ToDoListFragment implements the ToDoListContract.View class.
 * Populates into ToDoListActivity content frame
 */
public class ToDoListFragment extends Fragment implements ToDoListContract.View {

    // Presenter instance for view
    private ToDoListContract.Presenter mPresenter;
    // Inner class instance for ListView adapter
    private ToDoItemsAdapter mToDoItemsAdapter;

    AlarmManager alarmManager;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ToDoListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoListFragment newInstance() {
        ToDoListFragment fragment = new ToDoListFragment();
        return fragment;
    }

    /**
     * When fragment is created, create new instance of ToDoItemsAdapter with empty ArrayList and static ToDoItemsListener
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToDoItemsAdapter = new ToDoItemsAdapter(new ArrayList<ToDoItem>(0),mToDoItemsListener, mDeleteItemsListener, mCompleteListener);
    }

    /**
     * start presenter during onResume
     * Ideally coupled with stopping during onPause (not needed here)
     */
    @Override
    public void onResume(){
        super.onResume();
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
        View root = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        // Set up tasks view
        ListView listView = (ListView) root.findViewById(R.id.rvToDoList);
        listView.setAdapter(mToDoItemsAdapter);
        //Find button and set onClickMethod to add a New ToDoItem
        root.findViewById(R.id.btnNewToDo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewToDoItem();
            }
        });
        return root;
    }

    /**
     * set the presenter for this view
     * @param presenter - the ToDoListContract.presenter instance
     */
    @Override
    public void setPresenter(ToDoListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Replace the items in the ToDoItemsAdapter
     * @param toDoItemList - List of ToDoItems
     */
    @Override
    public void showToDoItems(List<ToDoItem> toDoItemList) {
        mToDoItemsAdapter.replaceData(toDoItemList);
    }

    /**
     * Create intent to start ACTIVITY TO BE IMPLEMENTED!
     * Start the activity for result - callback is onActivityResult
     * @param item - Item to be added/modified
     * @param requestCode - Integer code referencing whether a ToDoItem is being added or edited
     */
    @Override
    public void showAddEditToDoItem(ToDoItem item, int requestCode) {
        Intent intent = new Intent(this.getContext(), AddEditToDoItemActivity.class);
        intent.putExtra("item", item);
        startActivityForResult(intent, requestCode);
    }

    /**
     * callback function for startActivityForResult
     * Data intent should contain a ToDoItem
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check to make sure data object has a toDoItem
        try {
            if(data.hasExtra("ToDoItem")) {
                mPresenter.result(requestCode, resultCode,(ToDoItem)data.getSerializableExtra("ToDoItem") );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * instance of ToDoItemsListener with onToDoItemClick function
     */
    ToDoItemsListener mToDoItemsListener = new ToDoItemsListener() {
        @Override
        public void onToDoItemClick(ToDoItem clickedToDoItem) {
            Log.d("FRAGMENT","Open ToDoItem Details");
            //Grab item from the ListView click and pass to presenter
            mPresenter.showExistingToDoItem(clickedToDoItem);
        }
    };
    /**
     * instance of DeleteItemsListener with onDeleteItemClick function
     */
    DeleteItemsListener mDeleteItemsListener = new DeleteItemsListener() {
        @Override
        public void onDeleteItemClick(ToDoItem clickedToDoItem) {
            Log.d("FRAGMENT","Delete ToDoItem");
            //Grab item from the ListView click and pass to presenter
            mPresenter.deleteToDoItem(clickedToDoItem);
        }
    };
    /**
     * instance of CompleteItemListener with onCompleteClick function
     */
    CompleteItemListener mCompleteListener = new CompleteItemListener() {
        @Override
        public void onCompleteItemClick(ToDoItem clickedToDoItem) {
            Log.d("FRAGMENT","Complete ToDoItem");
            //Grab item from the ListView click and pass to presenter
            clickedToDoItem.setCompleted(!clickedToDoItem.getCompleted());
            mPresenter.updateToDoItem(clickedToDoItem);
        }
    };

    /**
     * Adapter for ListView to show ToDoItems
     */
    private static class ToDoItemsAdapter extends BaseAdapter {

        //List of all ToDoItems
        private List<ToDoItem> mToDoItems;
        // Listener for onItemClick events
        private ToDoItemsListener mItemListener;
        //Listener for deleteItem events
        private DeleteItemsListener mDeleteListener;
        //Listener for deleteItem events
        private CompleteItemListener mCompleteListener;

        /**
         * Constructor for the adapter
         * @param toDoItems - List of initial items
         * @param itemListener - onItemClick listener
         */
        public ToDoItemsAdapter(List<ToDoItem> toDoItems, ToDoItemsListener itemListener,
                                DeleteItemsListener deleteItemListener, CompleteItemListener completeItemListener) {
            setList(toDoItems);
            mItemListener = itemListener;
            mDeleteListener = deleteItemListener;
            mCompleteListener = completeItemListener;
        }

        /**
         * replace toDoItems list with new list
         * @param toDoItems
         */
        public void replaceData(List<ToDoItem> toDoItems) {
            setList(toDoItems);
            notifyDataSetChanged();
        }

        @SuppressLint("RestrictedApi")
        private void setList(List<ToDoItem> toDoItems) {
            mToDoItems = checkNotNull(toDoItems);
        }

        @Override
        public int getCount() {
            return mToDoItems.size();
        }

        @Override
        public ToDoItem getItem(int i) {
            return mToDoItems.get(i);
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
                rowView = inflater.inflate(R.layout.to_do_item_layout, viewGroup, false);
            }

            //get the ToDoItem associated with a given view
            //used in the OnItemClick callback
            final ToDoItem toDoItem = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.etItemTitle);
            titleTV.setText(toDoItem.getTitle());

            TextView contentTV = (TextView) rowView.findViewById(R.id.etItemContent);
            contentTV.setText(toDoItem.getContent());

            ToggleButton completeButton = rowView.findViewById(R.id.completeButton);
            completeButton.setChecked(toDoItem.getCompleted());

            TextView dueDateTV = (TextView) rowView.findViewById(R.id.etItemDueDate);
            dueDateTV.setText("Due Date: " + DateFormat.format("MMMM dd yyyy h:mmaa", toDoItem.getDueDate()));

            /**
             * Sets listener to delete button
             */
            ImageButton deleteButton = rowView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set delete listener
                    mDeleteListener.onDeleteItemClick(toDoItem);
//                    AlarmManager alarmManager;
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        alarmManager = view.getContext().getSystemService(AlarmManager.class);
//                    } else {
//                        alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
//                    }
//                    Intent alarmNotificationIntent = new Intent(view.getContext(), AlarmNotification.class);
//                    PendingIntent alarmIntent = PendingIntent.getBroadcast(view.getContext(), toDoItem.getId(), alarmNotificationIntent, 0);
//                    alarmManager.cancel(alarmIntent);
                }
            });

            /**
             * Sets listener to item click
             */
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set onItemClick listener
                    mItemListener.onToDoItemClick(toDoItem);
                }
            });

            /**
             * Sets listener to complete click
             */
            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set complete listener
                    mCompleteListener.onCompleteItemClick(toDoItem);
                }
            });

//            if (toDoItem.getCompleted() == false) {
//                setAlarm(toDoItem.getDueDate(), toDoItem.getId(), rowView, toDoItem.getTitle());
//            }

            return rowView;
        }

//        public void setAlarm(long dueDate, int id, View view, String title) {
//
//            Calendar testCal = Calendar.getInstance();
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(dueDate);
//            //testCal.setTimeInMillis(dueDate);
//
//            //if (testCal.get(Calendar.YEAR))
//
//            AlarmManager alarmManager;
//            testCal.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
//            testCal.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
//            testCal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
//            testCal.set(Calendar.MINUTE, 0);
//            testCal.set(Calendar.HOUR, 0);
//            //testCal.add(Calendar.SECOND, 10);
//
//
//            if (Build.VERSION.SDK_INT >= 23) {
//                alarmManager = view.getContext().getSystemService(AlarmManager.class);
//            } else {
//                alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
//            }
//            Intent alarmNotificationIntent = new Intent(view.getContext(), AlarmNotification.class);
//            alarmNotificationIntent.putExtra("todoTitle", title);
//            PendingIntent alarmIntent = PendingIntent.getBroadcast(view.getContext(), id, alarmNotificationIntent, 0);
//            alarmManager.cancel(alarmIntent);
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, testCal.getTimeInMillis(), alarmIntent);
//            Log.d("EDIT", "SET ALARM FOR " + DateFormat.format("mm dd yyyy h:mmaa", testCal));
//        }
    }

    public interface ToDoItemsListener {
        void onToDoItemClick(ToDoItem clickedToDoItem);
    }

    public interface DeleteItemsListener {
        void onDeleteItemClick(ToDoItem clickedToDoItem);
    }

    public interface CompleteItemListener {
        void onCompleteItemClick(ToDoItem clickedToDoItem);
    }
    
}