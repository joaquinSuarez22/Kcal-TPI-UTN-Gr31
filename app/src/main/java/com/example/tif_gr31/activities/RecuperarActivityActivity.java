package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad que permite al usuario solicitar el restablecimiento de su contraseña.
 * Envía un correo electrónico a través de Firebase Authentication.
 */
public class RecuperarActivityActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuración de diseño inmersivo
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar);
        
        // Inicialización de Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ajuste de márgenes para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias a los componentes de la interfaz
        EditText txtEmail = findViewById(R.id.EmailRecuperar);
        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        ImageView btnBackHeader = findViewById(R.id.btnBackHeader);

        /**
         * Evento para enviar el correo de recuperación.
         * Valida que el campo de email no esté vacío e invoca la función de Firebase.
         */
        btnEnviar.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo para continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Envía un correo de restablecimiento de contraseña a la dirección proporcionada
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Si el envío fue exitoso, informa al usuario y cierra la actividad
                            Toast.makeText(RecuperarActivityActivity.this, 
                                "Correo enviado con éxito",
                                Toast.LENGTH_LONG).show();
                            finish(); // Regresa al Login automáticamente
                        } else {
                            // Informa el motivo del fallo (ej: correo inexistente o mal formado)
                            Toast.makeText(RecuperarActivityActivity.this, 
                                "Error: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Botón para cancelar y volver atrás
        btnVolver.setOnClickListener(v -> finish());
        
        // Flecha de retroceso en el encabezado
        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(v -> finish());
        }
    }
}
