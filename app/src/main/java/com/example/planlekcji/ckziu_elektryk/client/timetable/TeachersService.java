package com.example.planlekcji.ckziu_elektryk.client.timetable;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

public class TeachersService extends AbstractTimetableService {

    public TeachersService(Config config) {
        super(config, Endpoint.TIMETABLES_TEACHERS, Endpoint.TIMETABLES_TEACHER);
    }
}
