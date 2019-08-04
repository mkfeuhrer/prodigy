package com.gcc;

import android.content.Context;
import android.os.Handler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Client {

    private final int CORE_POOL_SIZE = 2;
    private final int MAX_POOL_SIZE = 4;
    private final int KEEP_ALIVE_SEC = 60;
    private final long CONNECT_TIMEOUT_MILLIS = 5000;
    private final long READ_TIMEOUT_MILLIS = 5000;
    private final long WRITE_TIMEOUT_MILLIS = 5000;

    private static Client client;
    private static ReentrantLock lock = new ReentrantLock();

    public static Client getClient(Context context) {
        lock.lock();
        if (client == null) {
            client = new Client(context);
        }
        lock.unlock();
        return client;
    }

    private WeakReference<Context> wContext;
    private ThreadPoolExecutor executor;
    private OkHttpClient httpClient;
    private Handler handler;

    private Client(Context context) {

//        this.wContext = new WeakReference<>(context);
        this.handler = new Handler(context.getMainLooper());
        this.executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SEC, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setName("POOL THREAD : " + System.nanoTime());
                return thread;
            }
        });
        this.httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build();
    }

    public Call execute(Request request, ClientHandler handler) {
        Call call = this.httpClient.newCall(request);
        WeakReference<ClientHandler> weakHandler = new WeakReference<>(handler);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ClientHandler clientHandler = weakHandler.get();
                if (clientHandler != null) {
                    Client.this.handler.post(() -> {
                        clientHandler.handle(null, e, Event.FAILURE);
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ClientHandler clientHandler = weakHandler.get();
                if (clientHandler != null) {
                    String result = response.body().string();
                    Client.this.handler.post(() -> {
                        clientHandler.handle(result, null, Event.SUCCESS);
                    });
                }
            }
        });
        return call;
    }

    public static enum Event {
        SUCCESS, FAILURE
    }

    @FunctionalInterface
    public static interface ClientHandler {
        public void handle(String response, IOException exp, Event event);
    }

}
