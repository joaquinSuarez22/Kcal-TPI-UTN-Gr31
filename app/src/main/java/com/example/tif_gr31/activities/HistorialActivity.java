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

/**
 * Actividad que muestra el historial completo de comidas registradas por el usuario.
 * Utiliza un RecyclerView para listar los registros recuperados de Cloud Firestore.
 */
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
        
        // Configuración de diseño inmersivo
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        // Ajuste de márgenes para las barras del sistema
        View mainLayout = findViewById(R.id.main);
        if (mainLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Referencias a la UI
        rvHistorial = findViewById(R.id.rvHistorial);
        layoutVacio = findViewById(R.id.layoutVacio); // Se muestra si no hay datos
        
        // Configuración del RecyclerView con un LinearLayoutManager vertical
        rvHistorial.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistorialAdapter(listaComidas);
        rvHistorial.setAdapter(adapter);

        // Botón para volver atrás
        findViewById(R.id.BtnVolverHistorial).setOnClickListener(v -> finish());

        // Inicializa la barra de navegación flotante inferior
        FloatingNavigationHelper.setupFloatingNavigation(this, R.id.nav_historial);
    }

    /**
     * Refresca los datos cada vez que la actividad vuelve al primer plano.
     * Esto asegura que las comidas recién guardadas aparezcan inmediatamente.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarHistorial();
    }

    /**
     * Consulta la subcolección "comidas" del usuario actual en Firestore.
     * Ordena los resultados por fecha de forma descendente (más recientes primero).
     */
    private void cargarHistorial() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("usuarios").document(uid).collection("comidas")
                .orderBy("fecha", Query.Direction.DESCENDING) // Orden cronológico inverso
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaComidas.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            listaComidas.add(data);
                        }
                    }

                    // Gestión de estado vacío: si no hay comidas, se muestra un mensaje informativo
                    if (listaComidas.isEmpty()) {
                        rvHistorial.setVisibility(View.GONE);
                        layoutVacio.setVisibility(View.VISIBLE);
                    } else {
                        rvHistorial.setVisibility(View.VISIBLE);
                        layoutVacio.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged(); // Notifica al adaptador que hay nuevos datos
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar historial desde Firestore", e);
                });
    }

    /**
     * Adaptador interno para gestionar la visualización de cada ítem en el RecyclerView.
     */
    private static class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
        private final List<Map<String, Object>> lista;

        HistorialAdapter(List<Map<String, Object>> lista) {
            this.lista = lista;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Infla el diseño definido en item_historial_comida.xml
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_comida, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> comida = lista.get(position);

            // Vincula la categoría (ej: Almuerzo)
            String categoria = (String) comida.get("categoria");
            holder.tvTipo.setText(categoria != null ? categoria : "Comida");
            
            // Vincula la fecha
            holder.tvFecha.setText((String) comida.get("fecha"));
            
            // Procesa la lista de ingredientes para crear una cadena separada por comas
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

            // Recupera los valores numéricos de calorías y macros
            double cals = comida.get("calorias") != null ? ((Number) comida.get("calorias")).doubleValue() : 0;
            double p = comida.get("proteinas") != null ? ((Number) comida.get("proteinas")).doubleValue() : 0;
            double c = comida.get("carbohidratos") != null ? ((Number) comida.get("carbohidratos")).doubleValue() : 0;
            double g = comida.get("grasas") != null ? ((Number) comida.get("grasas")).doubleValue() : 0;

            // Formatea y muestra los valores en las etiquetas correspondientes
            holder.tvCals.setText(String.format("%.0f kcal", cals));
            holder.tvMacros.setText(String.format("P: %.1fg | C: %.1fg | G: %.1fg", p, c, g));
        }

        @Override
        public int getItemCount() {
            return lista.size();
        }

        /**
         * Clase interna ViewHolder que mantiene las referencias a las vistas de cada ítem.
         */
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
