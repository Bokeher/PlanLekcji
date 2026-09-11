package com.example.planlekcji.ckziu_elektryk.client.replacements;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ReplacementServiceImpl extends ClientService implements ReplacementService {

    public ReplacementServiceImpl(Config config) {
        super(config);
    }

    @Override
    public List<Replacement> getReplacements(ReplacementType replacementType, Date date) {
        APIResponseCall apiResponseCall = getData(Endpoint.LATEST_REPLACEMENTS
                .withPlaceholders(Map.of("{mode}", replacementType.getMode(), "{date}", DateUtil.formatDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, date))));

        if (!apiResponseCall.hasResponse()) return null;

        return apiResponseCall
                .error(handleError())
                .success(successResponse -> createReplacements(successResponse.getJsonElement()));
    }

    @Override
    public Map<Date, List<Replacement>> getReplacements(ReplacementType replacementType, Date startDate, Date endDate) {
        APIResponseCall apiResponseCall = getData(Endpoint.LATEST_REPLACEMENTS_BY_PERIOD.withPlaceholders(
                Map.of("{mode}", replacementType.getMode(),
                        "{from}", DateUtil.formatDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, startDate),
                        "{to}", DateUtil.formatDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, endDate)
                )));

        if (!apiResponseCall.hasResponse()) return null;

        return apiResponseCall.error(handleError())
                .success(this::createReplacementsPeriod);
    }

    private Map<Date, List<Replacement>> createReplacementsPeriod(SuccessResponse successResponse) {
        JsonElement jsonElement = successResponse.getJsonElement();

        Map<Date, List<Replacement>> replacements = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
            replacements.put(DateUtil.parseDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, entry.getKey()), createReplacements(entry.getValue()));
        }

        return replacements;
    }

    private List<Replacement> createReplacements(JsonElement jsonElement) {
        List<Replacement> replacements = new ArrayList<>();

        jsonElement.getAsJsonArray().forEach(obj -> {
            List<ReplacementChange> changes = obj.getAsJsonObject().get("changes").getAsJsonArray()
                    .asList().stream()
                    .map(jsonElement1 -> new ReplacementChange(jsonElement1.getAsJsonObject().get("period").getAsString(),
                            jsonElement1.getAsJsonObject().get("info").getAsString())).collect(Collectors.toList());

            replacements.add(new Replacement(obj.getAsJsonObject().get("name").getAsString(), changes));
        });

        return replacements;
    }
}
