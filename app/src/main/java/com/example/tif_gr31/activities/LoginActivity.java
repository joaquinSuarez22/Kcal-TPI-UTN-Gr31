package com.example.tif_gr31.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /// botones
        Button BtnRegistrar=findViewById(R.id.BtnRegistrar);
        Button BtnMenu=findViewById(R.id.loginBtn);
        Button btnTest= findViewById(R.id.loginBtn);

        ///  el evento click por si no esta registrado
        BtnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);

                finish();
            }
        });
        btnTest.setOnClickListener(v -> {

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword("a@a.com", "123456")
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(this, "OK FIREBASE", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

        });

        /// evento para ir al menu
      BtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginActivity.this, InicioActivity.class);
                startActivity(intent);
            }
        });



    }
}