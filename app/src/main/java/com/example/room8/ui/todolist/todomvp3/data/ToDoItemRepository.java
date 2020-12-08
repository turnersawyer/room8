package com.example.room8.ui.todolist.todomvp3.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.room8.ui.dashboard.chat.ChatMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import util.AppExecutors;

/**
 * ToDoItemRepository class - implements the ToDoDataSource interface
 */
public class ToDoItemRepository implements ToDoListDataSource {

    //Thread pool for execution on other threads
    private AppExecutors mAppExecutors;
    //Context for calling ToDoProvider
    private Context mContext;
    private static FirebaseFirestore INSTANCE;
    private static String collectionPathApartment = "apartments";
    private static String collectionPathToDo = "toDoItemsCollection";
    private static String apartmentPath;


    public ToDoItemRepository(){
        getInstance();
    }

    public static FirebaseFirestore getInstance(){
        if(INSTANCE == null){
            synchronized (ToDoItemRepository.class){
                if(INSTANCE == null){
                    INSTANCE = FirebaseFirestore.getInstance();
                }
            }
        }
        return INSTANCE;
    }

    public static void setCollectionPath(String collection) {
        apartmentPath = collection;
    }

    public static String getCollectionPathApartment(){
        return apartmentPath;
    }

    /**
     * getToDoItems runs query in a separate thread, and on success loads data from cursor into a list
     * @param callback
     */
    @Override
    public void getToDoItems(@NonNull final LoadToDoItemsCallback callback) {
        Log.d("REPOSITORY","Loading...");

        INSTANCE.collection(collectionPathApartment)
                .document(apartmentPath)
                .collection(collectionPathToDo)
                .orderBy("dueDate")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        final List<ToDoItem> toDoItems = new ArrayList<ToDoItem>(0);
                        for (QueryDocumentSnapshot doc : value) {
                            ToDoItem loadedToDo = doc.toObject(ToDoItem.class);
                            toDoItems.add(loadedToDo);
                        }
                        callback.onToDoItemsLoaded(toDoItems);
                    }
                });

    }

    public static void getMissedMessages(@NonNull final String uid, @NonNull final LoadMessagesCallback callback) {

        final List<ChatMessage> messages = new ArrayList<ChatMessage>(0);

        INSTANCE.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String lastMessageId = (String) documentSnapshot.get("lastSeenMessageId");

                        INSTANCE.collection(collectionPathApartment).document(apartmentPath)
                                .collection("messages").document(lastMessageId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection("messages")
                                                .orderBy("time")
                                                .whereGreaterThan("time", documentSnapshot.get("time"))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                Log.d("Getting all items", "loaded from firebase by date: " + document.getId() + " => " + document.getData());
                                                                ChatMessage loadedMessage = document.toObject(ChatMessage.class);
                                                                if (loadedMessage.getUID() != uid) {
                                                                    messages.add(loadedMessage);
                                                                }
                                                                Log.d("Getting all items", "loaded messages: " + loadedMessage.toString());
                                                            }
                                                        } else {
                                                            Log.d("Getting all items", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        callback.onMessagesLoaded(messages);
                                                    }
                                                });
                                    }
                                });
                    }
                });

    }

    @Override
    public void getToDoItemsDate(@NonNull final Long dueDate, @NonNull final LoadToDoItemsCallback callback) {
        Log.d("REPOSITORY","GetToDoItem: " + dueDate);

        final List<ToDoItem> toDoItems = new ArrayList<ToDoItem>(0);

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathToDo)
                .whereGreaterThan("dueDate", dueDate - 1)
                .whereLessThan("dueDate", dueDate + 86400000)
                .orderBy("dueDate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("REPO", task.getResult().toString());
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

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathToDo).document(toDoItem.getId()).set(toDoItem)
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

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathToDo).document();
        String id = INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathToDo).document().getId();

        toDoItem.setId(id);

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathToDo).document(id).set(toDoItem)
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

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathToDo).document(id).delete()
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
