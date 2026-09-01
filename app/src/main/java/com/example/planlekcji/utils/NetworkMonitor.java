package com.example.planlekcji.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

public class NetworkMonitor {
    private final ConnectivityManager connectivityManager;
    private final MutableLiveData<Boolean> isOnlineLiveData = new MutableLiveData<>(true);
    private ConnectivityManager.NetworkCallback networkCallback;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public NetworkMonitor(Context context) {
        connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean current = isCurrentlyOnline();
        isOnlineLiveData.setValue(current);
        registerCallback();
    }

    public LiveData<Boolean> getIsOnlineLiveData() {
        return isOnlineLiveData;
    }

    public boolean isCurrentlyOnline() {
        if (connectivityManager == null) return false;
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private void registerCallback() {
        if (connectivityManager == null) return;
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                mainHandler.post(() -> {
                    if (!Objects.equals(isOnlineLiveData.getValue(), true)) {
                        isOnlineLiveData.setValue(true);
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                mainHandler.post(() -> {
                    if (!Objects.equals(isOnlineLiveData.getValue(), false)) {
                        isOnlineLiveData.setValue(false);
                    }
                });
            }
        };

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback);
        } catch (Exception e) {
            Log.e("REGISTER_NETWORK_CALLBACK_ERROR", e.getMessage(), e);
        }
    }

    public void unregisterCallback() {
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) {
            }
        }
    }
}
