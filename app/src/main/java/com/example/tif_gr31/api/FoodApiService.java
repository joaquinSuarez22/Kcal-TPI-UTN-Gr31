package com.example.tif_gr31.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interfaz que define los endpoints de la API de USDA FoodData Central.
 * Utiliza anotaciones de Retrofit para definir el tipo de petición HTTP y los parámetros.
 */
public interface FoodApiService {

    /**
     * Busca alimentos por nombre en la base de datos de USDA.
     *
     * @param query    Texto o palabra clave a buscar (ej: "manzana", "pollo").
     * @param apiKey   Clave de API personal obtenida de USDA para autenticar la petición.
     * @param pageSize Número máximo de resultados que se desean recibir en la respuesta.
     * @return Un objeto Call que, al ejecutarse, devolverá un FoodSearchResponse con los resultados.
     */
    @GET("foods/search")
    Call<FoodSearchResponse> buscarAlimentos(
            @Query("query") String query,
            @Query("api_key") String apiKey,
            @Query("pageSize") int pageSize
    );
}
