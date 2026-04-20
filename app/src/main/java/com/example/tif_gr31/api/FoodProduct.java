package com.example.tif_gr31.api;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa un producto alimenticio. 
 * Soporta tanto datos provenientes de la API de USDA como datos locales (CSV).
 */
public class FoodProduct {

    @SerializedName("description")
    private String description;

    @SerializedName("foodNutrients")
    private List<Nutrient> foodNutrients;

    /**
     * @return El nombre descriptivo del alimento.
     */
    public String getNombre() {
        return description != null ? description : "Sin nombre";
    }

    /**
     * Permite establecer manualmente el nombre (útil para datos locales).
     */
    public void setNombreLocal(String nombre) {
        this.description = nombre;
    }

    /**
     * Configura los nutrientes manualmente (para datos locales del CSV).
     */
    public void setNutrientesLocales(double kcal, double proteinas, double carbos, double grasas) {
        this.foodNutrients = new ArrayList<>();
        addNutrient("Energy", kcal);
        addNutrient("Protein", proteinas);
        addNutrient("Carbohydrate, by difference", carbos);
        addNutrient("Total lipid (fat)", grasas);
    }

    private void addNutrient(String name, double value) {
        Nutrient n = new Nutrient();
        n.nutrientName = name;
        n.value = value;
        this.foodNutrients.add(n);
    }

    /**
     * @return Valor en kcal por cada 100g/ml.
     */
    public double getKcal() {
        return getNutrientValue("Energy");
    }

    /**
     * @return Gramos de proteína por cada 100g/ml.
     */
    public double getProteinas() {
        return getNutrientValue("Protein");
    }

    /**
     * @return Gramos de carbohidratos por cada 100g/ml.
     */
    public double getCarbohidratos() {
        return getNutrientValue("Carbohydrate, by difference");
    }

    /**
     * @return Gramos de grasa por cada 100g/ml.
     */
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
