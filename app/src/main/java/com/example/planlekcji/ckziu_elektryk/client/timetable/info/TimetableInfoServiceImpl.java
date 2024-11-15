package com.example.planlekcji.ckziu_elektryk.client.timetable.info;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.google.gson.JsonElement;

import java.time.LocalDate;
import java.util.Optional;

class TimetableInfoServiceImpl extends ClientService implements TimetableInfoService {
    protected TimetableInfoServiceImpl(Config config) {
        super(config);
    }

    @Override
    public Optional<TimetableInfo> getTimetableInfo() {
        APIResponseCall apiResponseCall = getData(Endpoint.TIMETABLE_INFO);

        if (!apiResponseCall.hasResponse())
            return Optional.empty();

        return Optional.of(apiResponseCall
                .error(printError())
                .success(successResponse -> {
                    JsonElement jsonElement = successResponse.getJsonElement();

                    return new TimetableInfo(
                            jsonElement.getAsJsonObject().get("apply_at").getAsString(),
                            LocalDate.parse(jsonElement.getAsJsonObject().get("generated_at").getAsString(),
                                    TimetableInfo.GENERATED_AT_FORMATTER)
                    );
                }));
    }
}
