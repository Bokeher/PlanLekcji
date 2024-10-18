package com.example.planlekcji.replacements;

import com.example.planlekcji.listener.DownloadCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ReplacementDataDownloader implements Runnable {
    private final DownloadCompleteListener listener;

    public ReplacementDataDownloader(DownloadCompleteListener listener) {
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
}
