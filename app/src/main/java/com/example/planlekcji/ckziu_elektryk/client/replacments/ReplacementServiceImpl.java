package com.example.planlekcji.ckziu_elektryk.client.replacments;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.utils.ParamValidator;
import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Optional;

class ReplacementServiceImpl extends ClientService implements ReplacementService {

    public ReplacementServiceImpl(Config config) {
        super(config);
    }

    @Override
    public Optional<Replacement> getLatestReplacement() {
        APIResponseCall apiResponseCall = getData(Endpoint.LATEST_REPLACEMENTS);

        if (!apiResponseCall.hasResponse()) return Optional.empty();

        return Optional.of(apiResponseCall
                .error(printError())
                .success(this::createReplacement));
    }

    @Override
    public Optional<Replacement> getReplacement(String fileName) {
        ParamValidator.checkNotNullAndNotEmpty(fileName);

        APIResponseCall apiResponseCall = getData(Endpoint.REPLACEMENTS_BY_FILE_NAME
                .withPlaceholders(Map.of("{file_name}", fileName)));

        if (!apiResponseCall.hasResponse()) return Optional.empty();

        return Optional.of(apiResponseCall
                .error(printError())
                .success(this::createReplacement));
    }

    private Replacement createReplacement(SuccessResponse successResponse) {
        JsonElement jsonElement = successResponse.getJsonElement();

        return new Replacement(
                jsonElement.getAsJsonObject().get("file_name").getAsString(),
                jsonElement.getAsJsonObject().get("content").getAsString()
        );
    }
}
