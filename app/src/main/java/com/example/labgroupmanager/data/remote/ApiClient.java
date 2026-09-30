package com.example.labgroupmanager.data.remote;

import android.content.Context;

import com.example.labgroupmanager.data.local.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiService instance;
    private static String currentBaseUrl;

    public static synchronized ApiService getService(Context context) {
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        String baseUrl = sessionManager.getServerUrl();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = SessionManager.DEFAULT_BASE_URL;
        }
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }

        if (instance == null || !baseUrl.equals(currentBaseUrl)) {
            currentBaseUrl = baseUrl;

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();

                String token = sessionManager.getAuthToken();
                if (token != null && !token.trim().isEmpty()) {
                    builder.header("Authorization", "Bearer " + token);
                }

                builder.header("Accept", "application/json");
                return chain.proceed(builder.build());
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
        currentBaseUrl = null;
    }

    public static String getBaseUrl(Context context) {
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        return sessionManager.getServerUrl();
    }

    public static void setBaseUrl(Context context, String newUrl) {
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        sessionManager.setServerUrl(newUrl);
        resetInstance();
    }
}
