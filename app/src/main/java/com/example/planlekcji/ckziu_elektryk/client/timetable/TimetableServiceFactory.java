package com.example.planlekcji.ckziu_elektryk.client.timetable;

import com.example.planlekcji.ckziu_elektryk.client.Config;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class TimetableServiceFactory {

    private static final Map<SchoolEntryType, Class<? extends TimetableService>> services =
            Map.of(SchoolEntryType.TEACHERS, TeachersService.class
                    , SchoolEntryType.CLASSES, ClassesServices.class
                    , SchoolEntryType.CLASSROOMS, ClassroomsService.class);

    public static TimetableService create(@NotNull SchoolEntryType type, @NotNull Config config) {
        Class<? extends TimetableService> clazz = services.get(type);

        try {
            return clazz.getConstructor(Config.class).newInstance(config);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
