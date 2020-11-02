package com.example.room8.ui.notifications.todomvp3.todolistactivity;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.room8.ui.notifications.todomvp3.data.ToDoItem;
import com.example.room8.ui.notifications.todomvp3.data.ToDoItemRepository;
import com.example.room8.ui.notifications.todomvp3.data.ToDoListDataSource;

import java.util.List;

/**
 * ToDoListPresenter -- Implements the Presenter interface from ToDoListContract Presenter
 */
public class ToDoListPresenter implements ToDoListContract.Presenter {

    //Data repository instance
    //Currently has a memory leak -- need to refactor context passing
    private static ToDoItemRepository mToDoItemRepository;
    //View instance
    private final ToDoListContract.View mToDoItemView;

    // Integer request codes for creating or updating through the result method
    private static final int CREATE_TODO_REQUEST = 0;
    private static final int UPDATE_TODO_REQUEST = 1;

    /**
     * ToDoListPresenter constructor
     * @param toDoItemRepository - Data repository instance
     * @param toDoItemView - ToDoListContract.View instance
     */
    public ToDoListPresenter(@NonNull ToDoItemRepository toDoItemRepository, @NonNull ToDoListContract.View toDoItemView){
        mToDoItemRepository = toDoItemRepository;
        mToDoItemView = toDoItemView;
        //Make sure to pass the presenter into the view!
        mToDoItemView.setPresenter(this);
    }

    @Override
    public void start(){
        //Load all toDoItems
        loadToDoItems();
    }


    @Override
    public void addNewToDoItem() {
        //Create stub ToDoItem with temporary data
        ToDoItem item = new ToDoItem();
        item.setTitle("Title");
        item.setContent("Content");
        item.setCompleted(false);
        item.setDueDate(0);
        item.setId(-1);
        //Show AddEditToDoItemActivity with a create request and temporary item
        mToDoItemView.showAddEditToDoItem(item,CREATE_TODO_REQUEST);
    }

    @Override
    public void showExistingToDoItem(ToDoItem item) {
        //Show AddEditToDoItemActivity with a edit request, passing through an item
       Log.d("ToDoListPresenter", "TODO: Show Existing ToDoItem");
       mToDoItemView.showAddEditToDoItem(item,UPDATE_TODO_REQUEST);
    }

    @Override
    public void result(int requestCode, int resultCode, ToDoItem item) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_TODO_REQUEST){
                createToDoItem(item);
            }else if(requestCode == UPDATE_TODO_REQUEST){
                updateToDoItem(item);
            }else{
                Log.e("ToDoPresenter", "No such request!");
            }
        }
    }

    /**
     * Create ToDoItem in repository from ToDoItem and reload data
     * @param item - item to be placed in the data repository
     */
    private void createToDoItem(ToDoItem item){
        try {
            mToDoItemRepository.createToDoItem(item);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update ToDoItem in repository from ToDoItem and reload data
     * @param item -- ToDoItem to be updated in the ToDoItemRepository
     */
    @Override
    public void updateToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter", "TODO: Update Item");
        try {
            mToDoItemRepository.saveToDoItem(item);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loadToDoItems -- loads all items from ToDoItemRepository
     * Two callbacks -- success/failure
     */
    @Override
    public void loadToDoItems(){
        Log.d("ToDoListPresenter","Loading ToDoItems");
        mToDoItemRepository.getToDoItems(new ToDoListDataSource.LoadToDoItemsCallback() {
            @Override
            public void onToDoItemsLoaded(List<ToDoItem> toDoItems) {
                Log.d("PRESENTER","Loaded");
                ////////////////////////////////////
                // Remove the following lines
                // Just for demonstration
                ////////////////////////////////////
                if (toDoItems.size() == 0){

                }
                mToDoItemView.showToDoItems(toDoItems);
            }

            @Override
            public void onDataNotAvailable() {
                Log.d("PRESENTER","Not Loaded");
            }
        });
    }

    @Override
    public void deleteToDoItem(ToDoItem item){
        Log.d("ToDoListPresenter", "TODO: Delete Item");
        try {
            mToDoItemRepository.deleteToDoItem(item.getId());
        } catch(Exception e) {
            e.printStackTrace();
        }
        loadToDoItems();
    }
}
