package com.example.room8.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.room8.MainActivity;
import com.example.room8.R;
import com.example.room8.ui.dashboard.chat.ChatMessage;
import com.example.room8.ui.dashboard.chat.ChatMessageAdapter;
import com.example.room8.ui.dashboard.chat.User;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItem;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.core.util.Preconditions.checkNotNull;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private DashboardViewModel dashboardViewModel;
    private RecyclerView recyclerView;

    private FirebaseFirestore myDb;
    private static FirebaseFirestore INSTANCE;
    private static String collectionPathApartment = "apartments";
    private static String collectionPathMessages = "messages";
    private static String apartmentPath;

    private EditText mMessage;

    private Task<QuerySnapshot> mChatMessageEventListener;
    private ArrayList<ChatMessage> mMessages = new ArrayList<>();
    private Set<String> mMessageIds = new HashSet<>();

    private ChatMessageAdapter mChatMessageAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_messaging, container, false);

        mMessage = root.findViewById(R.id.input);
        recyclerView = root.findViewById(R.id.list_of_messages);

        root.findViewById(R.id.fab).setOnClickListener(this);

        myDb = FirebaseFirestore.getInstance();

        apartmentPath = ToDoItemRepository.getCollectionPathApartment();

        initChatRoomView();
        loadChatMessages();

        return root;
    }



    private void initChatRoomView(){
        mChatMessageAdapter = new ChatMessageAdapter(mMessages, new ArrayList<User>(), getContext());
        recyclerView.setAdapter(mChatMessageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){

            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom){
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mMessages.size() > 0){
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() -1);
                            }
                        }
                    }, 100);
                }
            }
        });

    }




    public void loadChatMessages(){

        myDb.collection(collectionPathApartment)
                .document(apartmentPath)
                .collection(collectionPathMessages)
                .orderBy("time", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Log.d("Getting all the messages", "loading from firebase: " + document.getId() + " => " + document.getData());
                                ChatMessage message = document.toObject(ChatMessage.class);
                                mMessages.add(message);
                                recyclerView.smoothScrollToPosition(mMessages.size()-1);
                                Log.d("Getting all items", "loaded messages: " + mMessages.size());

                            }
                        }
                        mChatMessageAdapter.notifyDataSetChanged();
                    }
                });



    }

    @Override
    public void onClick(View view) {
        String message = mMessage.getText().toString();

        if(!message.equals("")){
            message = message.replaceAll(System.getProperty("line.separator"), "");

            DocumentReference newMessageDoc = myDb
                    .collection(collectionPathApartment)
                    .document(apartmentPath)
                    .collection(collectionPathMessages)
                    .document();

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage(message);
            chatMessage.setTime(new Timestamp(new Date(System.currentTimeMillis())));

            String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            chatMessage.setName(user);

            newMessageDoc.set(chatMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mMessage.setText("");
                    }else{
                        Log.d("DASHBOARD", "there was an error");
                    }
                }
            });

        }

    }

}

