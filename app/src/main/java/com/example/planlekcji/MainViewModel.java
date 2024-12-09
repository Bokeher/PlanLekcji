package com.example.planlekcji;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.listener.DownloadCompleteListener;
import com.example.planlekcji.listener.DownloadCompleteListenerString;
import com.example.planlekcji.replacements.ReplacementDataProcessor;
import com.example.planlekcji.replacements.ReplacementDataDownloader;
import com.example.planlekcji.utils.RetryHandler;
import com.example.planlekcji.timetable.model.LessonRow;
import com.example.planlekcji.timetable.TimetableDataProcessor;
import com.example.planlekcji.timetable.TimetableDataDownloader;

import org.jsoup.nodes.Document;

import java.util.List;

public class MainViewModel extends ViewModel {
    private final CKZiUElektrykClient client;

    // downloaded data
    private final MutableLiveData<List<String>> replacements = new MutableLiveData<>();
    private final MutableLiveData<List<LessonRow>> lessonRows = new MutableLiveData<>();

    // retry handlers
    private final RetryHandler replaceRetryHandler = new RetryHandler(this::startReplacementDownload);
    private final RetryHandler timetableRetryHandler = new RetryHandler(this::startReplacementDownload);

    public MainViewModel() {
        // Initialize the client
        client = new CKZiUElektrykClient();
    }

    public void fetchData() {
        startReplacementDownload();
        startTimetableDownload();
    }

    public void fetchReplacements() {
        startReplacementDownload();
    }

    public void fetchTimetable() {
        startTimetableDownload();
    }

    private void startReplacementDownload() {
        ReplacementDataDownloader downloader = new ReplacementDataDownloader(client, new DownloadCompleteListenerString() {
            @Override
            public void onDownloadComplete(String rawReplacements) {
                // Process replacement data
                ReplacementDataProcessor replacementDataProcessor = new ReplacementDataProcessor(rawReplacements);
                replacementDataProcessor.process();

                // Update LiveData
                replacements.postValue(replacementDataProcessor.getReplacements());
            }

            @Override
            public void onDownloadFailed() {
                replaceRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    private void startTimetableDownload() {
        TimetableDataDownloader downloader = new TimetableDataDownloader(new DownloadCompleteListener() {
            @Override
            public void onDownloadComplete(Document document) {
                // Process timetable data
                TimetableDataProcessor processTimetableData = new TimetableDataProcessor(document);
                List<LessonRow> lessonRows = processTimetableData.getLessonRows();

                // Update LiveData
                MainViewModel.this.lessonRows.postValue(lessonRows);
            }

            @Override
            public void onDownloadFailed() {
                timetableRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    public LiveData<List<LessonRow>> getTimetableLiveData() {
        return lessonRows;
    }

    public LiveData<List<String>> getReplacementsLiveData() {
        return replacements;
    }
}
