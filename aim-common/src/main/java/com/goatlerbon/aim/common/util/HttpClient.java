package com.goatlerbon.aim.common.util;

import okhttp3.*;

import java.io.IOException;

/**
 * 利用 OkHttp发送 http 请求的工具类
 */
public class HttpClient {
    private static MediaType mediaType = MediaType.parse("application/json");

    public static Object call(OkHttpClient okHttpClient, String param, String serverUrl) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType,param);

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        return response;
    }
}
