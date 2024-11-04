package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class CKZiUElektrykClientTest {

    @Test
    public void shouldCreateCKZiUElektrykClient() {
        CKZiUElektrykClient client = new CKZiUElektrykClient("token");

        assertNotNull(client);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateCKZiUElektrykClientAndThrowExceptionWhenTokenIsNullOrEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new CKZiUElektrykClient(""));

        String expectedMessage = "Token can not be null or empty";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldGetReplacementService() {
        CKZiUElektrykClient client = new CKZiUElektrykClient("token");

        assertNotNull(client.getReplacementService());
    }

//    @Test
//    void shouldGetTimetableServiceForClasses() {
//        CKZiUElektrykClient client = new CKZiUElektrykClient("token");
//
//        assertNotNull(client.getTimetableService(SchoolEntryType.CLASSES));
//    }
//
//    @Test
//    void shouldGetTimetableServiceForTeachers() {
//        CKZiUElektrykClient client = new CKZiUElektrykClient("token");
//
//        assertNotNull(client.getTimetableService(SchoolEntryType.TEACHERS));
//    }
//    @Test
//    void shouldGetTimetableServiceForClassrooms() {
//        CKZiUElektrykClient client = new CKZiUElektrykClient("token");
//
//        assertNotNull(client.getTimetableService(SchoolEntryType.CLASSROOMS));
//    }


}
