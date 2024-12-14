package com.example.planlekcji.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;

public class RetryHandler {
    private static final long RETRY_DELAY_MS = 10000;
    private final Runnable retryRunnable;
    private int retriesNumber = 0;

    public RetryHandler(Runnable retryRunnable) {
        this.retryRunnable = retryRunnable;
    }

    public void handleRetry() {
        scheduleRetry();

        Context context = MainActivity.getContext();
        String errorMessage = context.getString(R.string.toastErrorMessage_noInternetConnectionRetry);

        if(retriesNumber > 1) ToastUtils.showToast(context, errorMessage, false);
        retriesNumber++;
    }

    private void scheduleRetry() {
        new Handler(Looper.getMainLooper()).postDelayed(retryRunnable, RETRY_DELAY_MS);
    }
}
