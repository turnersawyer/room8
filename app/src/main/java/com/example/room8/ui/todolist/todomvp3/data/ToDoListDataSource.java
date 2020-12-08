package com.example.room8.ui.todolist.todomvp3.data;

import androidx.annotation.NonNull;

import com.example.room8.ui.dashboard.chat.ChatMessage;

import java.util.List;

/**
 * Interface for any implementation of a ToDoListDataSource
 * (Currently only have one - a local ContentProvider based implementation (@ToDoItemRepository)
 */
public interface ToDoListDataSource {

    void deleteToDoItem(@NonNull String id);

    /**
     * LoadToDoItemsCallback interface
     * Example of how to implement callback functions depending on the result of functions in interfaces
     * Currently, onDataNotAvailable is not implemented
     */
    interface LoadToDoItemsCallback {

        void onToDoItemsLoaded(List<ToDoItem> toDoItems);

        void onDataNotAvailable();
    }

    interface LoadMessagesCallback {

        void onMessagesLoaded(List<ChatMessage> chatMessages);

        void onDataNotAvailable();
    }

    /**
     * GetToDoItemsCallback interface
     * Not currently implementd
     */
    interface GetToDoItemCallback {

        void onToDoItemLoaded(ToDoItem task);

        void onDataNotAvailable();
    }

    /**
     * getToDoItems loads all ToDoItems, calls either success or failure fuction above
     * @param callback - Callback function
     */
    void getToDoItems(@NonNull LoadToDoItemsCallback callback);

    /**
     * getToDoItem - Get a single ToDoItem - currently not implemented
     * @param dueDate - String of the current ItemID to be retrieved
     * @param callback - Callback function
     */

    void getToDoItemsDate(@NonNull Long dueDate, @NonNull LoadToDoItemsCallback callback);

    /**
     * SaveToDoItem saves a toDoItem to the database - No callback (should be implemented for
     * remote databases)
     * @param toDoItem
     */
    void saveToDoItem(@NonNull final ToDoItem toDoItem);

    /**
     * CreateToDoItem adds a toDoItem to the database - No callback (should be implemented for
     * remote databases)
     * @param toDoItem
     */
    void createToDoItem(@NonNull ToDoItem toDoItem);

}
