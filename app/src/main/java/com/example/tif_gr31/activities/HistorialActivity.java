package com.example.tif_gr31.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Actividad que muestra el historial completo de comidas registradas por el usuario.
 * Utiliza un RecyclerView para listar los registros recuperados de Cloud Firestore.
 * Incluye un sistema de filtrado por periodo (Día, Semana, Mes) y por fecha específica.
 */
public class HistorialActivity extends AppCompatActivity {

    private static final String TAG = "HistorialActivity";
    private RecyclerView rvHistorial;
    private LinearLayout layoutVacio;
    private HistorialAdapter adapter;
    private final List<Map<String, Object>> listaComidas = new ArrayList<>();
    
    private TextView btnDia, btnSemana, btnMes;
    private ImageView btnCalendario;
    private TextView tvMensajeVacio, tvSubMensajeVacio;
    
    private int rangoSeleccionado = 7; // Semana por defecto
    private String fechaEspecifica = null; // Para filtrar por un día puntual

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
        layoutVacio = findViewById(R.id.layoutVacio);
        tvMensajeVacio = findViewById(R.id.tvMensajeVacio);
        tvSubMensajeVacio = findViewById(R.id.tvSubMensajeVacio);
        
        btnDia = findViewById(R.id.tvPeriodoDia);
        btnSemana = findViewById(R.id.tvPeriodoSemana);
        btnMes = findViewById(R.id.tvPeriodoMes);
        btnCalendario = findViewById(R.id.btnCalendario);

        configurarFiltros();

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
     * Configura los listeners para los botones de filtrado y el selector de fecha.
     */
    private void configurarFiltros() {
        if (btnDia != null) {
            btnDia.setOnClickListener(v -> {
                actualizarEstiloFiltro(btnDia);
                rangoSeleccionado = 1;
                fechaEspecifica = null;
                cargarHistorial();
            });
        }
        if (btnSemana != null) {
            btnSemana.setOnClickListener(v -> {
                actualizarEstiloFiltro(btnSemana);
                rangoSeleccionado = 7;
                fechaEspecifica = null;
                cargarHistorial();
            });
        }
        if (btnMes != null) {
            btnMes.setOnClickListener(v -> {
                actualizarEstiloFiltro(btnMes);
                rangoSeleccionado = 30;
                fechaEspecifica = null;
                cargarHistorial();
            });
        }
        if (btnCalendario != null) {
            btnCalendario.setOnClickListener(v -> mostrarDatePicker());
        }
    }

    /**
     * Muestra un diálogo para seleccionar una fecha específica, configurado en español.
     */
    private void mostrarDatePicker() {
        // Establecer el idioma a español para el diálogo
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Se usa el tema predeterminado del sistema que suele respetar el idioma del dispositivo
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Formatear la fecha seleccionada a yyyy-MM-dd
                    fechaEspecifica = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    actualizarEstiloFiltro(null); // Desmarcar otros filtros
                    btnCalendario.setColorFilter(Color.parseColor("#1c7d55"));
                    cargarHistorial();
                }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Resalta visualmente el filtro seleccionado.
     */
    private void actualizarEstiloFiltro(TextView seleccionado) {
        if (btnDia != null) btnDia.setTextColor(Color.parseColor("#64748b"));
        if (btnSemana != null) btnSemana.setTextColor(Color.parseColor("#64748b"));
        if (btnMes != null) btnMes.setTextColor(Color.parseColor("#64748b"));
        if (btnCalendario != null) btnCalendario.setColorFilter(Color.parseColor("#64748b"));
        
        if (seleccionado != null) {
            seleccionado.setTextColor(Color.parseColor("#1c7d55"));
        }
    }

    /**
     * Refresca los datos cada vez que la actividad vuelve al primer plano.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarHistorial();
    }

    /**
     * Consulta la subcolección "comidas" del usuario actual en Firestore.
     * Soporta filtrado por rango de días o por fecha específica.
     */
    private void cargarHistorial() {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        Query query;

        if (fechaEspecifica != null) {
            // Filtrado por un solo día
            query = db.collection("usuarios").document(uid).collection("comidas")
                    .whereEqualTo("fecha", fechaEspecifica);
            tvMensajeVacio.setText("Sin registros para este día");
            tvSubMensajeVacio.setText("No hay comidas guardadas el " + fechaEspecifica);
        } else {
            // Filtrado por rango (Día actual, Semana o Mes)
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -(rangoSeleccionado - 1));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaLimite = df.format(cal.getTime());

            query = db.collection("usuarios").document(uid).collection("comidas")
                    .whereGreaterThanOrEqualTo("fecha", fechaLimite)
                    .orderBy("fecha", Query.Direction.DESCENDING);
            
            tvMensajeVacio.setText("Tu historial aparecerá aquí");
            tvSubMensajeVacio.setText("Todavía no tienes registros en este periodo.");
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaComidas.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> data = doc.getData();
                        if (data != null) {
                            listaComidas.add(data);
                        }
                    }

                    // Gestión de estado vacío
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_comida, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> comida = lista.get(position);

            String categoria = (String) comida.get("categoria");
            holder.tvTipo.setText(categoria != null ? categoria : "Comida");
            
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

            double cals = comida.get("calorias") != null ? ((Number) comida.get("calorias")).doubleValue() : 0;
            double p = comida.get("proteinas") != null ? ((Number) comida.get("proteinas")).doubleValue() : 0;
            double c = comida.get("carbohidratos") != null ? ((Number) comida.get("carbohidratos")).doubleValue() : 0;
            double g = comida.get("grasas") != null ? ((Number) comida.get("grasas")).doubleValue() : 0;

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
