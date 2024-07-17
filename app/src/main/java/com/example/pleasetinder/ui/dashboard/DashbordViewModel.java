package com.example.pleasetinder.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashbordViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DashbordViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}