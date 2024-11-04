package com.example.planlekcji.ckziu_elektryk.client;

import android.util.Log;

import com.example.planlekcji.ckziu_elektryk.client.response.APIResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class ClientService {

    private final OkHttpClient httpClient;
    private final Gson gson;
    private final Config config;

    protected ClientService(Config config) {
        this.config = config;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    protected Optional<APIResponse> getData(Endpoint endpoint) {
        Request request = new Request.Builder()
                .addHeader("Authorization", config.getToken())
                .url(config.getAPIUrl())
                .get()
                .build();

        try (Response response = this.httpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            String bodyContent = "";

            if (body != null) bodyContent = body.string();

            if (!response.isSuccessful())
                return Optional.of(new ErrorResponse(response.code(), bodyContent));

            JsonElement jsonElement = gson.fromJson(bodyContent, JsonElement.class);

            return Optional.of(new SuccessResponse(response.code(), jsonElement));

        } catch (IOException exception) {
            Log.e("error", exception.getMessage(), exception);
        }

        return Optional.empty();
    }
}
