package com.example.tif_gr31.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Define los endpoints de la API de USDA FoodData Central.
 */
public interface FoodApiService {

    /**
     * Busca alimentos por nombre en USDA.
     *
     * @param query    Texto a buscar (ej: "chicken")
     * @param apiKey   Tu API KEY de USDA
     * @param pageSize Cantidad de resultados
     */
    @GET("foods/search")
    Call<FoodSearchResponse> buscarAlimentos(
            @Query("query") String query,
            @Query("api_key") String apiKey,
            @Query("pageSize") int pageSize
    );
}
