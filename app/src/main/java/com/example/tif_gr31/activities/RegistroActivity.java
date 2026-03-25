package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /// guardamos botones y campos
        Button BtnConfirmar = findViewById(R.id.BtnRegistrar);
        Button BtnRegresar = findViewById(R.id.btnYaCuenta);
        EditText txtEmail = findViewById(R.id.emailRegister);
        EditText txtPassword = findViewById(R.id.passwordRegister);
        EditText txtRepPass = findViewById(R.id.repeatPassword);

        BtnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();
                String repetir = txtRepPass.getText().toString().trim();

                // Validaciones básicas
                if (email.isEmpty() || pass.isEmpty() || repetir.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass.equals(repetir)) {
                    Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Registro en Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(RegistroActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // 2. Si Auth es exitoso, guardamos el perfil en Firestore
                                String userId = mAuth.getCurrentUser().getUid();
                                Map<String, Object> usuario = new HashMap<>();
                                usuario.put("email", email);
                                usuario.put("id", userId);

                                db.collection("usuarios").document(userId)
                                        .set(usuario)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(RegistroActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                            // Redirigir al Login o cerrar actividad
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(RegistroActivity.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Error en la creación de la cuenta (ej. email ya usado)
                                Toast.makeText(RegistroActivity.this, "Error de registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        /// evento click para volver al login
        BtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }
}