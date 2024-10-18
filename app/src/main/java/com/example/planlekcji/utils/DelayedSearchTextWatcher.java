package com.example.planlekcji.utils;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.planlekcji.listener.SearchListener;

public class DelayedSearchTextWatcher implements TextWatcher {
    private static final long DELAY_MS = 500;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private final SearchListener listener;

    public DelayedSearchTextWatcher(SearchListener listener) {
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(final Editable editable) {
        handler.removeCallbacks(runnable);
        runnable = () -> {
            if (listener != null) {
                listener.onSearch(editable.toString());
            }
        };
        handler.postDelayed(runnable, DELAY_MS);
    }
}