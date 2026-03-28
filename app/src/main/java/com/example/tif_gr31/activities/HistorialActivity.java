package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tif_gr31.R;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistorialActivity extends AppCompatActivity {

    private static final String TAG = "HistorialActivity";
    private RecyclerView rvHistorial;
    private LinearLayout layoutVacio;
    private HistorialAdapter adapter;
    private final List<Map<String, Object>> listaComidas = new ArrayList<>();
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvHistorial = findViewById(R.id.rvHistorial);
        layoutVacio = findViewById(R.id.layoutVacio);
        
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistorialAdapter(listaComidas);
        rvHistorial.setAdapter(adapter);

        findViewById(R.id.BtnVolverHistorial).setOnClickListener(v -> finish());

        // Configuración de Navegación Flotante
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_historial);

        cargarHistorial();
    }

    private void cargarHistorial() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        // Se usa la colección dentro del documento del usuario
        db.collection("usuarios").document(uid).collection("comidas")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaComidas.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            listaComidas.add(data);
                        }
                    }

                    if (listaComidas.isEmpty()) {
                        rvHistorial.setVisibility(View.GONE);
                        layoutVacio.setVisibility(View.VISIBLE);
                    } else {
                        rvHistorial.setVisibility(View.VISIBLE);
                        layoutVacio.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar historial", e);
                });
    }

    private static class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
        private final List<Map<String, Object>> lista;

        HistorialAdapter(List<Map<String, Object>> lista) {
            this.lista = lista;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_comida, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> comida = lista.get(position);

            // Mapeo corregido de nombres de campos según RegistrarComidaActivity
            String tipo = (String) comida.get("tipo");
            holder.tvTipo.setText(tipo != null ? tipo : "Comida");
            
            holder.tvFecha.setText((String) comida.get("fecha"));
            
            List<Map<String, Object>> ingredientes = (List<Map<String, Object>>) comida.get("ingredientes");
            if (ingredientes != null && !ingredientes.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ingredientes.size(); i++) {
                    sb.append(ingredientes.get(i).get("nombre"));
                    if (i < ingredientes.size() - 1) sb.append(", ");
                }
                holder.tvDetalle.setText(sb.toString());
            } else {
                holder.tvDetalle.setText("Sin ingredientes detallados");
            }

            double cals = comida.get("totalKcal") != null ? ((Number) comida.get("totalKcal")).doubleValue() : 0;
            double p = comida.get("totalProt") != null ? ((Number) comida.get("totalProt")).doubleValue() : 0;
            double c = comida.get("totalCarb") != null ? ((Number) comida.get("totalCarb")).doubleValue() : 0;
            double g = comida.get("totalGrasa") != null ? ((Number) comida.get("totalGrasa")).doubleValue() : 0;

            holder.tvCals.setText(String.format("%.0f kcal", cals));
            holder.tvMacros.setText(String.format("P: %.1fg | C: %.1fg | G: %.1fg", p, c, g));
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTipo, tvFecha, tvDetalle, tvCals, tvMacros;

            ViewHolder(View itemView) {
                super(itemView);
                tvTipo = itemView.findViewById(R.id.tvTipoComida);
                tvFecha = itemView.findViewById(R.id.tvFechaComida);
                tvDetalle = itemView.findViewById(R.id.tvDetalleIngredientes);
                tvCals = itemView.findViewById(R.id.tvCaloriasHistorial);
                tvMacros = itemView.findViewById(R.id.tvMacrosHistorial);
            }
        }
    }
}
