package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad principal que actúa como Dashboard de la aplicación.
 * Muestra el progreso diario de calorías y macronutrientes, permite agregar comidas
 * por categorías y visualiza el consumo desglosado.
 */
public class InicioActivity extends AppCompatActivity {

    private static final String TAG = "InicioActivity";
    
    // Vistas de resumen calórico central
    private TextView txtCaloriasRestantesCentro, txtObjetivoTotalCalorias;
    private CircularProgressIndicator progresoCaloriasCircular;
    
    // Vistas de macronutrientes (Carbos, Proteínas, Grasas)
    private TextView txtCarbosGramos, txtProteinasGramos, txtGrasasGramos;
    private CircularProgressIndicator progresoCarbos, progresoProteinas, progresoGrasas;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    
    // Variables de estado para los cálculos
    private double objetivoCaloricoTotal = 0;
    private double calsConsumidas = 0, carbosConsumidos = 0, protConsumidas = 0, grasasConsumidas = 0;

    // Configuración de categorías de comidas
    private final String[] categorias = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snacks"};
    private final String[] emojis = {"🥐", "🍝", "☕", "🍲", "🍎"};
    private final Map<String, Double> consumoPorCategoria = new HashMap<>();
    private final Map<String, View> mealViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita diseño de pantalla completa
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        // Ajuste de paddings para sistemas con "notch" o barras de navegación gestual
        View mainLayout = findViewById(R.id.main);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializa el mapa de consumo por categoría en 0
        for (String cat : categorias) {
            consumoPorCategoria.put(cat, 0.0);
        }

        vincularVistas();
        setupMealItems();

        // Inicializa la barra de navegación flotante inferior
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_inicio);

        // Botón secreto para herramientas de desarrollo
        MaterialButton btnDebug = findViewById(R.id.btnDebug);
        if (btnDebug != null) {
            btnDebug.setOnClickListener(v -> {
                Intent intent = new Intent(InicioActivity.this, DebugActivity.class);
                startActivity(intent);
            });
        }

        // Primera carga de datos
        cargarDatosResumen();
    }

    /**
     * Vincula las variables con los componentes del layout XML.
     */
    private void vincularVistas() {
        txtCaloriasRestantesCentro = findViewById(R.id.txtCaloriasRestantesCentro);
        txtObjetivoTotalCalorias = findViewById(R.id.txtObjetivoTotalCalorias);
        progresoCaloriasCircular = findViewById(R.id.progresoCaloriasCircular);

        txtCarbosGramos = findViewById(R.id.txtCarbosGramos);
        txtProteinasGramos = findViewById(R.id.txtProteinasGramos);
        txtGrasasGramos = findViewById(R.id.txtGrasasGramos);
        progresoCarbos = findViewById(R.id.progresoCarbos);
        progresoProteinas = findViewById(R.id.progresoProteinas);
        progresoGrasas = findViewById(R.id.progresoGrasas);

        // Mapea los items del layout de comidas
        mealViews.put("Desayuno", findViewById(R.id.itemDesayuno));
        mealViews.put("Almuerzo", findViewById(R.id.itemAlmuerzo));
        mealViews.put("Merienda", findViewById(R.id.itemMerienda));
        mealViews.put("Cena", findViewById(R.id.itemCena));
        mealViews.put("Snacks", findViewById(R.id.itemSnacks));
    }

    /**
     * Configura dinámicamente cada tarjeta de categoría de comida (Nombre, Emoji y acción del botón +).
     */
    private void setupMealItems() {
        for (int i = 0; i < categorias.length; i++) {
            final String cat = categorias[i];
            View view = mealViews.get(cat);
            if (view != null) {
                TextView tvNombre = view.findViewById(R.id.txtComidaNombre);
                TextView tvIcono = view.findViewById(R.id.imgComidaIcono);
                View btnAdd = view.findViewById(R.id.btnAgregarComida);

                if (tvNombre != null) tvNombre.setText(cat);
                if (tvIcono != null) tvIcono.setText(emojis[i]);
                
                if (btnAdd != null) {
                    // El botón "+" redirige a RegistrarComidaActivity pasando la categoría seleccionada
                    btnAdd.setOnClickListener(v -> {
                        Intent intent = new Intent(InicioActivity.this, RegistrarComidaActivity.class);
                        intent.putExtra("CATEGORIA_SELECCIONADA", cat);
                        startActivity(intent);
                    });
                }
            }
        }
    }

    /**
     * Obtiene el objetivo calórico del perfil del usuario desde Firestore
     * y luego dispara la carga de los consumos del día actual.
     */
    private void cargarDatosResumen() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("usuarios").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Object perfilObj = doc.get("perfil");
                        if (perfilObj instanceof Map) {
                            Map<String, Object> perfil = (Map<String, Object>) perfilObj;
                            if (perfil.containsKey("calorias_estimadas")) {
                                Object calsObj = perfil.get("calorias_estimadas");
                                if (calsObj instanceof Number) {
                                    objetivoCaloricoTotal = ((Number) calsObj).doubleValue();
                                }
                            }
                        }
                        // Una vez obtenido el objetivo, cargamos qué ha comido hoy
                        obtenerConsumoDelDia(user.getUid());
                    }
                });
    }

    /**
     * Consulta en Firestore todas las comidas registradas por el usuario con la fecha de hoy.
     * Suma los valores de calorías y macronutrientes.
     * 
     * @param userId UID del usuario activo.
     */
    private void obtenerConsumoDelDia(String userId) {
        Calendar cal = Calendar.getInstance();
        String hoy = String.format(Locale.US, "%d-%02d-%02d", 
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        db.collection("usuarios").document(userId).collection("comidas")
                .whereEqualTo("fecha", hoy)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    resetearTotales();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        calsConsumidas += doc.getDouble("calorias") != null ? doc.getDouble("calorias") : 0;
                        carbosConsumidos += doc.getDouble("carbohidratos") != null ? doc.getDouble("carbohidratos") : 0;
                        protConsumidas += doc.getDouble("proteinas") != null ? doc.getDouble("proteinas") : 0;
                        grasasConsumidas += doc.getDouble("grasas") != null ? doc.getDouble("grasas") : 0;
                        
                        // También agrupamos el consumo calórico por categoría (Desayuno, etc.)
                        String cat = doc.getString("categoria");
                        if (cat != null && consumoPorCategoria.containsKey(cat)) {
                            Double valorActual = consumoPorCategoria.get(cat);
                            consumoPorCategoria.put(cat, (valorActual != null ? valorActual : 0) + (doc.getDouble("calorias") != null ? doc.getDouble("calorias") : 0));
                        }
                    }
                    // Actualizamos la interfaz con los nuevos totales
                    actualizarUI();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error obteniendo comidas: ", e));
    }

    /**
     * Reinicia los contadores antes de cada recarga de datos.
     */
    private void resetearTotales() {
        calsConsumidas = 0; carbosConsumidos = 0; protConsumidas = 0; grasasConsumidas = 0;
        for (String cat : categorias) consumoPorCategoria.put(cat, 0.0);
    }

    /**
     * Actualiza todos los elementos visuales (textos y barras de progreso) con los datos actuales.
     */
    private void actualizarUI() {
        // Cálculo de calorías restantes
        double restantes = Math.max(0, objetivoCaloricoTotal - calsConsumidas);
        if (txtCaloriasRestantesCentro != null) {
            txtCaloriasRestantesCentro.setText(String.format(Locale.US, "%.0f", restantes));
        }
        if (txtObjetivoTotalCalorias != null) {
            txtObjetivoTotalCalorias.setText(String.format(Locale.US, "Objetivo: %.0f kcal", objetivoCaloricoTotal));
        }
        
        // Indicador circular de calorías totales
        if (objetivoCaloricoTotal > 0 && progresoCaloriasCircular != null) {
            int progreso = (int) ((calsConsumidas / objetivoCaloricoTotal) * 100);
            progresoCaloriasCircular.setProgress(Math.min(progreso, 100));
        }

        // Objetivos de macronutrientes basados en porcentajes estándar (50/20/30)
        double objCarbos = (objetivoCaloricoTotal * 0.5) / 4;
        double objProt = (objetivoCaloricoTotal * 0.2) / 4;
        double objGrasa = (objetivoCaloricoTotal * 0.3) / 9;

        // Actualiza los indicadores de cada macro
        actualizarMacroCircle(progresoCarbos, txtCarbosGramos, carbosConsumidos, objCarbos);
        actualizarMacroCircle(progresoProteinas, txtProteinasGramos, protConsumidas, objProt);
        actualizarMacroCircle(progresoGrasas, txtGrasasGramos, grasasConsumidas, objGrasa);

        // Actualiza las etiquetas de calorías consumidas por cada categoría de comida
        for (String cat : categorias) {
            double objCat = cat.equals("Snacks") ? objetivoCaloricoTotal * 0.1 : objetivoCaloricoTotal * 0.225;
            View v = mealViews.get(cat);
            if (v != null) {
                TextView tvCals = v.findViewById(R.id.txtComidaCalorias);
                if (tvCals != null) {
                    Double consumido = consumoPorCategoria.get(cat);
                    tvCals.setText(String.format(Locale.US, "%.0f / %.0f kcal", (consumido != null ? consumido : 0), objCat));
                }
            }
        }
    }

    /**
     * Ayudante para actualizar un indicador circular de macronutriente y su texto asociado.
     */
    private void actualizarMacroCircle(CircularProgressIndicator cp, TextView tv, double valor, double objetivo) {
        if (tv != null) tv.setText(String.format(Locale.US, "%.0fg", valor));
        if (objetivo > 0 && cp != null) {
            int prog = (int) ((valor / objetivo) * 100);
            cp.setProgress(Math.min(prog, 100));
        }
    }

    /**
     * Cuando el usuario vuelve a esta pantalla (ej: tras registrar una comida), refrescamos los datos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosResumen();
    }
}
