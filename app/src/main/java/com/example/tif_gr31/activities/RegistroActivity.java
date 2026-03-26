package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tif_gr31.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a la UI
        TextInputEditText txtEmail = findViewById(R.id.emailRegister);
        TextInputEditText txtPassword = findViewById(R.id.passwordRegister);
        Button btnRegistrar = findViewById(R.id.BtnRegistrar);

        // Evento para Registrar Usuario
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear usuario en Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Obtener el ID del usuario recién creado
                                String userId = mAuth.getCurrentUser().getUid();

                                // Crear documento en Firestore con estructura completa
                                crearUsuarioEnFirestore(userId, email);

                            } else {
                                Toast.makeText(RegistroActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    /**
     * Crea el documento del usuario en Firestore con estructura completa de perfil
     */
    private void crearUsuarioEnFirestore(String userId, String email) {
        // Crear objeto usuario principal
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("nombre", ""); // Vacío, lo completará después
        usuario.put("created_at", FieldValue.serverTimestamp());

        // Crear objeto perfil con valores por defecto
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("edad", 0);
        perfil.put("peso", 0.0);
        perfil.put("altura", 0);
        perfil.put("sexo", ""); // Vacío
        perfil.put("nivel_actividad", ""); // Vacío
        perfil.put("objetivo", ""); // Vacío
        perfil.put("calorias_estimadas", 0.0);

        // Agregar perfil al usuario
        usuario.put("perfil", perfil);

        // Guardar en Firestore
        db.collection("usuarios")
                .document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistroActivity.this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();

                    // Redirigir al login
                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistroActivity.this, "Error al crear perfil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
