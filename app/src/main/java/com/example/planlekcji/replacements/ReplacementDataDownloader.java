package com.example.planlekcji.replacements;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.replacments.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacments.ReplacementService;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;

import java.util.Optional;

public class ReplacementDataDownloader implements Runnable {
    private final ReplacementsDownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public ReplacementDataDownloader(CKZiUElektrykClient client, ReplacementsDownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        ReplacementService replacementService = client.getReplacementService();

        Optional<Replacement> replacementOptional = replacementService.getLatestReplacement();

        if (!replacementOptional.isPresent()) {
            listener.onDownloadComplete("");
            return;
        }

        Replacement replacement = replacementOptional.get();

        listener.onDownloadComplete(replacement.content());
    }
}
