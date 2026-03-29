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

public class RecomendacionesActivity extends AppCompatActivity {

    private static final String TAG = "RecomendacionesAct";
    
    private ImageView btnBack;
    private TextView tvEmojiIcono, tvTituloRecomendacion, tvTextoRecomendacion, tvConsumoPromedio;
    private MaterialButton btnVolverInicio;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
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
        
        // Configuración de Navegación Flotante
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_estadisticas);

        btnBack.setOnClickListener(v -> finish());
        btnVolverInicio.setOnClickListener(v -> finish());

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

    private void cargarDatosYCalcular() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();

        // 1. Obtener datos del usuario (Objetivo)
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> perfil = (Map<String, Object>) doc.get("perfil");
                        if (perfil != null) {
                            objetivoUsuario = String.valueOf(perfil.get("objetivo")).toLowerCase().replace(" ", "_");
                            if (perfil.get("calorias_estimadas") != null) {
                                objetivoCalorico = ((Number) perfil.get("calorias_estimadas")).doubleValue();
                            }
                        }
                        obtenerRegistrosUltimaSemana(uid);
                    }
                });
    }

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
                        double kcal = doc.getDouble("totalKcal") != null ? doc.getDouble("totalKcal") : 0;
                        
                        if (fecha != null) {
                            Double actual = caloriasPorDia.get(fecha);
                            caloriasPorDia.put(fecha, (actual != null ? actual : 0) + kcal);
                        }
                    }
                    procesarRecomendacion();
                });
    }

    private void procesarRecomendacion() {
        int diasConRegistros = caloriasPorDia.size();
        double sumaTotal = 0;
        for (double val : caloriasPorDia.values()) sumaTotal += val;
        double promedio = diasConRegistros > 0 ? sumaTotal / diasConRegistros : 0;

        if (tvConsumoPromedio != null) {
            tvConsumoPromedio.setText(String.format(Locale.getDefault(), "Tu promedio: %.0f kcal/día (%d días registrados)", promedio, diasConRegistros));
        }

        int idRecomendacion = seleccionarIdRecomendacion(promedio, diasConRegistros);
        mostrarRecomendacion(idRecomendacion);
    }

    private int seleccionarIdRecomendacion(double consumo, int dias) {
        // Lógica de selección según el documento
        if (dias < 3) return 12; // Primer uso
        if (dias < 5) return 11; // Falta registros
        if (consumo < 1200) return 13; // Muy bajo

        if (objetivoUsuario.contains("perder")) {
            if (consumo > objetivoCalorico + 600) return 1;
            if (consumo > objetivoCalorico + 300) return 2;
            if (consumo > objetivoCalorico - 200 && consumo < objetivoCalorico + 200) return 3;
            if (consumo < objetivoCalorico - 300) return 4;
        } else if (objetivoUsuario.contains("mantener")) {
            if (consumo > objetivoCalorico + 400) return 7;
            if (consumo > objetivoCalorico - 300 && consumo < objetivoCalorico + 300) return 5;
        } else if (objetivoUsuario.contains("ganar") || objetivoUsuario.contains("músculo")) {
            if (consumo > objetivoCalorico + 400) return 8;
            if (consumo > objetivoCalorico + 200) return 9;
            if (consumo < objetivoCalorico) return 10;
        }

        return 15; // Hidratación por defecto
    }

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
