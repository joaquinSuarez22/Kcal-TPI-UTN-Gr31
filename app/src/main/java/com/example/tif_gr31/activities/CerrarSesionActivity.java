package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;


public class CerrarSesionActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnConfirmarCerrarSesion;
    private MaterialButton btnCancelar;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        btnConfirmarCerrarSesion = findViewById(R.id.btnConfirmarCerrarSesion);
        btnCancelar = findViewById(R.id.btnCancelar);
        bottomNav = findViewById(R.id.bottomNav);

        // Set active item in bottom nav
        bottomNav.setSelectedItemId(R.id.menu_ajustes);

        // Set up click listeners
        btnBack.setOnClickListener(v -> finish());

        btnConfirmarCerrarSesion.setOnClickListener(v -> cerrarSesion());

        btnCancelar.setOnClickListener(v -> finish());

        // Bottom Navigation Logic
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_inicio) {
                startActivity(new Intent(this, InicioActivity.class));
                return true;
            } else if (id == R.id.menu_diario) {
                startActivity(new Intent(this, RegistrarComidaActivity.class));
                return true;
            } else if (id == R.id.menu_estadisticas) {
                startActivity(new Intent(this, EstadisticasActivity.class));
                return true;
            } else if (id == R.id.menu_ajustes) {
                return true;
            }
            return false;
        });
    }

    private void cerrarSesion() {
        Intent intent = new Intent(CerrarSesionActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
