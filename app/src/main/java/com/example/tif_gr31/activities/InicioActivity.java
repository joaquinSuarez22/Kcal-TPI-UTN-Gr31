package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.android.material.card.MaterialCardView;

public class InicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// Referencias a los botones del layout
        Button BtnRegistrarComida = findViewById(R.id.BtnRegisComida);
        MaterialCardView BtnEstadisticas = findViewById(R.id.BtnEstadisticas);
        MaterialCardView BtnRecomendaciones = findViewById(R.id.BtnRecomendaciones);
        MaterialCardView BtnHistorial = findViewById(R.id.BtnHistorial);
        MaterialCardView BtnPerfil = findViewById(R.id.BtnPerfil);

        /// Navegacion a pantalla Registrar comida
        BtnRegistrarComida.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RegistrarComidaActivity.class);
            startActivity(intent);
        });

        /// Navegacion a pantalla Estadisticas
        BtnEstadisticas.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, EstadisticasActivity.class);
            startActivity(intent);
        });

        /// Navegacion a pantalla Recomendaciones
        BtnRecomendaciones.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, RecomendacionesActivity.class);
            startActivity(intent);
        });

        /// Navegacion a pantalla Historial
        BtnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, HistorialActivity.class);
            startActivity(intent);
        });

        /// Navegacion a pantalla Perfil
        BtnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
    }
}
