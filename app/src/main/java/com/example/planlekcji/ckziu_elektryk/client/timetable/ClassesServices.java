package com.example.planlekcji.ckziu_elektryk.client.timetable;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

public class ClassesServices extends AbstractTimetableService {

    public ClassesServices(Config config) {
        super(config, Endpoint.TIMETABLES_CLASSES, Endpoint.TIMETABLES_CLASS);
    }
}
