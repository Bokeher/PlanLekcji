package com.example.planlekcji.ckziu_elektryk.client.timetable.info;

import com.example.planlekcji.ckziu_elektryk.client.Config;

public final class TimetableInfoServiceFactory {

    public static TimetableInfoService create(Config config) {
        return new TimetableInfoServiceImpl(config);
    }
}
