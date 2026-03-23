package com.example.tif_gr31.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Representa un producto de la API de USDA FoodData Central.
 */
public class FoodProduct {

    @SerializedName("description")
    private String description;

    @SerializedName("foodNutrients")
    private List<Nutrient> foodNutrients;

    public String getNombre() {
        return description != null ? description : "Sin nombre";
    }

    public double getKcal() {
        return getNutrientValue("Energy");
    }

    public double getProteinas() {
        return getNutrientValue("Protein");
    }

    public double getCarbohidratos() {
        return getNutrientValue("Carbohydrate, by difference");
    }

    public double getGrasas() {
        return getNutrientValue("Total lipid (fat)");
    }

    private double getNutrientValue(String name) {
        if (foodNutrients == null) return 0;
        for (Nutrient n : foodNutrients) {
            if (n.nutrientName != null && n.nutrientName.contains(name)) {
                return n.value;
            }
        }
        return 0;
    }

    public static class Nutrient {
        @SerializedName("nutrientName")
        public String nutrientName;
        @SerializedName("value")
        public double value;
    }
}
