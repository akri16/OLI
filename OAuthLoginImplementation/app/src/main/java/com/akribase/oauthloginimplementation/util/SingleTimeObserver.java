package com.akribase.oauthloginimplementation.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class SingleTimeObserver<T> implements Observer<T> {
    private final MutableLiveData<T> mutableLiveData;
    private final LiveData<T> liveData;

    public SingleTimeObserver(MutableLiveData<T> mutableLiveData, LiveData<T> liveData) {
        this.mutableLiveData = mutableLiveData;
        this.liveData = liveData;
    }

    @Override
    public void onChanged(T t) {
        mutableLiveData.setValue(t);
        liveData.removeObserver(this);
    }
}
