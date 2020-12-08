package com.example.room8.ui.mainMenu;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.room8.ui.dashboard.chat.ChatMessage;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import com.example.room8.ui.todolist.todomvp3.data.ToDoListDataSource;

import java.sql.Timestamp;
import java.util.List;

public class MainMenuViewModel extends ViewModel {

    private ToDoItemRepository mToDoItemRepository;
    private MainMenuContract.View mMainMenuView;


    public MainMenuViewModel(@NonNull ToDoItemRepository toDoItemRepository, @NonNull MainMenuContract.View calendarItemView) {
        mMainMenuView = calendarItemView;
        mToDoItemRepository = toDoItemRepository;
    }

    public void loadListItems(Long curDate){
        mToDoItemRepository.getToDoItemsDate(curDate, new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                if(toDoItems.size()==0){
                    Log.d("TODO LIST", "empty");
                }
                mMainMenuView.showCalendarItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public void getMissedMessages(String uid) {
        mToDoItemRepository.getMissedMessages(uid, new ToDoListDataSource.LoadMessagesCallback() {
            @Override
            public void onMessagesLoaded(List<ChatMessage> chatMessages) {
                if(chatMessages.size()==0){
                    Log.d("TODO LIST", "empty");
                }
                mMainMenuView.showMessages(chatMessages);
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    public static class MyViewModelFactory implements ViewModelProvider.Factory {
        private ToDoItemRepository mtoDoItemRepository;
        private MainMenuContract.View mView;


        public MyViewModelFactory(ToDoItemRepository todo, MainMenuContract.View view) {
            mtoDoItemRepository = todo;
            mView = view;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new MainMenuViewModel(mtoDoItemRepository, mView);
        }
    }
}