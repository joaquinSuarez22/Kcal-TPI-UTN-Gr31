package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class PerfilActivity extends AppCompatActivity {

    private ImageView btnBack;

    // UI elements mapping
    private EditText etNombre, etEdad, etPeso, etAltura;
    private Spinner spinnerSexo, spinnerActividad, spinnerObjetivo;
    private TextView tvCaloriasFinales;
    private MaterialButton btnGuardarPerfil, btnVolverInicio;

    // Bottom Nav
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Header bind
        btnBack = findViewById(R.id.btnBack);

        // Input binds
        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        etPeso = findViewById(R.id.etPeso);
        etAltura = findViewById(R.id.etAltura);

        // Spinners binds
        spinnerSexo = findViewById(R.id.spinnerSexo);
        spinnerActividad = findViewById(R.id.spinnerActividad);
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);

        // Output and Buttons
        tvCaloriasFinales = findViewById(R.id.tvCaloriasFinales);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);
        bottomNav = findViewById(R.id.bottomNav);

        // OnClick Listeners
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para guardar perfil
            }
        });

        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para regresar al Inicio principal
                // finish();
            }
        });

        // Configuración adicional: poblar Spinners con Arrays de res/values/strings.xml
        // ArrayAdapter<CharSequence> adapterSexo = ArrayAdapter.createFromResource(this, R.array.sexo_options, android.R.layout.simple_spinner_item);
        // ...
    }
}
