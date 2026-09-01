package com.example.planlekcji.timetable;

import android.content.Context;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.timetable.AbstractTimetableService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.database.DatabaseCacheManager;
import com.example.planlekcji.listener.TimetableDownloadCompleteListener;
import com.example.planlekcji.preview.PreviewDataStore;
import com.example.planlekcji.utils.DayOfWeek;
import com.example.planlekcji.utils.RefreshCooldownManager;
import com.example.planlekcji.utils.RefreshDataType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

public class TimetableDataDownloader implements Runnable {
    private final TimetableDownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public TimetableDataDownloader(CKZiUElektrykClient client, TimetableDownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        SchoolEntryType schoolEntryType = MainActivity.getTimetableType();
        String token = MainActivity.getToken(schoolEntryType).replace(" ", "");

        if (Config.getOrCreateConfig().isPreviewMode()) {
            listener.onDownloadComplete(PreviewDataStore.getTimetable(schoolEntryType, token));
            return;
        }

        // Cache first
        Context context = MainActivity.getContext();
        String cacheKey = "timetable_" + schoolEntryType.name() + "_" + token;
        Map<DayOfWeek, List<Lesson>> cachedMap = null;
        String cachedJson = null;
        if (context != null && !token.isEmpty()) {
            try {
                cachedJson = DatabaseCacheManager.getInstance(context).getRawJson(cacheKey);
                if (cachedJson != null) {
                    JsonObject jsonObject = JsonParser.parseString(cachedJson).getAsJsonObject();
                    cachedMap = AbstractTimetableService.parseTimetable(jsonObject);
                    if (!cachedMap.isEmpty()) {
                        listener.onCacheLoaded(cachedMap);
                    }
                }
            } catch (Exception e) {
                Log.e("TimetableDownloader", "Failed to load cached timetable", e);
            }
        }

        // Complete immediately if cache is fresh within TTL
        RefreshCooldownManager cooldown = (context != null) ? RefreshCooldownManager.getInstance(context) : null;
        if (cooldown != null && cooldown.isFresh(RefreshDataType.TIMETABLE, cachedMap)) {
            listener.onDownloadComplete(cachedMap);
            return;
        }

        if (token.isEmpty()) {
            Log.e("TimetableDownloader", "Token is empty");
            listener.onDownloadFailed();
            return;
        }

        try {
            TimetableService timetableService = client.getTimetableService(schoolEntryType);
            JsonObject jsonObject = timetableService.getTimetableJsonObject(token);

            if (jsonObject != null && !jsonObject.isEmpty()) {
                String newJson = jsonObject.toString();
                if (newJson.equals(cachedJson) && cachedMap != null && !cachedMap.isEmpty()) {
                    if (cooldown != null) {
                        cooldown.recordRefresh(RefreshDataType.TIMETABLE);
                    }
                    listener.onDownloadComplete(cachedMap);
                    return;
                }

                if (context != null) {
                    DatabaseCacheManager.getInstance(context).saveRawJson(cacheKey, newJson);
                    if (cooldown != null) {
                        cooldown.recordRefresh(RefreshDataType.TIMETABLE);
                    }
                }
                Map<DayOfWeek, List<Lesson>> map = AbstractTimetableService.parseTimetable(jsonObject);
                if (!map.isEmpty()) {
                    listener.onDownloadComplete(map);
                } else {
                    listener.onDownloadFailed();
                }
            } else {
                listener.onDownloadFailed();
            }
        } catch (Exception e) {
            listener.onDownloadFailed();
        }
    }
}
