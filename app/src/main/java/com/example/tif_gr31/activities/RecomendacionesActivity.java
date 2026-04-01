package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tif_gr31.R;
import com.example.tif_gr31.models.Recomendacion;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.example.tif_gr31.utils.RecomendacionesData;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad que genera y muestra recomendaciones personalizadas al usuario.
 * Analiza el consumo calórico de la última semana y lo compara con el objetivo del perfil
 * para ofrecer consejos útiles sobre nutrición y hábitos.
 */
public class RecomendacionesActivity extends AppCompatActivity {

    private static final String TAG = "RecomendacionesAct";
    
    private ImageView btnBack;
    private TextView tvEmojiIcono, tvTituloRecomendacion, tvTextoRecomendacion, tvConsumoPromedio;
    private MaterialButton btnVolverInicio;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    // Valores predeterminados para el cálculo
    private double objetivoCalorico = 2000;
    private String objetivoUsuario = "mantener_peso";
    private final Map<String, Double> caloriasPorDia = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        vincularVistas();
        
        // Configuración de la barra de navegación flotante inferior
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_estadisticas);

        // Listeners para cerrar la actividad
        btnBack.setOnClickListener(v -> finish());
        btnVolverInicio.setOnClickListener(v -> finish());

        // Inicia el proceso de carga de datos y cálculo de recomendaciones
        cargarDatosYCalcular();
    }

    private void vincularVistas() {
        btnBack = findViewById(R.id.btnBack);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        tvEmojiIcono = findViewById(R.id.tvEmojiIcono);
        tvTituloRecomendacion = findViewById(R.id.tvTituloRecomendacion);
        tvTextoRecomendacion = findViewById(R.id.tvTextoRecomendacion);
        tvConsumoPromedio = findViewById(R.id.tvConsumoPromedio);
    }

    /**
     * Recupera el perfil del usuario (objetivo y calorías estimadas) de Firestore.
     * Una vez obtenidos, procede a buscar los registros de comida de la última semana.
     */
    private void cargarDatosYCalcular() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> perfil = (Map<String, Object>) doc.get("perfil");
                        if (perfil != null) {
                            // Normaliza el objetivo para facilitar las comparaciones
                            objetivoUsuario = String.valueOf(perfil.get("objetivo")).toLowerCase().replace(" ", "_");
                            if (perfil.get("calorias_estimadas") != null) {
                                objetivoCalorico = ((Number) perfil.get("calorias_estimadas")).doubleValue();
                            }
                        }
                        // Paso 2: Obtener historial de consumo
                        obtenerRegistrosUltimaSemana(uid);
                    }
                });
    }

    /**
     * Consulta las comidas registradas en los últimos 7 días.
     * Agrupa las calorías totales consumidas por cada día.
     */
    private void obtenerRegistrosUltimaSemana(String uid) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String fechaLimite = String.format(Locale.US, "%d-%02d-%02d", 
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        db.collection("usuarios").document(uid).collection("comidas")
                .whereGreaterThanOrEqualTo("fecha", fechaLimite)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    caloriasPorDia.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String fecha = doc.getString("fecha");
                        double kcal = doc.getDouble("calorias") != null ? doc.getDouble("calorias") : 0;
                        
                        if (fecha != null) {
                            Double actual = caloriasPorDia.get(fecha);
                            caloriasPorDia.put(fecha, (actual != null ? actual : 0) + kcal);
                        }
                    }
                    // Paso 3: Analizar los datos y seleccionar la recomendación
                    procesarRecomendacion();
                });
    }

    /**
     * Calcula el promedio de consumo diario y dispara la lógica de selección de recomendación.
     */
    private void procesarRecomendacion() {
        int diasConRegistros = caloriasPorDia.size();
        double sumaTotal = 0;
        for (double val : caloriasPorDia.values()) sumaTotal += val;
        double promedio = diasConRegistros > 0 ? sumaTotal / diasConRegistros : 0;

        // Muestra el resumen informativo al usuario
        if (tvConsumoPromedio != null) {
            tvConsumoPromedio.setText(String.format(Locale.getDefault(), "Tu promedio: %.0f kcal/día (%d días registrados)", promedio, diasConRegistros));
        }

        // Determina qué recomendación mostrar según el comportamiento detectado
        int idRecomendacion = seleccionarIdRecomendacion(promedio, diasConRegistros);
        mostrarRecomendacion(idRecomendacion);
    }

    /**
     * Lógica de negocio para seleccionar un ID de recomendación.
     * Evalúa la cantidad de días registrados y la desviación respecto al objetivo.
     * 
     * @param consumo Promedio calórico diario calculado.
     * @param dias    Cantidad de días que tienen al menos un registro.
     * @return ID de la recomendación en el repositorio estático.
     */
    private int seleccionarIdRecomendacion(double consumo, int dias) {
        // Prioridad 1: Registros insuficientes
        if (dias < 3) return 12; // Mensaje de bienvenida/primer uso
        if (dias < 5) return 11; // Sugerencia de mayor consistencia
        
        // Prioridad 2: Salud y seguridad (consumo críticamente bajo)
        if (consumo < 1200) return 13; 

        // Prioridad 3: Según objetivo del usuario
        if (objetivoUsuario.contains("perder")) {
            if (consumo > objetivoCalorico + 600) return 1; // Exceso notable
            if (consumo > objetivoCalorico + 300) return 2; // Ligero exceso
            if (consumo > objetivoCalorico - 200 && consumo < objetivoCalorico + 200) return 3; // Objetivo cumplido
            if (consumo < objetivoCalorico - 300) return 4; // Déficit agresivo
        } else if (objetivoUsuario.contains("mantener")) {
            if (consumo > objetivoCalorico + 400) return 7;
            if (consumo > objetivoCalorico - 300 && consumo < objetivoCalorico + 300) return 5;
        } else if (objetivoUsuario.contains("ganar") || objetivoUsuario.contains("músculo")) {
            if (consumo > objetivoCalorico + 400) return 8;
            if (consumo > objetivoCalorico + 200) return 9;
            if (consumo < objetivoCalorico) return 10;
        }

        return 15; // Por defecto: hidratación
    }

    /**
     * Busca la recomendación por ID en RecomendacionesData y actualiza la UI.
     */
    private void mostrarRecomendacion(int id) {
        List<Recomendacion> todas = RecomendacionesData.getRecomendaciones();
        Recomendacion seleccionada = todas.get(14); // Por defecto hidratación
        for (Recomendacion r : todas) {
            if (r.getId() == id) {
                seleccionada = r;
                break;
            }
        }

        if (tvTituloRecomendacion != null) tvTituloRecomendacion.setText(seleccionada.getTitulo());
        if (tvTextoRecomendacion != null) tvTextoRecomendacion.setText(seleccionada.getTexto());
        if (tvEmojiIcono != null) tvEmojiIcono.setText(seleccionada.getIcono());
    }
}
