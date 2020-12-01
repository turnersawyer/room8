package com.example.room8.ui.todolist.todomvp3.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import util.AppExecutors;

/**
 * ToDoItemRepository class - implements the ToDoDataSource interface
 */
public class ToDoItemRepository implements ToDoListDataSource {

    //Memory leak here by including the context - Fix this at some point
    private static volatile ToDoItemRepository INSTANCE2; //uncomment

    //Thread pool for execution on other threads
    private AppExecutors mAppExecutors;
    //Context for calling ToDoProvider
    private Context mContext;
    private static FirebaseFirestore INSTANCE = FirebaseFirestore.getInstance();;
    // private String collectionPath = "toDoItemsCollection";
    private static String collectionPath = "toDoItemsCollection";
    private static CollectionReference toDoItemsCollection = INSTANCE.collection(collectionPath);


    public ToDoItemRepository(){
        getInstance();
    }


    public static FirebaseFirestore getInstance(){
        if(INSTANCE == null){
            synchronized (ToDoItemRepository.class){
                if(INSTANCE == null){
                    INSTANCE = FirebaseFirestore.getInstance();
                    if(toDoItemsCollection == null){
                        toDoItemsCollection = INSTANCE.collection("toDoItemsCollection");
                    }
                }
            }
        }
        return INSTANCE;
    }

    /**
     * getToDoItems runs query in a separate thread, and on success loads data from cursor into a list
     * @param callback
     */
    @Override
    public void getToDoItems(@NonNull final LoadToDoItemsCallback callback) {
        Log.d("REPOSITORY","Loading...");

        final List<ToDoItem> toDoItems = new ArrayList<ToDoItem>(0);

        INSTANCE.collection(collectionPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Getting all items", "loading all from firebase: " + document.getId() + " => " + document.getData());
                                ToDoItem loadedToDo = document.toObject(ToDoItem.class);
                                toDoItems.add(loadedToDo);
                                Log.d("Getting all items", "loaded todo: " + loadedToDo.getId());
                            }
                        } else {
                            Log.d("Getting all items", "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        callback.onToDoItemsLoaded(toDoItems);
                    }
                });

    }


    @Override
    public void getToDoItemsDate(@NonNull final Long dueDate, @NonNull final LoadToDoItemsCallback callback) {
        Log.d("REPOSITORY","GetToDoItem: " + dueDate);

        final List<ToDoItem> toDoItems = new ArrayList<ToDoItem>(0);

        INSTANCE.collection(collectionPath)
                .whereGreaterThan("dueDate", dueDate - 1)
                .whereLessThan("dueDate", dueDate + 86400000)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Getting all items", "loaded from firebase by date: " + document.getId() + " => " + document.getData());
                                ToDoItem loadedToDo = document.toObject(ToDoItem.class);
                                toDoItems.add(loadedToDo);
                                Log.d("Getting all items", "loaded todo: " + loadedToDo.toString());
                            }
                        } else {
                            Log.d("Getting all items", "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        callback.onToDoItemsLoaded(toDoItems);
                    }
        });
    }

    /**
     * saveToDoItem runs contentProvider update in separate thread
     * @param toDoItem
     */
    @Override
    public void saveToDoItem(@NonNull final ToDoItem toDoItem) {
        Log.d("REPOSITORY","SaveToDoItem");

        INSTANCE.collection(collectionPath).document(toDoItem.getId().toString()).set(toDoItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Saving todo", "todo successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Saving todo", "Error writing document", e);
                    }
                });
    }

    /**
     * createToDoItem runs contentProvider insert in separate thread
     * @param toDoItem
     */
    @Override
    public void createToDoItem(@NonNull final ToDoItem toDoItem) {
        Log.d("REPOSITORY","CreateToDoItem");

        INSTANCE.collection(collectionPath).document();
        String id = INSTANCE.collection(collectionPath).document().getId();

        toDoItem.setId(id);

        INSTANCE.collection(collectionPath).document(id).set(toDoItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Creating todo", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Creating todo", "Error writing document", e);
                    }
                });
    }

    /**
     * Method to facilitate delete item
     * @param id
     */
    @Override
    public void deleteToDoItem(@NonNull final String id) {

        INSTANCE.collection(collectionPath).document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleting todo", id + " successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("deleting todo", "Error writing document", e);
                    }
                });
    }
}
