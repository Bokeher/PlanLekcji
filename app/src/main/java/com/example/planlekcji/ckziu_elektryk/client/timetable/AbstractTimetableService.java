package com.example.planlekcji.ckziu_elektryk.client.timetable;

import android.util.Log;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.utils.ParamValidator;
import com.example.planlekcji.timetable.model.DayOfWeek;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

        return apiResponseCall.error(printError())
                .success(successResponse -> {
                    List<SchoolEntry> schoolEntries = new ArrayList<>();

                    JsonArray jsonArray = successResponse.getJsonElement().getAsJsonArray();

                    for (JsonElement element : jsonArray) {
                        JsonObject jsonObject = element.getAsJsonObject();

                        Log.e("JSON Response1", jsonObject.toString());
                        schoolEntries.add(new SchoolEntry(
                                jsonObject.get("shortcut").getAsString(),
                                jsonObject.get("url").getAsString()
                        ));
                    }

                    return schoolEntries;
                });
    }

    @Override
    public Map<DayOfWeek, List<String>> getTimetable(String name) {
        ParamValidator.checkNotNullAndNotEmpty(name);

        APIResponseCall apiResponseCall = getData(oneSchoolEntryEndpoint
                .withPlaceholders(Map.of("{school_entry_shortcut}", name)));

        if (!apiResponseCall.hasResponse()) return Collections.emptyMap();

        return apiResponseCall.error(printError())
                .success(successResponse -> {
                    Map<DayOfWeek, List<String>> timetable = new HashMap<>();

                    JsonObject jsonObject = successResponse.getJsonElement().getAsJsonObject();
                    Log.e("JSON Response2", jsonObject.toString());

                    String[] daysOfWeekShortcuts = {"PON", "WTO", "ŚRO", "CZW", "PTK"};

                    for (String shortcut : daysOfWeekShortcuts) {
                        if (!jsonObject.has(shortcut)) continue;

                        List<String> lessons = jsonObject.get(shortcut).getAsJsonArray().asList()
                                .stream()
                                .map(JsonElement::getAsString)
                                .collect(Collectors.toList());

                        timetable.put(matchDayOfWeekName(shortcut), lessons);
                    }

                    return timetable;
                });
    }

    /**
     * Converts API day of week to this app day of week convention
     *
     * @param dayOfWeekAsString day of week shortcut in Polish
     * @return the app day of week
     */
    private DayOfWeek matchDayOfWeekName(@NotNull String dayOfWeekAsString) {
        return switch (dayOfWeekAsString) {
            case "PON" -> DayOfWeek.MONDAY;
            case "WTO" -> DayOfWeek.TUESDAY;
            case "ŚRO" -> DayOfWeek.WEDNESDAY;
            case "CZW" -> DayOfWeek.THURSDAY;
            case "PTK" -> DayOfWeek.FRIDAY;
            default -> throw new IllegalStateException("Unexpected value: " + dayOfWeekAsString);
        };
    }
}
