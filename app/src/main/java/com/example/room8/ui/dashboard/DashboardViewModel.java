package com.example.room8.ui.dashboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.example.room8.ui.dashboard.chat.ChatMessage;
import com.example.room8.ui.dashboard.chat.User;
import com.example.room8.ui.home.HomeContract;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {


    private FirebaseFirestore myDb;
    private static FirebaseFirestore INSTANCE;
    private static String collectionPathApartment = "apartments";
    private static String collectionPathMessages = "messages";
    private static String apartmentPath;

    private EditText mMessage;

    private ListenerRegistration mChatMessageEventListener;
    private ArrayList<ChatMessage> mMessages = new ArrayList<>();

    public DashboardViewModel() {
        myDb = FirebaseFirestore.getInstance();


    }

    //load all the available chats





}