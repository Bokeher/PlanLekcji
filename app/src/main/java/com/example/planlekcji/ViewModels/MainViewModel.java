package com.example.planlekcji.ViewModels;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planlekcji.MainApp.Replacements.ProcessReplacementData;
import com.example.planlekcji.MainApp.Replacements.ReplacementDataDownloader;
import com.example.planlekcji.MainApp.Replacements.ReplacementToTimetable;
import com.example.planlekcji.MainApp.RetryHandler;
import com.example.planlekcji.MainApp.Timetable.LessonRow;
import com.example.planlekcji.MainApp.Timetable.ProcessTimetableData;
import com.example.planlekcji.MainApp.Timetable.TimetableDataDownloader;

import org.jsoup.nodes.Document;

import java.util.List;
public class MainViewModel extends ViewModel {
    // downloaded data
    private MutableLiveData<List<String>> replacements = new MutableLiveData<>();
    private MutableLiveData<List<ReplacementToTimetable>> replacementsForTimetable = new MutableLiveData<>();
    private MutableLiveData<List<LessonRow>> lessonRows = new MutableLiveData<>();
    private DoubleLiveData doubleLiveData = new DoubleLiveData();

    // selected tab
    private MutableLiveData<Integer> selectedTabNumber = new MutableLiveData<>();

    // retry handlers
    RetryHandler replaceRetryHandler = replaceRetryHandler = new RetryHandler(() -> startReplacementDownload());
    RetryHandler timetableRetryHandler = timetableRetryHandler = new RetryHandler(() -> startReplacementDownload());

    public MainViewModel() {
        selectedTabNumber.setValue(0); // set default
    }

    public void fetchData() {
        startReplacementDownload();
        startTimetableDownload();
    }

    private void startReplacementDownload() {
        ReplacementDataDownloader downloader = new ReplacementDataDownloader(new ReplacementDataDownloader.OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(Document document) {
                // Process replacement data
                ProcessReplacementData processReplacementData = new ProcessReplacementData(document);
                processReplacementData.process();

                // Update LiveData
                replacements.postValue(processReplacementData.getReplacements());
                replacementsForTimetable.postValue(processReplacementData.getReplacementsForTimetable());

                new Handler(Looper.getMainLooper()).post(() -> doubleLiveData.setData1Received(true));
            }

            @Override
            public void onDownloadFailed() {
                replaceRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    private void startTimetableDownload() {
        TimetableDataDownloader downloader = new TimetableDataDownloader(new TimetableDataDownloader.OnDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(Document document) {
                // Process timetable data
                ProcessTimetableData processTimetableData = new ProcessTimetableData(document);
                List<LessonRow> lessonRows = processTimetableData.getLessonRows();

                // Update LiveData
                MainViewModel.this.lessonRows.postValue(lessonRows);

                new Handler(Looper.getMainLooper()).post(() -> doubleLiveData.setData2Received(true));
            }

            @Override
            public void onDownloadFailed() {
                timetableRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    public LiveData<Boolean> getCombinedLiveData() {
        return doubleLiveData.asLiveData();
    }

    public List<String> getReplacementsValue() {
        return replacements.getValue();
    }

    public List<ReplacementToTimetable> getReplacementsForTimetableValue() {
        return replacementsForTimetable.getValue();
    }

    public List<LessonRow> getLessonRows() {
        return lessonRows.getValue();
    }

    public void setSelectedTabNumber(int selectedTabNumber) {
        this.selectedTabNumber.setValue(selectedTabNumber);
    }

    public int getSelectedTabNumber() {
        return selectedTabNumber.getValue();
    }
}
