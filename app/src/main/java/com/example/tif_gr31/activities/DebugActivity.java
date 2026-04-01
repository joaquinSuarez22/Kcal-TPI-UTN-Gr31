package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Actividad interna diseñada para facilitar las pruebas durante el desarrollo (Debug).
 * Permite realizar acciones masivas como la generación de datos de prueba en Firestore.
 */
public class DebugActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        // Inicialización de servicios
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Configuración del botón para cargar datos de prueba
        Button btnCargarComidas = findViewById(R.id.btnCargarComidasDebug);
        if (btnCargarComidas != null) {
            btnCargarComidas.setOnClickListener(v -> cargarComidasDePrueba());
        }
    }

    /**
     * Genera registros aleatorios de comidas para los últimos 7 días.
     * Esto es extremadamente útil para probar los gráficos de la pantalla de Estadísticas
     * y las Recomendaciones sin tener que cargar datos manualmente uno por uno.
     */
    private void cargarComidasDePrueba() {
        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(this, "Error: Debes estar autenticado para cargar datos", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        String[] categorias = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snacks"};

        // Itera para crear registros de los últimos 7 días (0 a 6 días atrás)
        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            
            // Formatea la fecha de manera consistente con el resto de la aplicación (yyyy-MM-dd)
            String fecha = String.format(Locale.US, "%d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

            // Genera entre 2 y 3 comidas aleatorias por cada día
            int numComidas = 2 + random.nextInt(2);
            for (int j = 0; j < numComidas; j++) {
                Map<String, Object> comidaMap = new HashMap<>();
                
                // Genera calorías aleatorias entre 300 y 800 kcal
                double kcal = 300 + random.nextInt(500);
                
                // Estructura de datos compatible con los modelos de la aplicación
                comidaMap.put("categoria", categorias[random.nextInt(categorias.length)]);
                comidaMap.put("fecha", fecha);
                comidaMap.put("timestamp", cal.getTime());
                comidaMap.put("calorias", kcal);
                comidaMap.put("proteinas", kcal * 0.05);     // Cálculo estimado para tener datos coherentes
                comidaMap.put("carbohidratos", kcal * 0.12);
                comidaMap.put("grasas", kcal * 0.03);
                
                // Crea una lista de ingredientes de prueba
                List<Map<String, Object>> ingredientes = new ArrayList<>();
                Map<String, Object> ing = new HashMap<>();
                ing.put("nombre", "Comida de prueba " + (j + 1));
                ing.put("gramos", 200.0);
                ing.put("kcal", kcal);
                ingredientes.add(ing);
                comidaMap.put("ingredientes", ingredientes);

                // Guarda cada comida en la subcolección del usuario en Firestore
                db.collection("usuarios")
                  .document(userId)
                  .collection("comidas")
                  .add(comidaMap);
            }
        }
        Toast.makeText(this, "Datos de prueba generados exitosamente", Toast.LENGTH_LONG).show();
    }
}
