package com.example.planlekcji.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class DoubleLiveData {
    private final MediatorLiveData<Boolean> mediatorLiveData = new MediatorLiveData<>();
    private boolean data1Received = false;
    private boolean data2Received = false;

    public void setData1Received(boolean received) {
        data1Received = received;
        checkAllDataReceived();
    }

    public void setData2Received(boolean received) {
        data2Received = received;
        checkAllDataReceived();
    }

    private void checkAllDataReceived() {
        if (data1Received && data2Received) {
            mediatorLiveData.setValue(true);
        }
    }

    public LiveData<Boolean> asLiveData() {
        return mediatorLiveData;
    }
}