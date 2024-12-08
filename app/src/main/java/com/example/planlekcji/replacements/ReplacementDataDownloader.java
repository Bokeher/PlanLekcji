package com.example.planlekcji.replacements;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.replacments.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacments.ReplacementService;
import com.example.planlekcji.listener.DownloadCompleteListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Optional;

public class ReplacementDataDownloader implements Runnable {
    private final DownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public ReplacementDataDownloader(CKZiUElektrykClient client, DownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        ReplacementService replacementService = client.getReplacementService();

        Optional<Replacement> replacementOptional = replacementService.getLatestReplacement();

        if (!replacementOptional.isPresent()) return;

        Replacement replacement = replacementOptional.get();

        Document doc = Jsoup.parse(replacement.content());

        listener.onDownloadComplete(doc);
    }
}
