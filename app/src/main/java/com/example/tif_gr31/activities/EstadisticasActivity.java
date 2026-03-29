package com.example.tif_gr31.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {

    private static final String TAG = "EstadisticasActivity";
    
    private TextView tvCaloriasTotales, tvComparativa, tvPromedio, tvDiasRegistrados;
    private TextView tvProtPct, tvCarbPct, tvGrasaPct;
    private ProgressBar pbProt, pbCarb, pbGrasa;
    private TextView btnDia, btnSemana, btnMes;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    
    private double objetivoDiario = 2000; // Valor por defecto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarFiltros();
        
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_estadisticas);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

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
    }

    private void configurarFiltros() {
        btnDia.setOnClickListener(v -> {
            actualizarEstiloFiltro(btnDia);
            cargarEstadisticas(1);
        });
        btnSemana.setOnClickListener(v -> {
            actualizarEstiloFiltro(btnSemana);
            cargarEstadisticas(7);
        });
        btnMes.setOnClickListener(v -> {
            actualizarEstiloFiltro(btnMes);
            cargarEstadisticas(30);
        });
    }

    private void actualizarEstiloFiltro(TextView seleccionado) {
        btnDia.setTextColor(Color.parseColor("#64748b"));
        btnSemana.setTextColor(Color.parseColor("#64748b"));
        btnMes.setTextColor(Color.parseColor("#64748b"));
        seleccionado.setTextColor(Color.parseColor("#1c7d55"));
    }

    private void obtenerObjetivoUsuario() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("usuarios").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("perfil.calorias_estimadas")) {
                objetivoDiario = doc.getDouble("perfil.calorias_estimadas");
            }
        });
    }

    private void cargarEstadisticas(int dias) {
        String uid = mAuth.getUid();
        if (uid == null) return;

        // Calculamos la fecha de inicio del rango
        Calendar cal = Calendar.getInstance();
        Date fin = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -(dias - 1));
        Date inicio = cal.getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaLimite = df.format(inicio);

        db.collection("usuarios").document(uid).collection("comidas")
                .whereGreaterThanOrEqualTo("fecha", fechaLimite)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalKcal = 0, totalProt = 0, totalCarb = 0, totalGrasa = 0;
                    int registros = 0;
                    
                    // Usamos un Set o un contador simple para días con registros
                    java.util.HashSet<String> diasUnicos = new java.util.HashSet<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        totalKcal += safeGetDouble(doc, "calorias");
                        totalProt += safeGetDouble(doc, "proteinas");
                        totalCarb += safeGetDouble(doc, "carbohidratos");
                        totalGrasa += safeGetDouble(doc, "grasas");
                        
                        String fecha = doc.getString("fecha");
                        if (fecha != null) diasUnicos.add(fecha);
                        registros++;
                    }

                    actualizarUI(totalKcal, totalProt, totalCarb, totalGrasa, diasUnicos.size(), dias);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error cargando stats", e));
    }

    private double safeGetDouble(DocumentSnapshot doc, String field) {
        return doc.getDouble(field) != null ? doc.getDouble(field) : 0;
    }

    private void actualizarUI(double kcal, double p, double c, double g, int cantDias, int rango) {
        tvCaloriasTotales.setText(String.format(Locale.getDefault(), "%,.0f", kcal));
        
        double promedio = kcal / (rango == 1 ? 1 : Math.max(1, cantDias));
        tvPromedio.setText(String.format(Locale.getDefault(), "%.0f kcal", promedio));
        tvDiasRegistrados.setText(cantDias + (cantDias == 1 ? " día" : " días"));

        // Comparativa simple con el objetivo
        double objetivoPeriodo = objetivoDiario * rango;
        if (kcal > objetivoPeriodo) {
            tvComparativa.setText("Sobre el objetivo");
            tvComparativa.setTextColor(Color.parseColor("#ef4444"));
        } else {
            tvComparativa.setText("Dentro del objetivo");
            tvComparativa.setTextColor(Color.parseColor("#22c55e"));
        }

        // Macronutrientes %
        double totalMacros = p + c + g;
        if (totalMacros > 0) {
            int pProt = (int) ((p / totalMacros) * 100);
            int pCarb = (int) ((c / totalMacros) * 100);
            int pGrasa = 100 - pProt - pCarb;

            tvProtPct.setText(pProt + "%");
            tvCarbPct.setText(pCarb + "%");
            tvGrasaPct.setText(pGrasa + "%");

            pbProt.setProgress(pProt);
            pbCarb.setProgress(pCarb);
            pbGrasa.setProgress(pGrasa);
        } else {
            resetMacros();
        }
    }

    private void resetMacros() {
        tvProtPct.setText("0%");
        tvCarbPct.setText("0%");
        tvGrasaPct.setText("0%");
        pbProt.setProgress(0);
        pbCarb.setProgress(0);
        pbGrasa.setProgress(0);
    }
}
