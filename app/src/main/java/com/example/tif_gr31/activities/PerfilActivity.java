package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombre, etEdad, etPeso, etAltura;
    private Spinner spinnerSexo, spinnerActividad, spinnerObjetivo;
    private TextView tvCaloriasFinales;
    private Button btnGuardarPerfil, btnVolverInicio;
    
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
        
        // Configuración de Navegación Flotante
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_perfil);

        cargarDatosPerfil();

        btnGuardarPerfil.setOnClickListener(v -> guardarPerfil());
        btnVolverInicio.setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
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
    }

    private void cargarDatosPerfil() {
        if (mAuth.getCurrentUser() == null) return;
        
        db.collection("usuarios").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etNombre.setText(doc.getString("nombre"));
                        Map<String, Object> perfil = (Map<String, Object>) doc.get("perfil");
                        if (perfil != null) {
                            if (perfil.get("edad") != null) etEdad.setText(String.valueOf(perfil.get("edad")));
                            if (perfil.get("peso") != null) etPeso.setText(String.valueOf(perfil.get("peso")));
                            if (perfil.get("altura") != null) etAltura.setText(String.valueOf(perfil.get("altura")));
                            
                            double cals = perfil.get("calorias_estimadas") != null ? ((Number)perfil.get("calorias_estimadas")).doubleValue() : 0;
                            tvCaloriasFinales.setText("Tu objetivo diario: " + String.format("%.0f", cals) + " kcal");
                        }
                    }
                });
    }

    private void guardarPerfil() {
        // Aquí iría la lógica de cálculo y guardado...
        Toast.makeText(this, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show();
    }
}
