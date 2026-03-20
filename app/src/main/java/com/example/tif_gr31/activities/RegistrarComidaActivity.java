package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class RegistrarComidaActivity extends AppCompatActivity {

    private LinearLayout btnBack;

    // Inputs del formulario
    private Spinner spinnerTipoComida;
    private EditText etDescripcion, etCantidad, etCalorias, etFechaHora;

    // Botones de acción
    private MaterialButton btnGuardarComida, btnCancelar;

    // Menú de navegación inferior
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_comida);

        // Instanciar la cabecera (Botón Volver tipo estilo iOS)
        btnBack = findViewById(R.id.btnBack);

        // Instanciar campos
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCantidad = findViewById(R.id.etCantidad);
        etCalorias = findViewById(R.id.etCalorias);
        etFechaHora = findViewById(R.id.etFechaHora);

        // Instanciar Botones
        btnGuardarComida = findViewById(R.id.btnGuardarComida);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Menú
        bottomNav = findViewById(R.id.bottomNav);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGuardarComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para capturar y guardar la comida usando los EditText...
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Descartar/Volver atrás
                finish();
            }
        });

    }
}
