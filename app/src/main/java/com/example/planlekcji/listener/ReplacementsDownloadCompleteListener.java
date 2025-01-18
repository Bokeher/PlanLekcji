package com.example.planlekcji.listener;

public interface ReplacementsDownloadCompleteListener {

    void onDownloadComplete(String rawReplacements);

    void onDownloadFailed();
}
