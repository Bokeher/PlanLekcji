package com.example.planlekcji.ckziu_elektryk.client.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.example.planlekcji.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.function.Consumer;

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

    protected APIResponseCall getData(@NonNull Endpoint endpoint) {
        Request request = new Request.Builder()
                .addHeader("Authorization", config.getToken())
                .url(config.getAPIUrl() + "/" + endpoint.getName())
                .get()
                .build();

        APIResponseCall apiResponseCall = new APIResponseCall();

        try (Response response = this.httpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            String bodyContent = "";

            if (body != null) bodyContent = body.string();

            JsonElement jsonElement = gson.fromJson(bodyContent, JsonElement.class);
            if (!response.isSuccessful()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.has("message")) {
                    bodyContent = jsonObject.get("message").getAsString();
                }

                apiResponseCall.setErrorResponse(new ErrorResponse(response.code(), bodyContent));

                return apiResponseCall;
            }

            apiResponseCall.setSuccessResponse(new SuccessResponse(response.code(), jsonElement));
        } catch (IOException exception) {
            ToastUtils.showToast(MainActivity.getContext(), "Nie udało się nawiązać połączenia z serwerem.", true);
        }

        return apiResponseCall;
    }

    protected @NonNull Consumer<ErrorResponse> printError() {
        return errorResponse -> Log.e("error", errorResponse.getMessage());
    }
}
