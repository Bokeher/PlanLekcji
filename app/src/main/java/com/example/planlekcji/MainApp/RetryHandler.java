package com.example.planlekcji.MainApp;

import android.os.Handler;
import android.os.Looper;

import com.example.planlekcji.ToastUtils;
import com.example.planlekcji.MainActivity;

public class RetryHandler {
    private static final long RETRY_DELAY_MS = 10000;
    private final Runnable retryRunnable;
    private int retriesNumber = 0;

    public RetryHandler(Runnable retryRunnable) {
        this.retryRunnable = retryRunnable;
    }

    public void handleRetry() {
        scheduleRetry();
        if(retriesNumber > 1) ToastUtils.showToast(MainActivity.getContext(), "Brak połączenia z internetem. Ponawianie połączenia.", false);
        retriesNumber ++;
    }

    private void scheduleRetry() {
        new Handler(Looper.getMainLooper()).postDelayed(retryRunnable, RETRY_DELAY_MS);
    }
}
