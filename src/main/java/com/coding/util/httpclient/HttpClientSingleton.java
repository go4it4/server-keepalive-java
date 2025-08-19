package com.coding.util.httpclient;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class HttpClientSingleton {

    private static final long TIMEOUT = 5L;

    private static volatile OkHttpClient client;

    private static final ReentrantLock LOCK = new ReentrantLock();

    private HttpClientSingleton() {
        throw new UnsupportedOperationException(this + "cannot be instantiated");
    }

    public static OkHttpClient getInstance() {
        if (client == null) {
            LOCK.lock();
            try {
                if (client == null) {
                    client = createHttpClient();
                }
            } finally {
                LOCK.unlock();
            }
        }
        return client;
    }

    private static OkHttpClient createHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 90, TimeUnit.SECONDS))
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS);

        return builder.build();
    }

}
