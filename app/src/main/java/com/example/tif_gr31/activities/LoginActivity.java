package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Actividad encargada del inicio de sesión de los usuarios.
 * Utiliza Firebase Authentication para validar las credenciales.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Habilita el diseño inmersivo
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configuración de los insets para respetar las barras del sistema (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vinculación de vistas
        EditText txtEmail = findViewById(R.id.email);
        EditText txtPassword = findViewById(R.id.password);
        Button BtnLogin = findViewById(R.id.loginBtn);
        Button BtnRegistrar = findViewById(R.id.BtnRegistrar);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        /**
         * Evento de clic para el botón de inicio de sesión.
         * Valida que los campos no estén vacíos e intenta autenticar con Firebase.
         */
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();

                // Validación simple de campos
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Ingresa email y contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Intento de autenticación con Firebase
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Login exitoso, redirigir al Inicio
                                Toast.makeText(LoginActivity.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                                startActivity(intent);
                                finish(); // Finaliza LoginActivity para evitar volver atrás
                            } else {
                                // Login fallido, mostrar error al usuario
                                Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        /**
         * Redirige a la pantalla de Registro si el usuario no tiene cuenta.
         */
        BtnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        /**
         * Redirige a la pantalla de recuperación de contraseña.
         */
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecuperarActivityActivity.class);
            startActivity(intent);
        });
    }
}
