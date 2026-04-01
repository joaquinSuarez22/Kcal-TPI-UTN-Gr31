package com.example.tif_gr31.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase Singleton que se encarga de configurar y proveer la instancia de Retrofit
 * para realizar las peticiones HTTP a la API de USDA (FoodData Central).
 */
public class ApiClient {

    // URL base de la API de USDA para las consultas de alimentos
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/";

    // Instancia única de Retrofit (patrón Singleton)
    private static Retrofit retrofitInstance = null;

    /**
     * Obtiene o crea la instancia de Retrofit.
     * Configura un interceptor de logs para depurar las peticiones en la consola
     * y un conversor de GSON para transformar las respuestas JSON en objetos Java.
     * 
     * @return Instancia configurada de Retrofit.
     */
    public static Retrofit getClient() {
        if (retrofitInstance == null) {
            // Interceptor para ver las peticiones y respuestas en el Logcat
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cliente HTTP que incluye el interceptor de logs
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            // Construcción de la instancia de Retrofit
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }

    /**
     * Método de conveniencia para obtener directamente el servicio definido en FoodApiService.
     * 
     * @return Instancia del servicio API.
     */
    public static FoodApiService getService() {
        return getClient().create(FoodApiService.class);
    }
}
