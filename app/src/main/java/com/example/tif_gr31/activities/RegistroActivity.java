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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.Value;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

   private FirebaseFirestore db= FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /// botones
        Button BtnConfirmar=findViewById(R.id.BtnRegistrar);
        Button BtnRegresar=findViewById(R.id.btnYaCuenta);
        EditText txtEmail=findViewById(R.id.emailRegister);
        EditText txtPassword=findViewById(R.id.passwordRegister);
        EditText txtRepPass=findViewById(R.id.repeatPassword);


        /// evento para ir al menu
        BtnConfirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String pass = txtPassword.getText().toString();
                String repetir = txtRepPass.getText().toString();

                Map<String, Object> usuario = new HashMap<>();
                usuario.put("email", email);
                usuario.put("password", pass);

                // Guardar en Firestore
                db.collection("usuarios")
                        .add(usuario)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(RegistroActivity.this, "Usuario guardado ", Toast.LENGTH_SHORT).show();
                        });


            }
        });
        /// evento clcik para volver al login
        BtnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}