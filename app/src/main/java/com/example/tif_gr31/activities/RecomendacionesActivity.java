package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class RecomendacionesActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnVolverInicio;

    // Bottom Nav
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones);

        // Header bind
        btnBack = findViewById(R.id.btnBack);

        // Buttons
        btnVolverInicio = findViewById(R.id.btnVolverInicio);

        // Bottom Nav bar
        bottomNav = findViewById(R.id.bottomNav);

        // OnClick Listeners
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnVolverInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para regresar al Inicio principal
                // finish();
            }
        });

    }
}
