package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tif_gr31.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Actividad encargada de registrar nuevos usuarios en la aplicación.
 * Realiza el registro en Firebase Authentication y crea un perfil inicial en Cloud Firestore.
 */
public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicialización de servicios de Firebase (Autenticación y Base de Datos)
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias a los componentes de la interfaz de usuario
        TextInputEditText txtEmail = findViewById(R.id.emailRegister);
        TextInputEditText txtPassword = findViewById(R.id.passwordRegister);
        Button btnRegistrar = findViewById(R.id.BtnRegistrar);
        Button btnYaCuenta = findViewById(R.id.btnYaCuenta);
        ImageView btnBack = findViewById(R.id.btnBack);

        /**
         * Evento para procesar el registro del usuario.
         * Valida los datos ingresados e invoca la creación de cuenta en Firebase.
         */
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String pass = txtPassword.getText().toString().trim();

                // Validaciones básicas de entrada
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase requiere contraseñas de al menos 6 caracteres
                if (pass.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Intenta crear el usuario en Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Si la creación de cuenta es exitosa, se procede a crear el perfil en Firestore
                                String userId = mAuth.getCurrentUser().getUid();
                                crearUsuarioEnFirestore(userId, email);
                            } else {
                                // Informa si hubo un error (ej: email ya registrado)
                                Toast.makeText(RegistroActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        /**
         * Evento para regresar a la pantalla de Login.
         */
        btnYaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad de registro
            }
        });

        /**
         * Botón de retroceso (flecha en la parte superior).
         */
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * Crea un documento inicial para el usuario en la colección "usuarios" de Cloud Firestore.
     * Esto inicializa los campos de perfil que el usuario podrá completar más tarde.
     * 
     * @param userId El UID generado por Firebase Authentication.
     * @param email  El correo electrónico del usuario registrado.
     */
    private void crearUsuarioEnFirestore(String userId, String email) {
        // Objeto principal del documento del usuario
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("email", email);
        usuario.put("nombre", ""); // El nombre se completará en la pantalla de Perfil
        usuario.put("created_at", FieldValue.serverTimestamp()); // Fecha de creación del servidor

        // Objeto anidado 'perfil' con valores numéricos y de texto predeterminados
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("edad", 0);
        perfil.put("peso", 0.0);
        perfil.put("altura", 0);
        perfil.put("sexo", ""); 
        perfil.put("nivel_actividad", ""); 
        perfil.put("objetivo", ""); 
        perfil.put("calorias_estimadas", 0.0);

        // Agrega el perfil al objeto usuario
        usuario.put("perfil", perfil);

        // Guarda el documento en Firestore usando el UID como nombre del documento
        db.collection("usuarios")
                .document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistroActivity.this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();

                    // Una vez creado el perfil, redirigimos al login para que el usuario inicie sesión
                    Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistroActivity.this, "Error al crear perfil en la base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
