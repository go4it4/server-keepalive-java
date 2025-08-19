package com.coding.util;

import com.coding.util.httpclient.HttpClientSingleton;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public final class HttpClientUtil {

    private HttpClientUtil() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    public static OkHttpClient getHttpClient() {
        return HttpClientSingleton.getInstance();
    }

    public static int requestGet0(String url) {

        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        Call call = getHttpClient().newCall(request);

        try (Response response = call.execute()) {
            return Objects.requireNonNull(response).code();
        } catch (IOException e) {
            // throw new IOException(e);
        }

        return -1;
    }

}
