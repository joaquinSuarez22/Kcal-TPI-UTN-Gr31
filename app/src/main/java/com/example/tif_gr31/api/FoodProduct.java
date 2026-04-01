package com.example.tif_gr31.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Clase que representa un producto alimenticio individual devuelto por la API de USDA.
 * Contiene la descripción del alimento y su información nutricional detallada.
 */
public class FoodProduct {

    // Descripción o nombre común del alimento
    @SerializedName("description")
    private String description;

    // Lista de nutrientes asociados al alimento (calorías, proteínas, etc.)
    @SerializedName("foodNutrients")
    private List<Nutrient> foodNutrients;

    /**
     * @return El nombre descriptivo del alimento.
     */
    public String getNombre() {
        return description != null ? description : "Sin nombre";
    }

    /**
     * Busca el valor energético (calorías) dentro de la lista de nutrientes.
     * @return Valor en kcal por cada 100g/ml.
     */
    public double getKcal() {
        return getNutrientValue("Energy");
    }

    /**
     * Busca el contenido de proteínas.
     * @return Gramos de proteína por cada 100g/ml.
     */
    public double getProteinas() {
        return getNutrientValue("Protein");
    }

    /**
     * Busca el contenido de carbohidratos.
     * @return Gramos de carbohidratos por cada 100g/ml.
     */
    public double getCarbohidratos() {
        return getNutrientValue("Carbohydrate, by difference");
    }

    /**
     * Busca el contenido de grasas totales.
     * @return Gramos de grasa por cada 100g/ml.
     */
    public double getGrasas() {
        return getNutrientValue("Total lipid (fat)");
    }

    /**
     * Método auxiliar para buscar un nutriente específico por su nombre en la lista de nutrientes.
     * 
     * @param name Nombre o parte del nombre del nutriente (ej: "Energy", "Protein").
     * @return El valor del nutriente si se encuentra, de lo contrario 0.
     */
    private double getNutrientValue(String name) {
        if (foodNutrients == null) return 0;
        for (Nutrient n : foodNutrients) {
            if (n.nutrientName != null && n.nutrientName.contains(name)) {
                return n.value;
            }
        }
        return 0;
    }

    /**
     * Clase interna que representa un nutriente específico (nombre y valor numérico).
     */
    public static class Nutrient {
        @SerializedName("nutrientName")
        public String nutrientName;

        @SerializedName("value")
        public double value;
    }
}
