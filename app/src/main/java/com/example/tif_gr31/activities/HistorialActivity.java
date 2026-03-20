package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView BtnVolver = findViewById(R.id.BtnVolverHistorial);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Set active item
        bottomNav.setSelectedItemId(R.id.menu_diario); // Or the corresponding id

        BtnVolver.setOnClickListener(v -> finish());

        // Bottom Navigation Logic
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_inicio) {
                startActivity(new Intent(this, InicioActivity.class));
                return true;
            } else if (id == R.id.menu_diario) {
                // Already here or navigate to daily
                return true;
            } else if (id == R.id.menu_estadisticas) {
                startActivity(new Intent(this, EstadisticasActivity.class));
                return true;
            } else if (id == R.id.menu_ajustes) {
                startActivity(new Intent(this, CerrarSesionActivity.class));
                return true;
            }
            return false;
        });
    }
}
