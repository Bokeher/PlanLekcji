package com.example.planlekcji.MainApp.Replacements;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ReplacementDataDownloader implements Runnable {
    private final OnDownloadCompleteListener listener;

    public ReplacementDataDownloader(OnDownloadCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("http://zastepstwa.ckziu-elektryk.pl/").get();
            listener.onDownloadComplete(doc);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onDownloadFailed();
        }
    }

    public interface OnDownloadCompleteListener {
        void onDownloadComplete(Document document);
        void onDownloadFailed();
    }
}
