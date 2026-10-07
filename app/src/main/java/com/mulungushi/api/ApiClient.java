package com.mulungushi.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:3000/";
    public static final String BASE_URL_LAN      = "http://192.168.1.15:3000/";
    public static final String BASE_URL_DEPLOYED = "https://your-domain.example.com/";

    private static final String BASE_URL = BASE_URL_EMULATOR;

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static String authToken = null;

    public static void setAuthToken(String token) {
        authToken = token;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void clearAuthToken() {
        authToken = null;
    }

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            okhttp3.Interceptor authInterceptor = chain -> {
                okhttp3.Request original = chain.request();
                okhttp3.Request.Builder builder = original.newBuilder()
                        .header("Accept", "application/json");

                if (authToken != null && !authToken.isEmpty()) {
                    builder.header("Authorization", "Bearer " + authToken);
                }

                return chain.proceed(builder.build());
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }
}