package com.example.planlekcji.ckziu_elektryk.client.timetable;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

public class ClassroomsService extends AbstractTimetableService {

    public ClassroomsService(Config config) {
        super(config, Endpoint.TIMETABLES_CLASSROOMS, Endpoint.TIMETABLES_CLASSROOM);
    }
}
