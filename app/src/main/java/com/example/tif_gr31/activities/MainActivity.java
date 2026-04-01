package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad inicial de la aplicación.
 * Su función principal es actuar como un "splash screen" o punto de entrada
 * que redirige al usuario a la pantalla de Inicio si ya está autenticado,
 * o a la pantalla de Login si no lo está.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Habilita el diseño de borde a borde (Edge-to-Edge)
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Ajusta el padding de la vista para que no se superponga con las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Verificamos el estado de la sesión con Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // Si el usuario ya está logueado, vamos directo al Inicio
            startActivity(new Intent(MainActivity.this, InicioActivity.class));
        } else {
            // Si no, vamos a la pantalla de Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        
        // Finalizamos esta actividad para que el usuario no pueda volver a ella con el botón "atrás"
        finish();
    }
}
