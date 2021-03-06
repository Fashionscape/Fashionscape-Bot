/*
 * Copyright (c) 2020. Aleksei Gryczewski
 * All rights reserved.
 */

package dev.salmonllama.fsbot.endpoints.imgur;

import dev.salmonllama.fsbot.config.BotConfig;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ImgurAPIConnection {
    private final String REQUEST_URL = "https://api.imgur.com/3/image/";

    private final String CLIENT_ID; // Required for uploading
    private final String BEARER_TOKEN; // Required for deleting

    // Create the okhttp objects. Use methods to complete requests.
    private final OkHttpClient client;
    private final Request.Builder requestBuilder;

    public ImgurAPIConnection() {
        CLIENT_ID = BotConfig.IMGUR_ID;
        BEARER_TOKEN = BotConfig.IMGUR_BEARER;

        client = new OkHttpClient().newBuilder().build();
        requestBuilder = new Request.Builder();
    }

    public CompletableFuture<ImgurUpload> uploadImage(String link) {
        return CompletableFuture.supplyAsync(() -> uploadImageExec(link));
    }

    public CompletableFuture<Boolean> deleteImage(String deleteHash) {
        return CompletableFuture.supplyAsync(() -> deleteImageExec(deleteHash));
    }

    private ImgurUpload uploadImageExec(String discordLink) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image", discordLink).build();

        Request request = requestBuilder
                .url(REQUEST_URL)
                .method("POST", body)
                .addHeader("Authorization", CLIENT_ID)
                .build();

        JSONObject json;
        try (Response response = client.newCall(request).execute()) {
            json = new JSONObject(response.body().string()).getJSONObject("data");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return new ImgurUpload.ImgurUploadBuilder()
                .setId(json.getString("id"))
                .setDateTime(json.getLong("datetime"))
                .setDeleteHash(json.getString("deletehash"))
                .setLink(json.getString("link"))
                .build();
    }

    private boolean deleteImageExec(String deleteHash) {
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).build();

        Request request = requestBuilder
                .url(REQUEST_URL.concat(deleteHash))
                .method("DELETE", body)
                .addHeader("Authorization", BEARER_TOKEN)
                .build();

        boolean success;
        try (Response response = client.newCall(request).execute()) {
            success = new JSONObject(response.body().string()).getBoolean("success");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return success;
    }
}
