package com.example.planlekcji.listener;

public interface DownloadCompleteListener<T> {

    default void onCacheLoaded(T data) {}
    void onDownloadComplete(T data);
    void onDownloadFailed();
}
