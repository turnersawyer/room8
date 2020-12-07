package com.example.room8.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.room8.R;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static androidx.core.util.Preconditions.checkNotNull;

public class HomeFragment extends Fragment implements HomeContract.View {

    private HomeViewModel homeViewModel;
    private ToDoItemRepository toDoItemRepository;
    private CalendarItemAdapter mCalendarAdapter;
    private HomeContract.Presenter mPresenter;
    private HomeContract.View mView;
    private ListView listView;

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

        Log.d("CALENDAR", "IN ON CREATE");
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d("CALENDAR", "IN ON CREATE VIEW");
        toDoItemRepository = new ToDoItemRepository();
        mView = (HomeFragment) getFragmentManager().findFragmentById(R.id.nav_host_fragment);
        homeViewModel = ViewModelProviders.of(this, new HomeViewModel.MyViewModelFactory(toDoItemRepository,mView)).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        CalendarView calendarView = root.findViewById(R.id.calender);
        listView = root.findViewById(R.id.calendarItem);
        final TextView label = root.findViewById(R.id.toDoLabel);

        //clicked date
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                GregorianCalendar cal = new GregorianCalendar(year, month, dayOfMonth);
                homeViewModel.loadListItems(cal.getTimeInMillis());
                label.setText("Items due on " + DateFormat.format("MMMM dd yyyy", cal));
            }
        });

        if(toDoItemRepository.getCollectionPathApartment() != null){
            Calendar cal = Calendar.getInstance();
            calendarView.getDate();
            GregorianCalendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            homeViewModel.loadListItems(calendar.getTimeInMillis());
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        label.setText("Items due on " + DateFormat.format("MMMM dd yyyy", cal));

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
                rowView = inflater.inflate(R.layout.calender_row, viewGroup, false);
            }

            final ToDoItem calendarItem = getItem(i);
            TextView calendarDate = (TextView) rowView.findViewById(R.id.calendarRowDate);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
            calendarDate.setText(sdf.format(calendarItem.getDueDate()));

            TextView calendarTitle = (TextView) rowView.findViewById(R.id.calendarRowTitle);
            calendarTitle.setText(calendarItem.getTitle());

            return rowView;
        }
    }
}