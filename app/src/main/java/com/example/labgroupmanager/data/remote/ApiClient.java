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

    // Default base URL for Android Emulator pointing to local Node.js Express server
    private static final String BASE_URL = "http://10.0.2.2:3000/api/";
    private static ApiService instance;

    public static synchronized ApiService getService(Context context) {
        if (instance == null) {
            SessionManager sessionManager = new SessionManager(context);

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
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            instance = retrofit.create(ApiService.class);
        }
        return instance;
    }
}
