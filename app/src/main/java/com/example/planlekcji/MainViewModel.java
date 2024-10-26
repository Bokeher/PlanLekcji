package com.example.planlekcji;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planlekcji.listener.DownloadCompleteListener;
import com.example.planlekcji.replacements.ReplacementDataProcessor;
import com.example.planlekcji.replacements.ReplacementDataDownloader;
import com.example.planlekcji.utils.RetryHandler;
import com.example.planlekcji.timetable.model.LessonRow;
import com.example.planlekcji.timetable.TimetableDataProcessor;
import com.example.planlekcji.timetable.TimetableDataDownloader;

import org.jsoup.nodes.Document;

import java.util.List;

public class MainViewModel extends ViewModel {
    // downloaded data
    private final MutableLiveData<List<String>> replacements = new MutableLiveData<>();
    private final MutableLiveData<List<LessonRow>> lessonRows = new MutableLiveData<>();

    // selected tab
    private final MutableLiveData<Integer> selectedTabNumber = new MutableLiveData<>();

    // retry handlers
    private final RetryHandler replaceRetryHandler = new RetryHandler(this::startReplacementDownload);
    private final RetryHandler timetableRetryHandler = new RetryHandler(this::startReplacementDownload);

    public MainViewModel() {
        selectedTabNumber.setValue(0); // set default
    }

    public void fetchData() {
        startReplacementDownload();
        startTimetableDownload();
    }

    private void startReplacementDownload() {
        ReplacementDataDownloader downloader = new ReplacementDataDownloader(new DownloadCompleteListener() {
            @Override
            public void onDownloadComplete(Document document) {
                // Process replacement data
                ReplacementDataProcessor replacementDataProcessor = new ReplacementDataProcessor(document);
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

    public void setSelectedTabNumber(int selectedTabNumber) {
        this.selectedTabNumber.setValue(selectedTabNumber);
    }

    public int getSelectedTabNumber() {
        return selectedTabNumber.getValue();
    }


    public LiveData<List<LessonRow>> getTimetableLiveData() {
        return lessonRows;
    }

    public LiveData<List<LessonRow>> getReplacementsLiveData() {
        return lessonRows;
    }
}
