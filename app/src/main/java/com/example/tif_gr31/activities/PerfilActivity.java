package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private static final String[] SEXO_OPCIONES = {"Masculino", "Femenino", "Otro"};
    private static final String[] ACTIVIDAD_OPCIONES = {
            "Sedentario (poco o nada de ejercicio)",
            "Ligero (1-3 días a la semana)",
            "Moderado (3-5 días a la semana)",
            "Intenso (6-7 días a la semana)",
            "Muy intenso (doble sesión/entrenamiento)"
    };
    private static final String[] OBJETIVO_OPCIONES = {"Perder peso", "Mantener peso", "Ganar músculo"};

    private EditText etNombre, etEdad, etPeso, etAltura;
    private Spinner spinnerSexo, spinnerActividad, spinnerObjetivo;
    private TextView tvCaloriasFinales;
    private Button btnGuardarPerfil, btnVolverInicio, btnIrCerrarSesion;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarSpinners();
        
        // Configuración de Navegación Flotante
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_perfil);

        cargarDatosPerfil();

        btnGuardarPerfil.setOnClickListener(v -> guardarPerfil());
        btnVolverInicio.setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnIrCerrarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, CerrarSesionActivity.class);
            startActivity(intent);
        });
    }

    private void vincularVistas() {
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);
        spinnerSexo = findViewById(R.id.spinnerSexo);
        spinnerActividad = findViewById(R.id.spinnerActividad);
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);
        tvCaloriasFinales = findViewById(R.id.tvCaloriasFinales);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        btnIrCerrarSesion = findViewById(R.id.btnIrCerrarSesion);
    }

    private void configurarSpinners() {
        configurarAdapter(spinnerSexo, SEXO_OPCIONES);
        configurarAdapter(spinnerActividad, ACTIVIDAD_OPCIONES);
        configurarAdapter(spinnerObjetivo, OBJETIVO_OPCIONES);
    }

    private void configurarAdapter(Spinner spinner, String[] opciones) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void cargarDatosPerfil() {
        if (mAuth.getCurrentUser() == null) return;
        
        db.collection("usuarios").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etNombre.setText(doc.getString("nombre"));
                        Object perfilObj = doc.get("perfil");
                        if (perfilObj instanceof Map) {
                            Map<String, Object> perfil = (Map<String, Object>) perfilObj;
                            if (perfil.get("edad") != null) etEdad.setText(String.valueOf(perfil.get("edad")));
                            if (perfil.get("peso") != null) etPeso.setText(String.valueOf(perfil.get("peso")));
                            if (perfil.get("altura") != null) etAltura.setText(String.valueOf(perfil.get("altura")));
                            
                            seleccionarOpcionSpinner(spinnerSexo, perfil.get("sexo"), SEXO_OPCIONES);
                            seleccionarOpcionSpinner(spinnerActividad, perfil.get("nivelActividad"), ACTIVIDAD_OPCIONES);
                            seleccionarOpcionSpinner(spinnerObjetivo, perfil.get("objetivo"), OBJETIVO_OPCIONES);

                            double cals = 0;
                            Object calsObj = perfil.get("calorias_estimadas");
                            if (calsObj instanceof Number) {
                                cals = ((Number) calsObj).doubleValue();
                            }
                            tvCaloriasFinales.setText(String.format(Locale.getDefault(), "Tu objetivo diario: %.0f kcal", cals));
                        }
                    }
                });
    }

    private void seleccionarOpcionSpinner(Spinner spinner, Object valor, String[] opciones) {
        if (valor == null) return;
        String valStr = String.valueOf(valor);
        int index = Arrays.asList(opciones).indexOf(valStr);
        if (index >= 0) {
            spinner.setSelection(index);
        }
    }

    private void guardarPerfil() {
        if (mAuth.getCurrentUser() == null) return;

        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();

        if (nombre.isEmpty() || edadStr.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int edad = Integer.parseInt(edadStr);
        double peso = Double.parseDouble(pesoStr);
        double altura = Double.parseDouble(alturaStr);
        String sexo = spinnerSexo.getSelectedItem().toString();
        String actividad = spinnerActividad.getSelectedItem().toString();
        String objetivo = spinnerObjetivo.getSelectedItem().toString();

        // Cálculo básico de TMB (Harris-Benedict)
        double tmb;
        if (sexo.equals("Masculino")) {
            tmb = 88.362 + (13.397 * peso) + (4.799 * altura) - (5.677 * edad);
        } else {
            tmb = 447.593 + (9.247 * peso) + (3.098 * altura) - (4.330 * edad);
        }

        // Factor de actividad
        double factor = 1.2;
        if (actividad.contains("Ligero")) factor = 1.375;
        else if (actividad.contains("Moderado")) factor = 1.55;
        else if (actividad.contains("Intenso")) factor = 1.725;
        else if (actividad.contains("Muy intenso")) factor = 1.9;

        double caloriasCalculadas = tmb * factor;

        if (objetivo.equals("Perder peso")) caloriasCalculadas -= 500;
        else if (objetivo.equals("Ganar músculo")) caloriasCalculadas += 500;

        final double caloriasFinales = caloriasCalculadas;

        Map<String, Object> perfil = new HashMap<>();
        perfil.put("edad", edad);
        perfil.put("peso", peso);
        perfil.put("altura", altura);
        perfil.put("sexo", sexo);
        perfil.put("nivelActividad", actividad);
        perfil.put("objetivo", objetivo);
        perfil.put("calorias_estimadas", caloriasFinales);

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("perfil", perfil);

        db.collection("usuarios").document(mAuth.getCurrentUser().getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show();
                    tvCaloriasFinales.setText(String.format(Locale.getDefault(), "Tu objetivo diario: %.0f kcal", caloriasFinales));
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
    }
}