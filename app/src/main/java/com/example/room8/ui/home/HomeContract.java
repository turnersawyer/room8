package com.example.room8.ui.home;

import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;

import java.util.List;

public interface HomeContract {

    interface View{

        void setPresenter(Presenter presenter);

        void showCalendarItems(List<ToDoItem> calendarItem);
    }

    interface Presenter{
        void loadCalendarItems();
        void start();
    }

}
