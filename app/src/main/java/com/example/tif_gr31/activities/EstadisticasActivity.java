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

/**
 * Actividad que gestiona la visualización de estadísticas de consumo del usuario.
 * Analiza los datos de Firestore para generar gráficos de barras y resúmenes nutricionales
 * desglosados por día, semana y mes.
 */
public class EstadisticasActivity extends AppCompatActivity {

    private static final String TAG = "EstadisticasActivity";

    // Componentes de la interfaz de usuario para mostrar resúmenes calóricos
    private TextView tvCaloriasTotales, tvComparativa, tvPromedio, tvDiasRegistrados;
    
    // Componentes para la distribución de macronutrientes (porcentajes y barras)
    private TextView tvProtPct, tvCarbPct, tvGrasaPct;
    private ProgressBar pbProt, pbCarb, pbGrasa;
    
    // Selectores de periodo (Día, Semana, Mes)
    private TextView btnDia, btnSemana, btnMes;
    private MaterialButton btnRecomendaciones;
    
    // Contenedores para el gráfico de barras dinámico
    private LinearLayout chartContainer, chartLabelsContainer;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Objetivo calórico diario recuperado del perfil (2000 kcal por defecto)
    private double objetivoDiario = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita diseño de pantalla completa
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);

        // Ajuste de paddings para respetar las barras del sistema
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

        // Configura la barra de navegación flotante inferior
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_estadisticas);
        
        // Listener para el botón de retroceso
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // Botón para acceder a la pantalla de recomendaciones personalizadas
        if (btnRecomendaciones != null) {
            btnRecomendaciones.setOnClickListener(v -> {
                Intent intent = new Intent(EstadisticasActivity.this, RecomendacionesActivity.class);
                startActivity(intent);
            });
        }

        // Carga el objetivo del usuario y muestra los datos de la última semana por defecto
        obtenerObjetivoUsuario();
        cargarEstadisticas(7);
    }

    /**
     * Vincula las variables con los elementos definidos en el XML.
     */
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

    /**
     * Configura los eventos de clic para cambiar entre los periodos de visualización.
     */
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

    /**
     * Cambia el color del texto de los filtros para indicar cuál está activo.
     */
    private void actualizarEstiloFiltro(TextView seleccionado) {
        if (btnDia != null) btnDia.setTextColor(Color.parseColor("#64748b"));
        if (btnSemana != null) btnSemana.setTextColor(Color.parseColor("#64748b"));
        if (btnMes != null) btnMes.setTextColor(Color.parseColor("#64748b"));
        if (seleccionado != null) seleccionado.setTextColor(Color.parseColor("#1c7d55"));
    }

    /**
     * Recupera el objetivo calórico diario del usuario desde Firestore.
     */
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

    /**
     * Consulta las comidas en Firestore dentro del rango de días especificado.
     * Agrupa los datos por fecha y calcula los totales de macronutrientes.
     * 
     * @param rangoDias Número de días hacia atrás a consultar (1, 7 o 30).
     */
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
                    
                    // Mapa para agrupar calorías por día, ordenado por fecha (TreeMap)
                    Map<String, Double> calsPorDia = new TreeMap<>();
                    
                    // Inicializar el mapa con todos los días del rango para asegurar barras vacías
                    Calendar tempCal = Calendar.getInstance();
                    for (int i = 0; i < rangoDias; i++) {
                        String f = df.format(tempCal.getTime());
                        calsPorDia.put(f, 0.0);
                        tempCal.add(Calendar.DAY_OF_YEAR, -1);
                    }

                    // Procesa cada documento (comida) devuelto por la consulta
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

                    // Contador de días reales en los que hubo ingesta
                    int diasRealesConDatos = 0;
                    for(Double val : calsPorDia.values()) if(val > 0) diasRealesConDatos++;

                    actualizarUI(totalKcal, totalProt, totalCarb, totalGrasa, diasRealesConDatos, rangoDias);
                    dibujarGrafico(calsPorDia, rangoDias);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error cargando estadísticas", e));
    }

    /**
     * Genera dinámicamente un gráfico de barras simple usando vistas de Android.
     * Ajusta la altura de las barras según el consumo y cambia su color si exceden el objetivo.
     */
    private void dibujarGrafico(Map<String, Double> datos, int rango) {
        if (chartContainer == null) return;
        chartContainer.removeAllViews(); // Limpia gráfico anterior
        if (chartLabelsContainer != null) chartLabelsContainer.removeAllViews();

        if (datos.isEmpty()) return;

        // Determina la escala máxima del gráfico
        double maxCals = objetivoDiario * 1.2; 
        for (Double val : datos.values()) {
            if (val > maxCals) maxCals = val;
        }

        // Itera sobre los datos ordenados para crear las barras
        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            double cals = entry.getValue();
            
            // Creación visual de la barra
            View bar = new View(this);
            int heightPx = (int) ((cals / maxCals) * chartContainer.getHeight());
            if (heightPx < 5 && cals > 0) heightPx = 5; // Altura mínima de visibilidad

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, heightPx);
            params.weight = 1; // Distribución equitativa del ancho
            params.setMargins(4, 0, 4, 0);
            bar.setLayoutParams(params);
            
            // Lógica de color: Verde (Bien), Rojo (Exceso)
            if (cals > objetivoDiario) {
                bar.setBackgroundColor(Color.parseColor("#ef4444"));
            } else {
                bar.setBackgroundColor(Color.parseColor("#22c55e"));
            }
            
            chartContainer.addView(bar);

            // Añade etiquetas de texto debajo de las barras (solo para periodos cortos)
            if (rango <= 7 && chartLabelsContainer != null) {
                TextView label = new TextView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                label.setLayoutParams(lp);
                label.setTextSize(8);
                label.setGravity(Gravity.CENTER);
                label.setTextColor(Color.parseColor("#64748b"));
                
                String fecha = entry.getKey();
                label.setText(fecha.substring(fecha.length() - 2)); // Solo el número del día
                chartLabelsContainer.addView(label);
            }
        }
    }

    /**
     * Actualiza los indicadores textuales y las barras de progreso de macronutrientes.
     */
    private void actualizarUI(double kcal, double p, double c, double g, int cantDias, int rango) {
        if (tvCaloriasTotales != null) {
            tvCaloriasTotales.setText(String.format(Locale.getDefault(), "%,.0f", kcal));
        }

        // Cálculo del promedio (evita división por cero)
        double promedio = kcal / (rango == 1 ? 1 : Math.max(1, cantDias));
        if (tvPromedio != null) {
            tvPromedio.setText(String.format(Locale.getDefault(), "%.0f kcal", promedio));
        }
        if (tvDiasRegistrados != null) {
            tvDiasRegistrados.setText(cantDias + (cantDias == 1 ? " día" : " días"));
        }

        // Determina si el usuario cumplió el objetivo en el periodo
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

        // Cálculo de distribución porcentual de macros (basado en gramos totales)
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

    /**
     * Limpia los indicadores de macronutrientes si no hay datos.
     */
    private void resetMacros() {
        if (tvProtPct != null) tvProtPct.setText("0%");
        if (tvCarbPct != null) tvCarbPct.setText("0%");
        if (tvGrasaPct != null) tvGrasaPct.setText("0%");
        if (pbProt != null) pbProt.setProgress(0);
        if (pbCarb != null) pbCarb.setProgress(0);
        if (pbGrasa != null) pbGrasa.setProgress(0);
    }
}
