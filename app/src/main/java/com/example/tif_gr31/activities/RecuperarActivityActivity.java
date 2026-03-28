package com.example.tif_gr31.activities;

import android.os.Bundle;
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

public class RecuperarActivityActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar);
        
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText txtEmail = findViewById(R.id.EmailRecuperar);
        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo para continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Envia un correo para recuperar la contra
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RecuperarActivityActivity.this, 
                                "Correo enviado con exito",
                                Toast.LENGTH_LONG).show();
                            finish(); // Volvemos al Login automáticamente
                        } else {
                            Toast.makeText(RecuperarActivityActivity.this, 
                                "Error: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnVolver.setOnClickListener(v -> finish());
    }
}
