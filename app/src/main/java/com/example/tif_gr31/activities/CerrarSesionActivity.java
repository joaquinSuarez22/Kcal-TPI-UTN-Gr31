package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad de confirmación para cerrar la sesión del usuario.
 * Proporciona una interfaz sencilla para asegurar que el usuario desea salir.
 */
public class CerrarSesionActivity extends AppCompatActivity {

    private ImageView btnBack;
    private MaterialButton btnConfirmarCerrarSesion;
    private MaterialButton btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion);

        // Inicialización de componentes de la interfaz
        btnBack = findViewById(R.id.btnBack);
        btnConfirmarCerrarSesion = findViewById(R.id.btnConfirmarCerrarSesion);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configuración de la barra de navegación flotante inferior
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_perfil);

        // Listener para el botón de retroceso (flecha)
        btnBack.setOnClickListener(v -> finish());

        // Listener para el botón que ejecuta el cierre de sesión real
        btnConfirmarCerrarSesion.setOnClickListener(v -> cerrarSesion());

        // El botón cancelar simplemente cierra esta pantalla y vuelve a la anterior
        btnCancelar.setOnClickListener(v -> finish());
    }

    /**
     * Realiza la salida del usuario mediante Firebase Auth y limpia la pila de actividades.
     * Redirige al usuario a la pantalla de Login.
     */
    private void cerrarSesion() {
        // Cierra la sesión en Firebase
        FirebaseAuth.getInstance().signOut();
        
        // Prepara el Intent para ir al Login
        Intent intent = new Intent(CerrarSesionActivity.this, LoginActivity.class);
        
        // FLAG_ACTIVITY_NEW_TASK y FLAG_ACTIVITY_CLEAR_TASK eliminan todas las actividades previas
        // para que el usuario no pueda volver atrás una vez cerrada la sesión.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
