package com.example.room8.ui.todolist;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
        Log.d("VIEW MODEL", "FRAG VIEW MODEL");
    }

    public LiveData<String> getText() {
        return mText;
    }
}