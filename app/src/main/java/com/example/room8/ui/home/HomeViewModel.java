package com.example.room8.ui.home;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import com.example.room8.ui.todolist.todomvp3.data.ToDoListDataSource;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private ToDoItemRepository mToDoItemRepository;
    private HomeContract.View mCalendarItemView;


    public HomeViewModel(@NonNull ToDoItemRepository toDoItemRepository, @NonNull HomeContract.View calendarItemView) {
        mToDoItemRepository = toDoItemRepository;
        mCalendarItemView = calendarItemView;
    }


    public void loadListItems(Long curDate){
        mToDoItemRepository.getToDoItemsDate(curDate, new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                if(toDoItems.size()==0){
                    Log.d("CALENDAR", "empty");
                }
                mCalendarItemView.showCalendarItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public static class MyViewModelFactory implements ViewModelProvider.Factory {
        private ToDoItemRepository mtoDoItemRepository;
        private HomeContract.View mView;


        public MyViewModelFactory(ToDoItemRepository toDoItemRepository, HomeContract.View view) {
            mtoDoItemRepository = toDoItemRepository;
            mView = view;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new HomeViewModel(mtoDoItemRepository, mView);
        }
    }
}