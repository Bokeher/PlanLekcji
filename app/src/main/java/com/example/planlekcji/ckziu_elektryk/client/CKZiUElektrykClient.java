package com.example.planlekcji.ckziu_elektryk.client;

public class CKZiUElektrykClient {

    private final String token;

    private ReplacementService replacementService;
    private TimetableService timetableService;

    public CKZiUElektrykClient(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token can not be null or empty");
        }

        this.token = token;
       // this.replacementService = new ReplacementServiceImpl(token);
    }

    public ReplacementService getReplacementService() {
        return replacementService;
    }

    //    public TimetableService getTimetableService(SchoolEntryType type) {
//        return TimetableServiceFactory.create(type, token);
//    }

    /*
    get replacements
    get timetable
    get timetable info
     */
}
