package com.example.tif_gr31.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Clase que mapea la respuesta de búsqueda de la API de USDA.
 * Contiene la lista de alimentos encontrados y metadatos sobre la búsqueda.
 */
public class FoodSearchResponse {

    // Lista de productos alimenticios que coinciden con los criterios de búsqueda
    @SerializedName("foods")
    private List<FoodProduct> foods;

    // Número total de resultados encontrados en la base de datos de USDA
    @SerializedName("totalHits")
    private int totalHits;

    /**
     * @return La lista de objetos FoodProduct obtenidos.
     */
    public List<FoodProduct> getFoods() { 
        return foods; 
    }

    /**
     * @return El número total de coincidencias encontradas por la API.
     */
    public int getTotalHits() { 
        return totalHits; 
    }
}
