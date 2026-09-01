package com.example.planlekcji.ckziu_elektryk.client.timetable;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonFactory;
import com.example.planlekcji.ckziu_elektryk.client.utils.ParamValidator;
import com.example.planlekcji.utils.DayOfWeek;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractTimetableService extends ClientService implements TimetableService {

    private final Endpoint schoolEntriesEndpoint;
    private final Endpoint oneSchoolEntryEndpoint;

    public AbstractTimetableService(Config config, Endpoint schoolEntriesEndpoint, Endpoint oneSchoolEntryEndpoint) {
        super(config);
        this.schoolEntriesEndpoint = schoolEntriesEndpoint;
        this.oneSchoolEntryEndpoint = oneSchoolEntryEndpoint;
    }

    @Override
    public List<SchoolEntry> getList() {
        APIResponseCall apiResponseCall = getData(schoolEntriesEndpoint);

        if (!apiResponseCall.hasResponse()) return Collections.emptyList();

        return apiResponseCall.error(handleError())
                .success(successResponse -> {
                    List<SchoolEntry> schoolEntries = new ArrayList<>();

                    JsonArray jsonArray = successResponse.getJsonElement().getAsJsonArray();

                    for (JsonElement element : jsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();

                        SchoolEntry entry = new SchoolEntry(
                                jsonObject.get("shortcut").getAsString()
                        );

                        if (jsonObject.has("name"))
                            entry.setName(jsonObject.get("name").getAsString());

                        schoolEntries.add(entry);
                    }

                    return schoolEntries;
                });
    }

    @Override
    public JsonObject getTimetableJsonObject(String name) {
        ParamValidator.checkNotNullAndNotEmpty(name);

        APIResponseCall apiResponseCall = getData(oneSchoolEntryEndpoint
                .withPlaceholders(Map.of("{school_entry_shortcut}", name)));

        if (!apiResponseCall.hasResponse()) return null;

        return apiResponseCall.error(handleError())
                .success(successResponse -> successResponse.getJsonElement().getAsJsonObject());
    }

    @Override
    public Map<DayOfWeek, List<Lesson>> getTimetable(String name) {
        JsonObject jsonObject = getTimetableJsonObject(name);
        if (jsonObject == null) return Collections.emptyMap();
        return parseTimetable(jsonObject);
    }

    public static Map<DayOfWeek, List<Lesson>> parseTimetable(JsonObject jsonObject) {
        Map<DayOfWeek, List<Lesson>> timetable = new LinkedHashMap<>();
        if (jsonObject == null) return timetable;

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            String shortcut = dayOfWeek.name();

            if (!jsonObject.has(shortcut)) continue;

            JsonArray jsonArray = jsonObject.get(shortcut).getAsJsonArray();

            if (jsonArray.isEmpty()) {
                timetable.put(dayOfWeek, Collections.emptyList());
            } else {
                List<Lesson> lessons = jsonArray.asList()
                        .stream()
                        .map(jsonElement -> LessonFactory.createLesson(jsonElement.getAsJsonObject()))
                        .collect(Collectors.toList());

                timetable.put(dayOfWeek, lessons);
            }
        }

        return timetable;
    }
}
