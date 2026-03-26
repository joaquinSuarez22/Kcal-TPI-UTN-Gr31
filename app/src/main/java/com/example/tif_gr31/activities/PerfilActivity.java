package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView btnBack;
    private EditText etNombre, etEdad, etPeso, etAltura;
    private Spinner spinnerSexo, spinnerActividad, spinnerObjetivo;
    private TextView tvCaloriasFinales;
    private MaterialButton btnGuardarPerfil, btnVolverInicio;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a vistas
        btnBack = findViewById(R.id.btnBack);
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
        bottomNav = findViewById(R.id.bottomNav);

        // Configurar Spinners
        configurarSpinners();

        // Cargar datos actuales del perfil
        cargarDatosPerfil();

        // Botón atrás
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Botón Guardar Perfil
        btnGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPerfil();
            }
        });

        // Botón Volver a Inicio
        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Navegación inferior
        if (bottomNav != null) {
            NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.menu_perfil);
        }
    }

    /**
     * Configura los adaptadores de los Spinners
     */
    private void configurarSpinners() {
        // Spinner Sexo
        String[] sexos = {"Masculino", "Femenino", "Otro", "Prefiero no decir"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                sexos
        );
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSexo.setAdapter(adapterSexo);

        // Spinner Actividad
        String[] actividades = {"Sedentario", "Ligero", "Moderado", "Muy activo"};
        ArrayAdapter<String> adapterActividad = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                actividades
        );
        adapterActividad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActividad.setAdapter(adapterActividad);

        // Spinner Objetivo
        String[] objetivos = {"Perder peso", "Mantener peso", "Ganar masa muscular"};
        ArrayAdapter<String> adapterObjetivo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                objetivos
        );
        adapterObjetivo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObjetivo.setAdapter(adapterObjetivo);
    }

    /**
     * Carga los datos actuales del perfil desde Firestore
     */
    private void cargarDatosPerfil() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Cargar nombre
                        String nombre = documentSnapshot.getString("nombre");
                        if (nombre != null && !nombre.isEmpty()) {
                            etNombre.setText(nombre);
                        }

                        // Cargar datos del perfil
                        Map<String, Object> perfil = (Map<String, Object>) documentSnapshot.get("perfil");
                        if (perfil != null) {
                            // Edad
                            Long edad = (Long) perfil.get("edad");
                            if (edad != null && edad > 0) {
                                etEdad.setText(String.valueOf(edad));
                            }

                            // Peso
                            Double peso = (Double) perfil.get("peso");
                            if (peso != null && peso > 0) {
                                etPeso.setText(String.valueOf(peso));
                            }

                            // Altura
                            Long altura = (Long) perfil.get("altura");
                            if (altura != null && altura > 0) {
                                etAltura.setText(String.valueOf(altura));
                            }

                            // Sexo
                            String sexo = (String) perfil.get("sexo");
                            if (sexo != null && !sexo.isEmpty()) {
                                seleccionarEnSpinner(spinnerSexo, sexo);
                            }

                            // Actividad
                            String actividad = (String) perfil.get("nivel_actividad");
                            if (actividad != null && !actividad.isEmpty()) {
                                seleccionarEnSpinner(spinnerActividad, actividad);
                            }

                            // Objetivo
                            String objetivo = (String) perfil.get("objetivo");
                            if (objetivo != null && !objetivo.isEmpty()) {
                                seleccionarEnSpinner(spinnerObjetivo, objetivo);
                            }

                            // Calorías estimadas
                            Double calorias = (Double) perfil.get("calorias_estimadas");
                            if (calorias != null && calorias > 0) {
                                tvCaloriasFinales.setText("Tu objetivo diario: " + Math.round(calorias) + " kcal");
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PerfilActivity", "Error al cargar perfil", e);
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Selecciona un ítem en un Spinner según el valor guardado
     */
    private void seleccionarEnSpinner(Spinner spinner, String valor) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();

        // Normalizar el valor para comparar
        String valorNormalizado = normalizarTexto(valor);

        for (int i = 0; i < adapter.getCount(); i++) {
            String item = adapter.getItem(i).toString();
            if (normalizarTexto(item).equals(valorNormalizado)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    /**
     * Normaliza texto para comparación (minúsculas, sin espacios ni guiones)
     */
    private String normalizarTexto(String texto) {
        return texto.toLowerCase()
                .replace(" ", "")
                .replace("_", "")
                .replace("muscular", "")
                .trim();
    }

    /**
     * Guarda el perfil actualizado en Firestore
     */
    private void guardarPerfil() {
        // Obtener valores de los campos
        String nombre = etNombre.getText().toString().trim();
        String edadStr = etEdad.getText().toString().trim();
        String pesoStr = etPeso.getText().toString().trim();
        String alturaStr = etAltura.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu nombre", Toast.LENGTH_SHORT).show();
            etNombre.requestFocus();
            return;
        }

        if (edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu edad", Toast.LENGTH_SHORT).show();
            etEdad.requestFocus();
            return;
        }

        if (pesoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu peso", Toast.LENGTH_SHORT).show();
            etPeso.requestFocus();
            return;
        }

        if (alturaStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu altura", Toast.LENGTH_SHORT).show();
            etAltura.requestFocus();
            return;
        }

        // Convertir valores
        int edad = Integer.parseInt(edadStr);
        double peso = Double.parseDouble(pesoStr);
        int altura = Integer.parseInt(alturaStr);

        // Validar rangos
        if (edad < 15 || edad > 100) {
            Toast.makeText(this, "La edad debe estar entre 15 y 100 años", Toast.LENGTH_SHORT).show();
            return;
        }

        if (peso < 30 || peso > 300) {
            Toast.makeText(this, "El peso debe estar entre 30 y 300 kg", Toast.LENGTH_SHORT).show();
            return;
        }

        if (altura < 100 || altura > 250) {
            Toast.makeText(this, "La altura debe estar entre 100 y 250 cm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener valores de Spinners
        String sexo = spinnerSexo.getSelectedItem().toString().toLowerCase();
        String actividad = spinnerActividad.getSelectedItem().toString().toLowerCase();
        String objetivo = spinnerObjetivo.getSelectedItem().toString().toLowerCase().replace(" ", "_");

        // Calcular calorías de mantenimiento
        double caloriasMantenimiento = calcularCaloriasMantenimiento(edad, peso, altura, sexo, actividad);

        // Ajustar según objetivo
        double caloriasObjetivo = ajustarCaloriasPorObjetivo(caloriasMantenimiento, objetivo);

        // Preparar datos para actualizar
        Map<String, Object> datosActualizar = new HashMap<>();
        datosActualizar.put("nombre", nombre);
        datosActualizar.put("perfil.edad", edad);
        datosActualizar.put("perfil.peso", peso);
        datosActualizar.put("perfil.altura", altura);
        datosActualizar.put("perfil.sexo", sexo);
        datosActualizar.put("perfil.nivel_actividad", actividad);
        datosActualizar.put("perfil.objetivo", objetivo);
        datosActualizar.put("perfil.calorias_estimadas", caloriasObjetivo);

        // Actualizar en Firestore
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("usuarios")
                .document(userId)
                .update(datosActualizar)
                .addOnSuccessListener(aVoid -> {
                    // Actualizar UI con las calorías calculadas
                    tvCaloriasFinales.setText("Tu objetivo diario: " + Math.round(caloriasObjetivo) + " kcal");

                    Toast.makeText(PerfilActivity.this, "¡Perfil actualizado exitosamente!", Toast.LENGTH_SHORT).show();
                    Log.d("PerfilActivity", "Perfil guardado correctamente");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PerfilActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("PerfilActivity", "Error al guardar perfil", e);
                });
    }

    /**
     * Calcula las calorías de mantenimiento usando la fórmula Mifflin-St Jeor
     */
    private double calcularCaloriasMantenimiento(int edad, double peso, int altura, String sexo, String actividad) {
        double tmb;

        // Calcular TMB (Tasa Metabólica Basal) según sexo
        if (sexo.equals("masculino")) {
            tmb = (10 * peso) + (6.25 * altura) - (5 * edad) + 5;
        } else if (sexo.equals("femenino")) {
            tmb = (10 * peso) + (6.25 * altura) - (5 * edad) - 161;
        } else {
            // Promedio para otros casos
            tmb = (10 * peso) + (6.25 * altura) - (5 * edad) - 78;
        }

        // Multiplicar por factor de actividad
        double factorActividad;
        switch (actividad) {
            case "sedentario":
                factorActividad = 1.2;
                break;
            case "ligero":
                factorActividad = 1.375;
                break;
            case "moderado":
                factorActividad = 1.55;
                break;
            case "muy activo":
            case "muy_activo":
                factorActividad = 1.725;
                break;
            default:
                factorActividad = 1.2;
        }

        return tmb * factorActividad;
    }

    /**
     * Ajusta las calorías según el objetivo del usuario
     */
    private double ajustarCaloriasPorObjetivo(double caloriasMantenimiento, String objetivo) {
        switch (objetivo) {
            case "perder_peso":
                // Déficit de 300-500 kcal para perder peso de forma saludable
                return caloriasMantenimiento - 400;
            case "ganar_masa_muscular":
            case "ganar_masa":
                // Superávit de 300-500 kcal para ganar masa muscular
                return caloriasMantenimiento + 400;
            case "mantener_peso":
            default:
                return caloriasMantenimiento;
        }
    }
}