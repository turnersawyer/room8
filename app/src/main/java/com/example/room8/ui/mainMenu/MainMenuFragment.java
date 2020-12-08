package com.example.room8.ui.mainMenu;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.room8.R;
import com.example.room8.ui.dashboard.chat.ChatMessage;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainMenuFragment extends Fragment implements MainMenuContract.View {

    private FirebaseFirestore INSTANCE;
    private FirebaseAuth mAuth;
    private ListView listView;
    private ToDoItemRepository toDoItemRepository;
    private MainMenuViewModel mainMenuViewModel;
    private MainMenuContract.View mView;
    private CalendarItemAdapter mCalendarAdapter;
    private MainMenuContract.Presenter mPresenter;
    private TextView missedTV;

    public MainMenuFragment() {
        // Required empty public constructor
    }

    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mCalendarAdapter = new MainMenuFragment.CalendarItemAdapter(new ArrayList<ToDoItem>(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        toDoItemRepository = new ToDoItemRepository();
        mView = (MainMenuFragment) getFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mainMenuViewModel = ViewModelProviders.of(this, new MainMenuViewModel.MyViewModelFactory(toDoItemRepository, mView)).get(MainMenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_main_menu, container, false);

        String uid = "";

        TextView titleTV = (TextView) root.findViewById(R.id.welcomeTV);
        if (mAuth.getCurrentUser() != null) {
            titleTV.setText("Welcome " + mAuth.getCurrentUser().getDisplayName() + ",");
            uid = mAuth.getCurrentUser().getUid();
        }

        listView = root.findViewById(R.id.itemListView);

        if(toDoItemRepository.getCollectionPathApartment() != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            mainMenuViewModel.loadListItems(cal.getTimeInMillis());
        }

        listView.setAdapter(mCalendarAdapter);

        if(toDoItemRepository.getCollectionPathApartment() != null && mAuth.getCurrentUser() != null) {
            mainMenuViewModel.getMissedMessages(uid);
        }

        missedTV = (TextView) root.findViewById(R.id.messagesTV);
        missedTV.setText("No missed messages.");


        return root;
    }

    @Override
    public void showMessages(List<ChatMessage> messages) {

        int missedMessages = messages.size();

        if (missedMessages == 0) {
            Log.d("MESSAGES", "NO MESSAGE");
            missedTV.setText("No missed messages.");
        } else if (missedMessages > 0) {
            Log.d("MESSAGES", "IS MESSAGE");
            missedTV.setText(missedMessages + " missed messages!");
        }

    }

    @Override
    public void setPresenter(MainMenuContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void showCalendarItems(List<ToDoItem> calendarItem) {
        mCalendarAdapter.replaceData(calendarItem);
    }

    public static class CalendarItemAdapter extends BaseAdapter {

        private List<ToDoItem> mCalendarItem;

        public CalendarItemAdapter(List<ToDoItem> calendarItems) {
            setList(calendarItems);
        }

        public void replaceData(List<ToDoItem> calendarItems) {
            setList(calendarItems);
            notifyDataSetChanged();
        }

        @SuppressLint("RestrictedApi")
        private void setList(List<ToDoItem> calendarItems) {
            mCalendarItem = checkNotNull(calendarItems);
        }

        @Override
        public int getCount() {
            return mCalendarItem.size();
        }

        @Override
        public ToDoItem getItem(int i) {
            return mCalendarItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.calender_row, viewGroup, false);
            }

            final ToDoItem calendarItem = getItem(i);
            TextView calendarDate = (TextView) rowView.findViewById(R.id.calendarRowDate);
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            calendarDate.setText(sdf.format(calendarItem.getDueDate()));

            TextView calendarTitle = (TextView) rowView.findViewById(R.id.calendarRowTitle);

            if(calendarItem.getCompleted() == false){
                calendarTitle.setTextColor(Color.RED);
            } else {
                calendarTitle.setTextColor(Color.BLACK);
            }

            calendarTitle.setText(calendarItem.getTitle());
            Log.d("CALENDAR", "TITLE: " + calendarItem.getTitle());

            return rowView;
        }
    }
}