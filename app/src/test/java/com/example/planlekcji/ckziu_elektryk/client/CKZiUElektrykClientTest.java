package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertNotNull;

import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStub;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;

import org.junit.Test;

public class CKZiUElektrykClientTest {

    @Test
    public void shouldCreateCKZiUElektrykClient() {
        CKZiUElektrykClient client = new CKZiUElektrykClientStub(Config.getOrCreateConfig());

        assertNotNull(client);
    }

    @Test
    public void shouldGetReplacementService() {
        CKZiUElektrykClient client = new CKZiUElektrykClientStub(Config.getOrCreateConfig());

        assertNotNull(client.getReplacementService());
    }

    @Test
    public void shouldGetTimetableServiceForClasses() {
        CKZiUElektrykClient client = new CKZiUElektrykClientStub(Config.getOrCreateConfig());

        assertNotNull(client.getTimetableService(SchoolEntryType.CLASSES));
    }

    @Test
    public void shouldGetTimetableServiceForTeachers() {
        CKZiUElektrykClient client = new CKZiUElektrykClient(Config.getOrCreateConfig());

        assertNotNull(client.getTimetableService(SchoolEntryType.TEACHERS));
    }
    @Test
    public void shouldGetTimetableServiceForClassrooms() {
        CKZiUElektrykClient client = new CKZiUElektrykClient(Config.getOrCreateConfig());

        assertNotNull(client.getTimetableService(SchoolEntryType.CLASSROOMS));
    }
}
