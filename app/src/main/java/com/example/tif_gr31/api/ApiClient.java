package com.example.tif_gr31.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton que provee la instancia de Retrofit configurada para USDA FoodData Central.
 */
public class ApiClient {

    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";

    private static Retrofit retrofitInstance = null;

    public static Retrofit getClient() {
        if (retrofitInstance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }

    public static FoodApiService getService() {
        return getClient().create(FoodApiService.class);
    }
}
