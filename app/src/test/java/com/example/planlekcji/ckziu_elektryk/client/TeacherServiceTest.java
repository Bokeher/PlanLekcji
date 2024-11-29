package com.example.planlekcji.ckziu_elektryk.client;

import static com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants.TOKEN;
import static com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants.URL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStub;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntry;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TeacherServiceTest {


    private TimetableService service;

    @Before
    public void init() {
        Config config = mock(Config.class);

        when(config.getAPIUrl()).thenReturn(URL);
        when(config.getToken()).thenReturn(TOKEN);

        CKZiUElektrykClient client = new CKZiUElektrykClientStub(config);

        service = client.getTimetableService(SchoolEntryType.TEACHERS);
    }

    @Test
    public void shouldGetAllTeachers() {
        List<SchoolEntry> list = service.getList();

        assertEquals(90, list.size());
    }

}
