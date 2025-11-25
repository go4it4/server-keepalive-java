package com.coding.util;

import com.coding.util.httpclient.HttpClientSingleton;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public final class HttpClientUtil {

    private HttpClientUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    public static OkHttpClient getHttpClient() {
        return HttpClientSingleton.getInstance();
    }

    public static Headers defaultHeaders(Map<String, String> headers) {
        Headers.Builder headersBuilder = new Headers.Builder();
        headersBuilder.add("Content-Type", "application/json; charset=UTF-8");

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headersBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        return headersBuilder.build();
    }

    public static String joinUrl(String baseUrl, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringJoiner stringJoiner = new StringJoiner("&");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            stringJoiner.add(entry.getKey() + "=" + entry.getValue());
        }

        return baseUrl + "?" + stringJoiner;
    }

    public static int requestGetStatusCode(String url) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        return responseCode(request);
    }

    public static String requestPost(String url, Map<String, Object> params) {
        return requestPost(url, null, params, null);
    }

    public static String requestPost(String url, Map<String, Object> urlParams,
                                     Map<String, Object> params, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(joinUrl(url, urlParams))
                .headers(defaultHeaders(headers));

        String jsonStr;
        if (params != null && !params.isEmpty()) {
            jsonStr = JsonUtil.toJsonString(params);
        } else {
            jsonStr = "{}";
        }

        RequestBody requestBody = RequestBody.Companion.create(jsonStr,
                MediaType.parse("application/json; charset=utf-8"));
        requestBuilder.post(requestBody);
        Request request = requestBuilder.build();
        return responseResult(request);
    }

    public static String responseResult(Request request) {
        try (Response response = getHttpClient().newCall(request).execute()) {
            try (ResponseBody body = response.body()) {
                return Objects.requireNonNull(body).string();
            } catch (IOException e) {
                // throw new IOException(e);
            }
        } catch (IOException e) {
            // throw new IOException(e);
        }
        return null;
    }

    public static int responseCode(Request request) {
        try (Response response = getHttpClient().newCall(request).execute()) {
            return Objects.requireNonNull(response).code();
        } catch (IOException e) {
            // throw new IOException(e);
        }

        return -1;
    }
}
