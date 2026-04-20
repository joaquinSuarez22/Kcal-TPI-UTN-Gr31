package com.example.tif_gr31.utils;

import android.content.Context;
import android.util.Log;

import com.example.tif_gr31.api.FoodProduct;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Ayudante para leer y buscar alimentos en el archivo CSV local.
 * Permite complementar los datos de la API con una base de datos local en español.
 */
public class CsvFoodHelper {
    private static final String TAG = "CsvFoodHelper";
    private static final String FILE_NAME = "INCAPTCA2009.csv";

    /**
     * Busca alimentos en el archivo CSV que coincidan con el término de búsqueda.
     * 
     * @param context Contexto de la aplicación para acceder a los assets.
     * @param query   Texto a buscar en el nombre del alimento.
     * @return Lista de FoodProduct encontrados.
     */
    public static List<FoodProduct> buscarEnCsv(Context context, String query) {
        List<FoodProduct> resultados = new ArrayList<>();
        String queryLower = query.toLowerCase().trim();

        try (InputStream is = context.getAssets().open(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                // El CSV usa comas, pero algunos nombres pueden tener comas internas entre comillas.
                // Esta expresión regular simple maneja la mayoría de los casos de este CSV específico.
                String[] columns = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (columns.length > 8) {
                    String nombre = columns[1].replace("\"", "").trim();
                    
                    if (nombre.toLowerCase().contains(queryLower)) {
                        FoodProduct product = new FoodProduct();
                        
                        // Mapeo manual de columnas basado en la estructura del CSV:
                        // 1: NOMBRE, 5: Energia(kcal), 6: Proteina(g), 7: Grasa_Total(g), 8: HDC(g)
                        
                        double kcal = parseSafe(columns[5]);
                        double proteinas = parseSafe(columns[6]);
                        double grasas = parseSafe(columns[7]);
                        double carbos = parseSafe(columns[8]);

                        // Usamos una implementación simplificada para el objeto FoodProduct local
                        // Nota: Tendremos que ajustar FoodProduct para permitir setters o crear una subclase
                        // Por ahora, lo simulamos para la UI de búsqueda.
                        product.setNombreLocal(nombre);
                        product.setNutrientesLocales(kcal, proteinas, carbos, grasas);
                        
                        resultados.add(product);
                    }
                }
                
                // Limitar resultados locales para no saturar la UI
                if (resultados.size() >= 20) break;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error leyendo CSV: " + e.getMessage());
        }

        return resultados;
    }

    private static double parseSafe(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return 0;
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
