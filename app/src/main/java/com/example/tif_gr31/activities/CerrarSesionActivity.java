package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.android.material.button.MaterialButton;


public class CerrarSesionActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnConfirmarCerrarSesion;
    private MaterialButton btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnConfirmarCerrarSesion = findViewById(R.id.btnConfirmarCerrarSesion);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configuración de Navegación Flotante
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_perfil);

        // Set up click listeners
        btnBack.setOnClickListener(v -> finish());

        btnConfirmarCerrarSesion.setOnClickListener(v -> cerrarSesion());

        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cerrarSesion() {
        Intent intent = new Intent(CerrarSesionActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
