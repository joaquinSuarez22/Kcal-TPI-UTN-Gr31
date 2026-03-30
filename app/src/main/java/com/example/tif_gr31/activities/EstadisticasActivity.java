package com.example.tif_gr31.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class EstadisticasActivity extends AppCompatActivity {

    private static final String TAG = "EstadisticasActivity";

    private TextView tvCaloriasTotales, tvComparativa, tvPromedio, tvDiasRegistrados;
    private TextView tvProtPct, tvCarbPct, tvGrasaPct;
    private ProgressBar pbProt, pbCarb, pbGrasa;
    private TextView btnDia, btnSemana, btnMes;
    private MaterialButton btnRecomendaciones;
    private LinearLayout chartContainer, chartLabelsContainer;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private double objetivoDiario = 2000; // Valor por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);

        View mainLayout = findViewById(R.id.main);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarFiltros();

        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_estadisticas);
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        if (btnRecomendaciones != null) {
            btnRecomendaciones.setOnClickListener(v -> {
                Intent intent = new Intent(EstadisticasActivity.this, RecomendacionesActivity.class);
                startActivity(intent);
            });
        }

        // Cargar objetivo y datos iniciales (Semana por defecto)
        obtenerObjetivoUsuario();
        cargarEstadisticas(7);
    }

    private void vincularVistas() {
        tvCaloriasTotales = findViewById(R.id.tvCaloriasTotales);
        tvComparativa = findViewById(R.id.tvComparativaPeriodo);
        tvPromedio = findViewById(R.id.tvPromedioSemanal);
        tvDiasRegistrados = findViewById(R.id.tvDiasRegistrados);

        tvProtPct = findViewById(R.id.tvPorcentajeProteinas);
        tvCarbPct = findViewById(R.id.tvPorcentajeCarbos);
        tvGrasaPct = findViewById(R.id.tvPorcentajeGrasas);

        pbProt = findViewById(R.id.pbProteinas);
        pbCarb = findViewById(R.id.pbCarbos);
        pbGrasa = findViewById(R.id.pbGrasas);

        btnDia = findViewById(R.id.tvPeriodoDia);
        btnSemana = findViewById(R.id.tvPeriodoSemana);
        btnMes = findViewById(R.id.tvPeriodoMes);
        
        btnRecomendaciones = findViewById(R.id.btnRecomendaciones);
        chartContainer = findViewById(R.id.chartContainer);
        chartLabelsContainer = findViewById(R.id.chartLabelsContainer);
    }

    private void configurarFiltros() {
        if (btnDia != null) {
            btnDia.setOnClickListener(v -> {
                actualizarEstiloFiltro(btnDia);
                cargarEstadisticas(1);
            });
        }
        if (btnSemana != null) {
            btnSemana.setOnClickListener(v -> {
                actualizarEstiloFiltro(btnSemana);
                cargarEstadisticas(7);
            });
        }
        if (btnMes != null) {
            btnMes.setOnClickListener(v -> {
                actualizarEstiloFiltro(btnMes);
                cargarEstadisticas(30);
            });
        }
    }

    private void actualizarEstiloFiltro(TextView seleccionado) {
        if (btnDia != null) btnDia.setTextColor(Color.parseColor("#64748b"));
        if (btnSemana != null) btnSemana.setTextColor(Color.parseColor("#64748b"));
        if (btnMes != null) btnMes.setTextColor(Color.parseColor("#64748b"));
        if (seleccionado != null) seleccionado.setTextColor(Color.parseColor("#1c7d55"));
    }

    private void obtenerObjetivoUsuario() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("usuarios").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Object perfilObj = doc.get("perfil");
                if (perfilObj instanceof java.util.Map) {
                    java.util.Map<String, Object> perfil = (java.util.Map<String, Object>) perfilObj;
                    if (perfil.containsKey("calorias_estimadas")) {
                        Object val = perfil.get("calorias_estimadas");
                        if (val instanceof Number) {
                            objetivoDiario = ((Number) val).doubleValue();
                        }
                    }
                }
            }
        });
    }

    private void cargarEstadisticas(int rangoDias) {
        String uid = mAuth.getUid();
        if (uid == null) return;

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -(rangoDias - 1));
        Date fechaInicio = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaLimite = df.format(fechaInicio);

        db.collection("usuarios").document(uid).collection("comidas")
                .whereGreaterThanOrEqualTo("fecha", fechaLimite)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalKcal = 0, totalProt = 0, totalCarb = 0, totalGrasa = 0;
                    
                    // Mapa para agrupar calorías por día
                    Map<String, Double> calsPorDia = new TreeMap<>();
                    
                    // Inicializar el mapa con todos los días del rango (para que aparezcan barras vacías si no hay datos)
                    Calendar tempCal = Calendar.getInstance();
                    for (int i = 0; i < rangoDias; i++) {
                        String f = df.format(tempCal.getTime());
                        calsPorDia.put(f, 0.0);
                        tempCal.add(Calendar.DAY_OF_YEAR, -1);
                    }

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        double kcal = doc.getDouble("calorias") != null ? doc.getDouble("calorias") : 0;
                        totalKcal += kcal;
                        totalProt += doc.getDouble("proteinas") != null ? doc.getDouble("proteinas") : 0;
                        totalCarb += doc.getDouble("carbohidratos") != null ? doc.getDouble("carbohidratos") : 0;
                        totalGrasa += doc.getDouble("grasas") != null ? doc.getDouble("grasas") : 0;

                        String fecha = doc.getString("fecha");
                        if (fecha != null && calsPorDia.containsKey(fecha)) {
                            calsPorDia.put(fecha, calsPorDia.get(fecha) + kcal);
                        }
                    }

                    int diasRealesConDatos = 0;
                    for(Double val : calsPorDia.values()) if(val > 0) diasRealesConDatos++;

                    actualizarUI(totalKcal, totalProt, totalCarb, totalGrasa, diasRealesConDatos, rangoDias);
                    dibujarGrafico(calsPorDia, rangoDias);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error cargando stats", e));
    }

    private void dibujarGrafico(Map<String, Double> datos, int rango) {
        if (chartContainer == null) return;
        chartContainer.removeAllViews();
        if (chartLabelsContainer != null) chartLabelsContainer.removeAllViews();

        if (datos.isEmpty()) return;

        double maxCals = objetivoDiario * 1.2; // Escala máxima basada en el objetivo
        for (Double val : datos.values()) {
            if (val > maxCals) maxCals = val;
        }

        // Usamos TreeMap para que el gráfico esté ordenado por fecha
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            double cals = entry.getValue();
            
            // Crear la barra
            View bar = new View(this);
            int heightPx = (int) ((cals / maxCals) * chartContainer.getHeight());
            if (heightPx < 5 && cals > 0) heightPx = 5; // Altura mínima si hay datos

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, heightPx);
            params.weight = 1;
            params.setMargins(4, 0, 4, 0);
            bar.setLayoutParams(params);
            
            // Color según si pasó el objetivo o no
            if (cals > objetivoDiario) {
                bar.setBackgroundColor(Color.parseColor("#ef4444")); // Rojo si se pasó
            } else {
                bar.setBackgroundColor(Color.parseColor("#22c55e")); // Verde si está bien
            }
            
            chartContainer.addView(bar);

            // Agregar labels simplificados (solo para Semana y Día)
            if (rango <= 7 && chartLabelsContainer != null) {
                TextView label = new TextView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                label.setLayoutParams(lp);
                label.setTextSize(8);
                label.setGravity(Gravity.CENTER);
                label.setTextColor(Color.parseColor("#64748b"));
                
                String fecha = entry.getKey();
                label.setText(fecha.substring(fecha.length() - 2)); // Mostrar solo el día del mes
                chartLabelsContainer.addView(label);
            }
        }
    }

    private void actualizarUI(double kcal, double p, double c, double g, int cantDias, int rango) {
        if (tvCaloriasTotales != null) {
            tvCaloriasTotales.setText(String.format(Locale.getDefault(), "%,.0f", kcal));
        }

        double promedio = kcal / (rango == 1 ? 1 : Math.max(1, cantDias));
        if (tvPromedio != null) {
            tvPromedio.setText(String.format(Locale.getDefault(), "%.0f kcal", promedio));
        }
        if (tvDiasRegistrados != null) {
            tvDiasRegistrados.setText(cantDias + (cantDias == 1 ? " día" : " días"));
        }

        double objetivoPeriodo = objetivoDiario * (rango == 1 ? 1 : (cantDias > 0 ? cantDias : rango));
        if (tvComparativa != null) {
            if (kcal > objetivoPeriodo && cantDias > 0) {
                tvComparativa.setText("Sobre el objetivo");
                tvComparativa.setTextColor(Color.parseColor("#ef4444"));
            } else if (cantDias > 0) {
                tvComparativa.setText("Dentro del objetivo");
                tvComparativa.setTextColor(Color.parseColor("#22c55e"));
            } else {
                tvComparativa.setText("Sin registros");
                tvComparativa.setTextColor(Color.parseColor("#64748b"));
            }
        }

        double totalGramosMacros = p + c + g;
        if (totalGramosMacros > 0) {
            int pProt = (int) ((p / totalGramosMacros) * 100);
            int pCarb = (int) ((c / totalGramosMacros) * 100);
            int pGrasa = Math.max(0, 100 - pProt - pCarb);

            if (tvProtPct != null) tvProtPct.setText(pProt + "%");
            if (tvCarbPct != null) tvCarbPct.setText(pCarb + "%");
            if (tvGrasaPct != null) tvGrasaPct.setText(pGrasa + "%");

            if (pbProt != null) pbProt.setProgress(pProt);
            if (pbCarb != null) pbCarb.setProgress(pCarb);
            if (pbGrasa != null) pbGrasa.setProgress(pGrasa);
        } else {
            resetMacros();
        }
    }

    private void resetMacros() {
        if (tvProtPct != null) tvProtPct.setText("0%");
        if (tvCarbPct != null) tvCarbPct.setText("0%");
        if (tvGrasaPct != null) tvGrasaPct.setText("0%");
        if (pbProt != null) pbProt.setProgress(0);
        if (pbCarb != null) pbCarb.setProgress(0);
        if (pbGrasa != null) pbGrasa.setProgress(0);
    }
}
