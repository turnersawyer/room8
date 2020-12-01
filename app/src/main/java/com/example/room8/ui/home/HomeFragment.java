package com.example.room8.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.room8.R;
import com.example.room8.ui.todolist.todomvp3.addedittodoitem.AddEditToDoItemActivity;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import util.AppExecutors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static androidx.core.util.Preconditions.checkNotNull;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeViewModel homeViewModel;
    private ToDoItemRepository toDoItemRepository;
    String curDate;
    private CalendarItemAdapter mCalendarAdapter;
    private HomeContract.Presenter mPresenter;
    private HomeContract.View mView;

    public HomeFragment() {
    }
    public static HomeFragment newInstance(){
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCalendarAdapter = new CalendarItemAdapter(new ArrayList<ToDoItem>(0));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toDoItemRepository = new ToDoItemRepository(new AppExecutors(), getContext());
        mView = (HomeFragment) getFragmentManager().findFragmentById(R.id.nav_host_fragment);
        homeViewModel = ViewModelProviders.of(this, new HomeViewModel.MyViewModelFactory(toDoItemRepository,mView)).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final CalendarView calendarView = root.findViewById(R.id.calender);
        final ListView listView = root.findViewById(R.id.calendarItem);
        //clicked date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                GregorianCalendar cal = new GregorianCalendar(year, month, dayOfMonth);
                homeViewModel.loadListItems(cal.getTimeInMillis());

            }
        });

        listView.setAdapter(mCalendarAdapter);

        return root;
    }



    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        mPresenter=presenter;
    }

    @Override
    public void showCalendarItems(List<ToDoItem> calendarItem) {
        mCalendarAdapter.replaceData(calendarItem);
    }
    public static class CalendarItemAdapter extends BaseAdapter{

        private List<ToDoItem> mCalendarItem;

        public CalendarItemAdapter(List<ToDoItem> calendarItems){
            setList(calendarItems);
        }

        public void replaceData(List<ToDoItem> calendarItems){
            setList(calendarItems);
            notifyDataSetChanged();
        }

        @SuppressLint("RestrictedApi")
        private void setList(List<ToDoItem> calendarItems){
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
            if(rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.fragment_dashboard, viewGroup, false);
            }

            final ToDoItem calendarItem = getItem(i);
            TextView calendarDate = (TextView) rowView.findViewById(R.id.tvDate);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            calendarDate.setText(sdf.format(calendarItem.getDueDate()));

            TextView calendarTitle = (TextView) rowView.findViewById(R.id.tvTitle);
            calendarTitle.setText(calendarItem.getTitle());

            return rowView;
        }
    }
}