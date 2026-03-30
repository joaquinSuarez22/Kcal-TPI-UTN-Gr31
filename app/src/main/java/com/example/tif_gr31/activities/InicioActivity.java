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

public class InicioActivity extends AppCompatActivity {

    private static final String TAG = "InicioActivity";
    
    private TextView txtCaloriasRestantesCentro, txtObjetivoTotalCalorias;
    private CircularProgressIndicator progresoCaloriasCircular;
    
    private TextView txtCarbosGramos, txtProteinasGramos, txtGrasasGramos;
    private CircularProgressIndicator progresoCarbos, progresoProteinas, progresoGrasas;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    
    private double objetivoCaloricoTotal = 0;
    private double calsConsumidas = 0, carbosConsumidos = 0, protConsumidas = 0, grasasConsumidas = 0;

    private final String[] categorias = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snacks"};
    private final String[] emojis = {"🥐", "🍝", "☕", "🍲", "🍎"};
    private final Map<String, Double> consumoPorCategoria = new HashMap<>();
    private final Map<String, View> mealViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

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

        for (String cat : categorias) {
            consumoPorCategoria.put(cat, 0.0);
        }

        vincularVistas();
        setupMealItems();

        // Configuración de Navegación Flotante
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_inicio);

        MaterialButton btnDebug = findViewById(R.id.btnDebug);
        if (btnDebug != null) {
            btnDebug.setOnClickListener(v -> {
                Intent intent = new Intent(InicioActivity.this, DebugActivity.class);
                startActivity(intent);
            });
        }

        cargarDatosResumen();
    }

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

        mealViews.put("Desayuno", findViewById(R.id.itemDesayuno));
        mealViews.put("Almuerzo", findViewById(R.id.itemAlmuerzo));
        mealViews.put("Merienda", findViewById(R.id.itemMerienda));
        mealViews.put("Cena", findViewById(R.id.itemCena));
        mealViews.put("Snacks", findViewById(R.id.itemSnacks));
    }

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
                    // CONFIGURADO: El botón "+" redirige a RegistrarComidaActivity con la categoría
                    btnAdd.setOnClickListener(v -> {
                        Intent intent = new Intent(InicioActivity.this, RegistrarComidaActivity.class);
                        intent.putExtra("CATEGORIA_SELECCIONADA", cat);
                        startActivity(intent);
                    });
                }
            }
        }
    }

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
                        obtenerConsumoDelDia(user.getUid());
                    }
                });
    }

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
                        
                        String cat = doc.getString("categoria");
                        if (cat != null && consumoPorCategoria.containsKey(cat)) {
                            Double valorActual = consumoPorCategoria.get(cat);
                            consumoPorCategoria.put(cat, (valorActual != null ? valorActual : 0) + (doc.getDouble("calorias") != null ? doc.getDouble("calorias") : 0));
                        }
                    }
                    actualizarUI();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error obteniendo comidas: ", e));
    }

    private void resetearTotales() {
        calsConsumidas = 0; carbosConsumidos = 0; protConsumidas = 0; grasasConsumidas = 0;
        for (String cat : categorias) consumoPorCategoria.put(cat, 0.0);
    }

    private void actualizarUI() {
        double restantes = Math.max(0, objetivoCaloricoTotal - calsConsumidas);
        if (txtCaloriasRestantesCentro != null) {
            txtCaloriasRestantesCentro.setText(String.format(Locale.US, "%.0f", restantes));
        }
        if (txtObjetivoTotalCalorias != null) {
            txtObjetivoTotalCalorias.setText(String.format(Locale.US, "Objetivo: %.0f kcal", objetivoCaloricoTotal));
        }
        
        if (objetivoCaloricoTotal > 0 && progresoCaloriasCircular != null) {
            int progreso = (int) ((calsConsumidas / objetivoCaloricoTotal) * 100);
            progresoCaloriasCircular.setProgress(Math.min(progreso, 100));
        }

        double objCarbos = (objetivoCaloricoTotal * 0.5) / 4;
        double objProt = (objetivoCaloricoTotal * 0.2) / 4;
        double objGrasa = (objetivoCaloricoTotal * 0.3) / 9;

        actualizarMacroCircle(progresoCarbos, txtCarbosGramos, carbosConsumidos, objCarbos);
        actualizarMacroCircle(progresoProteinas, txtProteinasGramos, protConsumidas, objProt);
        actualizarMacroCircle(progresoGrasas, txtGrasasGramos, grasasConsumidas, objGrasa);

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

    private void actualizarMacroCircle(CircularProgressIndicator cp, TextView tv, double valor, double objetivo) {
        if (tv != null) tv.setText(String.format(Locale.US, "%.0fg", valor));
        if (objetivo > 0 && cp != null) {
            int prog = (int) ((valor / objetivo) * 100);
            cp.setProgress(Math.min(prog, 100));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosResumen();
    }
}
