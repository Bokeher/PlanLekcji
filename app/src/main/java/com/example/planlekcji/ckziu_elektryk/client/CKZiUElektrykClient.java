package com.example.planlekcji.ckziu_elektryk.client;

import com.example.planlekcji.ckziu_elektryk.client.replacments.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.ckziu_elektryk.client.replacments.ReplacementServiceFactory;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableServiceFactory;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfo;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfoService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfoServiceFactory;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CKZiUElektrykClient {

    protected Config config;
    private final ReplacementService replacementService;
    private final TimetableInfoService timetableInfoService;

    protected CKZiUElektrykClient(@NotNull Config config) {
        this.config = config;
        this.timetableInfoService = TimetableInfoServiceFactory.create(config);
        this.replacementService = ReplacementServiceFactory.create(config);
    }

    public CKZiUElektrykClient() {
        this(Config.getOrCreateConfig());
    }

    public ReplacementService getReplacementService() {
        return replacementService;
    }

    public TimetableService getTimetableService(SchoolEntryType type) {
        return TimetableServiceFactory.create(type, config);
    }

    public Optional<TimetableInfo> getTimetableInfo() {
        return timetableInfoService.getTimetableInfo();
    }
}
