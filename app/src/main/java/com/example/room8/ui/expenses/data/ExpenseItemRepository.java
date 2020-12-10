package com.example.room8.ui.expenses.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import util.AppExecutors;

/**
 * ExpenseItemRepository class - implements the ExpenseDataSource interface
 */
public class ExpenseItemRepository implements ExpenseListDataSource {

    //Thread pool for execution on other threads
    private AppExecutors mAppExecutors;
    //Context for calling ExpenseProvider
    private Context mContext;
    private static FirebaseFirestore INSTANCE;
    private static String collectionPathApartment = "apartments";
    private static String collectionPathExpense = "expenseItemsCollection";
    private static String apartmentPath;


    public ExpenseItemRepository(){
        getInstance();
    }

    public static FirebaseFirestore getInstance(){
        if(INSTANCE == null){
            synchronized (ExpenseItemRepository.class){
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
     * getExpenseItems runs query in a separate thread, and on success loads data from cursor into a list
     * @param callback
     */
    @Override
    public void getExpenseItems(@NonNull final LoadExpenseItemsCallback callback) {
        Log.d("REPOSITORY","Loading...");

        INSTANCE.collection(collectionPathApartment)
                .document(apartmentPath)
                .collection(collectionPathExpense)
                .orderBy("datePayed")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        final List<ExpenseItem> expenseItems = new ArrayList<ExpenseItem>(0);
                        for (QueryDocumentSnapshot doc : value) {
                            ExpenseItem loadedexpense = doc.toObject(ExpenseItem.class);
                            expenseItems.add(loadedexpense);
                        }
                        callback.onExpenseItemsLoaded(expenseItems);
                    }
                });
    }


    @Override
    public void getExpenseItemsDate(@NonNull final Long paymentDate, @NonNull final LoadExpenseItemsCallback callback) {
        Log.d("REPOSITORY","GetExpenseItem: " + paymentDate);

        final List<ExpenseItem> expenseItems = new ArrayList<ExpenseItem>(0);

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathExpense)
                .whereGreaterThan("paymentDate", paymentDate - 1)
                .whereLessThan("paymentDate", paymentDate + 86400000)
                .orderBy("paymentDate")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("REPO", task.getResult().toString());
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Getting all items", "loaded from firebase by date: " + document.getId() + " => " + document.getData());
                                ExpenseItem loadedExpense = document.toObject(ExpenseItem.class);
                                expenseItems.add(loadedExpense);
                                Log.d("Getting all items", "loaded expense: " + loadedExpense.toString());
                            }
                        } else {
                            Log.d("Getting all items", "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        callback.onExpenseItemsLoaded(expenseItems);
                    }
        });
    }

    /**
     * saveExpenseItem runs contentProvider update in separate thread
     * @param expenseItem
     */
    @Override
    public void saveExpenseItem(@NonNull final ExpenseItem expenseItem) {
        Log.d("REPOSITORY","SaveExpenseItem");

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathExpense).document(expenseItem.getId()).set(expenseItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Saving expense", "expense successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Saving expense", "Error writing document", e);
                    }
                });
    }

    /**
     * createExpenseItem runs contentProvider insert in separate thread
     * @param expenseItem
     */
    @Override
    public void createExpenseItem(@NonNull final ExpenseItem expenseItem) {
        Log.d("REPOSITORY","CreateExpenseItem");

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathExpense).document();
        String id = INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathExpense).document().getId();

        expenseItem.setId(id);

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathExpense).document(id).set(expenseItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Creating expense", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Creating expense", "Error writing document", e);
                    }
                });
    }

    /**
     * Method to facilitate delete item
     * @param id
     */
    @Override
    public void deleteExpenseItem(@NonNull final String id) {

        INSTANCE.collection(collectionPathApartment).document(apartmentPath).collection(collectionPathExpense).document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleting expense", id + " successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("deleting expense", "Error writing document", e);
                    }
                });
    }
}
