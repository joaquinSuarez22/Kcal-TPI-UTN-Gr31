package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tif_gr31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class DebugActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Button btnCargarComidas = findViewById(R.id.btnCargarComidasDebug);
        btnCargarComidas.setOnClickListener(v -> cargarComidasDePrueba());
    }

    private void cargarComidasDePrueba() {
        String userId = auth.getUid();
        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        Random random = new Random();
        String[] tipos = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snacks"};

        for (int i = 0; i < 7; i++) { // Últimos 7 días
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String fecha = String.format(Locale.US, "%d-%02d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

            // Cargar 2-3 comidas por día
            int numComidas = 2 + random.nextInt(2);
            for (int j = 0; j < numComidas; j++) {
                Map<String, Object> comidaMap = new HashMap<>();
                double kcal = 300 + random.nextInt(500);
                
                comidaMap.put("userId", userId);
                comidaMap.put("tipo", tipos[random.nextInt(tipos.length)]);
                comidaMap.put("fecha", fecha);
                comidaMap.put("timestamp", cal.getTime());
                comidaMap.put("totalKcal", kcal);
                comidaMap.put("totalProt", kcal * 0.05);
                comidaMap.put("totalCarb", kcal * 0.12);
                comidaMap.put("totalGrasa", kcal * 0.03);
                
                List<Map<String, Object>> ingredientes = new ArrayList<>();
                Map<String, Object> ing = new HashMap<>();
                ing.put("nombre", "Comida de prueba");
                ing.put("gramos", 200.0);
                ing.put("kcal", kcal);
                ingredientes.add(ing);
                comidaMap.put("ingredientes", ingredientes);

                db.collection("usuarios").document(userId).collection("comidas").add(comidaMap);
            }
        }
        Toast.makeText(this, "Comidas de prueba cargadas para los últimos 7 días", Toast.LENGTH_LONG).show();
    }
}
