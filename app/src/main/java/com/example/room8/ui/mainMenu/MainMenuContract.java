package com.example.room8.ui.mainMenu;

import com.example.room8.ui.dashboard.chat.ChatMessage;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;

import java.util.List;

public interface MainMenuContract {

    interface View{

        void setPresenter(Presenter presenter);

        void showCalendarItems(List<ToDoItem> calendarItem);

        void showMessages(List<ChatMessage> messages);
    }

    interface Presenter{
        void loadCalendarItems();
        void start();
    }

}
