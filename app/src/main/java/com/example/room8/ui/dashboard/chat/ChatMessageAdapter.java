package com.example.room8.ui.dashboard.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.room8.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private ArrayList<ChatMessage> mMessages = new ArrayList<>();
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;
    public static final int msg_left = 0;
    public static final int msg_right = 1;
    private String displayName;

    public ChatMessageAdapter(ArrayList<ChatMessage> messages, ArrayList<User> users, Context context){

        this.mMessages = messages;
        this.mContext = context;
        this.mUsers = users;
    }

    @Override
    public int getItemViewType(int position){
        displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(displayName.equals(mMessages.get(position).getName())){
            return msg_right;
        }else{
            return msg_left;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(viewType == msg_right){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageAdapter.ViewHolder holder, int position) {

        ChatMessage chatMessage = mMessages.get(position);
        holder.message.setText(chatMessage.getMessage());
        holder.username.setText(chatMessage.getName());
        Date time = chatMessage.getTime().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");

        holder.time.setText(sdf.format(time));


    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, username, time;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_text);
            username = itemView.findViewById(R.id.message_user);
            time = itemView.findViewById(R.id.text_message_time);
        }
    }
}
