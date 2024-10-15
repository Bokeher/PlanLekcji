package com.example.planlekcji.listener;

import org.jsoup.nodes.Document;

public interface DownloadCompleteListener {

    void onDownloadComplete(Document document);

    void onDownloadFailed();
}
