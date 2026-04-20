package com.example.tif_gr31.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tif_gr31.R;
import com.example.tif_gr31.api.ApiClient;
import com.example.tif_gr31.api.FoodProduct;
import com.example.tif_gr31.api.FoodSearchResponse;
import com.example.tif_gr31.utils.CsvFoodHelper;
import com.example.tif_gr31.utils.FloatingNavigationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.card.MaterialCardView;

/**
 * Actividad encargada de la búsqueda de alimentos y el registro de comidas.
 * Configurada actualmente para usar ÚNICAMENTE la base de datos local (CSV).
 */
public class RegistrarComidaActivity extends AppCompatActivity {

    private static final String USDA_API_KEY = "BpuqGxfs81r5NeHjS2pe7fdWV2fRJY5vKd59lgnI";
    private static final String[] TIPOS_COMIDA = {"Desayuno", "Almuerzo", "Merienda", "Cena", "Snacks"};

    private EditText etBuscarAlimento, etGramos;
    private ImageView btnLimpiarBusqueda;
    private RecyclerView rvResultadosBusqueda, rvIngredientesAgregados;
    private MaterialCardView cardAlimentoSeleccionado;
    private TextView tvNombreAlimentoSeleccionado, tvTotalCalorias, tvTotalMacros;
    private TextView tvCaloriasCalculadas, tvProteinasCalculadas, tvCarbosCalculados, tvGrasasCalculadas;
    private Spinner spinnerTipoComida;
    private MaterialButton btnAgregarIngrediente, btnGuardarComida;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final List<Map<String, Object>> listaResultados = new ArrayList<>();
    private final List<Map<String, Object>> listaIngredientes = new ArrayList<>();
    private ResultadosAdapter resultadosAdapter;
    private IngredientesAdapter ingredientesAdapter;

    private FoodProduct alimentoActual;
    private double totalKcal = 0, totalProt = 0, totalCarb = 0, totalGrasa = 0;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debouncedSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_comida);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrarComidaMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        vincularVistas();
        configurarAdapters();
        configurarBusqueda();
        configurarSpinner();
        configurarCampoGramos();

        manejarCategoriaPreseleccionada();
        FloatingNavigationHelper.setupFloatingNavigation(this, -1);

        btnAgregarIngrediente.setOnClickListener(v -> agregarIngredienteALista());
        btnGuardarComida.setOnClickListener(v -> guardarComidaCompleta());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void manejarCategoriaPreseleccionada() {
        String categoria = getIntent().getStringExtra("CATEGORIA_SELECCIONADA");
        if (categoria != null) {
            int position = Arrays.asList(TIPOS_COMIDA).indexOf(categoria);
            if (position >= 0) {
                spinnerTipoComida.setSelection(position);
            }
        }
    }

    private void vincularVistas() {
        etBuscarAlimento = findViewById(R.id.etBuscarAlimento);
        btnLimpiarBusqueda = findViewById(R.id.btnLimpiarBusqueda);
        rvResultadosBusqueda = findViewById(R.id.rvResultadosBusqueda);
        rvIngredientesAgregados = findViewById(R.id.rvIngredientesAgregados);
        cardAlimentoSeleccionado = findViewById(R.id.cardAlimentoSeleccionado);
        tvNombreAlimentoSeleccionado = findViewById(R.id.tvNombreAlimentoSeleccionado);
        etGramos = findViewById(R.id.etGramos);
        btnAgregarIngrediente = findViewById(R.id.btnAgregarIngrediente);
        tvTotalCalorias = findViewById(R.id.tvTotalCalorias);
        tvTotalMacros = findViewById(R.id.tvTotalMacros);
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);
        btnGuardarComida = findViewById(R.id.btnGuardarComida);

        tvCaloriasCalculadas = findViewById(R.id.tvCaloriasCalculadas);
        tvProteinasCalculadas = findViewById(R.id.tvProteinasCalculadas);
        tvCarbosCalculados = findViewById(R.id.tvCarbosCalculados);
        tvGrasasCalculadas = findViewById(R.id.tvGrasasCalculadas);
    }

    private void configurarAdapters() {
        rvResultadosBusqueda.setLayoutManager(new LinearLayoutManager(this));
        resultadosAdapter = new ResultadosAdapter(listaResultados, this::onAlimentoSeleccionadoDeBusqueda);
        rvResultadosBusqueda.setAdapter(resultadosAdapter);

        rvIngredientesAgregados.setLayoutManager(new LinearLayoutManager(this));
        ingredientesAdapter = new IngredientesAdapter(listaIngredientes);
        rvIngredientesAgregados.setAdapter(ingredientesAdapter);
    }

    private void configurarBusqueda() {
        etBuscarAlimento.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                btnLimpiarBusqueda.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                
                if (debouncedSearch != null) debounceHandler.removeCallbacks(debouncedSearch);
                
                if (query.length() < 2) {
                    rvResultadosBusqueda.setVisibility(View.GONE);
                    return;
                }
                
                // Ahora solo usamos la búsqueda en CSV
                debouncedSearch = () -> ejecutarBusquedaCSV(query);
                debounceHandler.postDelayed(debouncedSearch, 300); // Más rápido al ser local
            }
        });

        btnLimpiarBusqueda.setOnClickListener(v -> {
            etBuscarAlimento.setText("");
            rvResultadosBusqueda.setVisibility(View.GONE);
        });
    }

    /**
     * Búsqueda utilizando únicamente el archivo CSV local.
     * La lógica de la API de USDA queda comentada para pruebas.
     */
    private void ejecutarBusquedaCSV(String query) {
        listaResultados.clear();
        
        // 1. Buscar en CSV Local
        List<FoodProduct> locales = CsvFoodHelper.buscarEnCsv(this, query);
        for (FoodProduct f : locales) {
            Map<String, Object> map = new HashMap<>();
            map.put("nombre", f.getNombre());
            map.put("kcal", f.getKcal());
            map.put("rawObject", f);
            listaResultados.add(map);
        }

        /* 
        // 2. [COMENTADO] Buscar en API USDA
        ApiClient.getService().buscarAlimentos(query, USDA_API_KEY, 10).enqueue(new Callback<FoodSearchResponse>() {
            @Override
            public void onResponse(Call<FoodSearchResponse> call, Response<FoodSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodProduct> foods = response.body().getFoods();
                    if (foods != null) {
                        for (FoodProduct f : foods) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("nombre", f.getNombre());
                            map.put("kcal", f.getKcal());
                            map.put("rawObject", f);
                            listaResultados.add(map);
                        }
                    }
                }
                runOnUiThread(() -> {
                    resultadosAdapter.notifyDataSetChanged();
                    rvResultadosBusqueda.setVisibility(listaResultados.isEmpty() ? View.GONE : View.VISIBLE);
                });
            }
            @Override
            public void onFailure(Call<FoodSearchResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    resultadosAdapter.notifyDataSetChanged();
                    rvResultadosBusqueda.setVisibility(listaResultados.isEmpty() ? View.GONE : View.VISIBLE);
                });
            }
        });
        */

        // Notificación inmediata al ser local
        resultadosAdapter.notifyDataSetChanged();
        rvResultadosBusqueda.setVisibility(listaResultados.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void configurarCampoGramos() {
        etGramos.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                recalcularMacrosTemporales();
            }
        });
    }

    private void recalcularMacrosTemporales() {
        if (alimentoActual == null) return;
        String gramosStr = etGramos.getText().toString().trim();
        if (gramosStr.isEmpty()) {
            resetearDashboard();
            return;
        }

        try {
            double gramos = Double.parseDouble(gramosStr);
            double factor = gramos / 100.0;
            tvCaloriasCalculadas.setText(String.format(Locale.getDefault(), "%.0f", alimentoActual.getKcal() * factor));
            tvProteinasCalculadas.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getProteinas() * factor));
            tvCarbosCalculados.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getCarbohidratos() * factor));
            tvGrasasCalculadas.setText(String.format(Locale.getDefault(), "%.1f", alimentoActual.getGrasas() * factor));
        } catch (Exception e) {
            resetearDashboard();
        }
    }

    private void resetearDashboard() {
        tvCaloriasCalculadas.setText("0");
        tvProteinasCalculadas.setText("0");
        tvCarbosCalculados.setText("0");
        tvGrasasCalculadas.setText("0");
    }

    private void configurarSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TIPOS_COMIDA);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoComida.setAdapter(adapter);
    }

    private void onAlimentoSeleccionadoDeBusqueda(Map<String, Object> map) {
        alimentoActual = (FoodProduct) map.get("rawObject");
        tvNombreAlimentoSeleccionado.setText(alimentoActual.getNombre());
        cardAlimentoSeleccionado.setVisibility(View.VISIBLE);
        rvResultadosBusqueda.setVisibility(View.GONE);
        etGramos.requestFocus();
        recalcularMacrosTemporales();
    }

    private void agregarIngredienteALista() {
        String gramosStr = etGramos.getText().toString();
        if (gramosStr.isEmpty() || alimentoActual == null) return;

        double gramos = Double.parseDouble(gramosStr);
        double factor = gramos / 100.0;

        double kcalFinal = alimentoActual.getKcal() * factor;
        double protFinal = alimentoActual.getProteinas() * factor;
        double carbFinal = alimentoActual.getCarbohidratos() * factor;
        double grasaFinal = alimentoActual.getGrasas() * factor;

        Map<String, Object> ingrediente = new HashMap<>();
        ingrediente.put("nombre", alimentoActual.getNombre());
        ingrediente.put("gramos", gramos);
        ingrediente.put("kcal", kcalFinal);
        ingrediente.put("prot", protFinal);
        ingrediente.put("carb", carbFinal);
        ingrediente.put("grasas", grasaFinal);

        listaIngredientes.add(ingrediente);

        totalKcal += kcalFinal;
        totalProt += protFinal;
        totalCarb += carbFinal;
        totalGrasa += grasaFinal;

        actualizarResumenTotales();
        ingredientesAdapter.notifyDataSetChanged();

        alimentoActual = null;
        cardAlimentoSeleccionado.setVisibility(View.GONE);
        etBuscarAlimento.setText("");
        etGramos.setText("");
    }

    private void actualizarResumenTotales() {
        tvTotalCalorias.setText(String.format(Locale.getDefault(), "Total: %.0f kcal", totalKcal));
        tvTotalMacros.setText(String.format(Locale.getDefault(), "P: %.1fg | C: %.1fg | G: %.1fg", totalProt, totalCarb, totalGrasa));
    }

    private void guardarComidaCompleta() {
        if (listaIngredientes.isEmpty()) {
            Toast.makeText(this, "Añade al menos un ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getUid();
        if (userId == null) return;

        String tipoComida = spinnerTipoComida.getSelectedItem().toString();
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Map<String, Object> comidaMap = new HashMap<>();
        comidaMap.put("categoria", tipoComida); 
        comidaMap.put("fecha", fechaHoy);
        comidaMap.put("timestamp", new Date());
        comidaMap.put("calorias", totalKcal); 
        comidaMap.put("proteinas", totalProt); 
        comidaMap.put("carbohidratos", totalCarb); 
        comidaMap.put("grasas", totalGrasa); 
        comidaMap.put("ingredientes", listaIngredientes);

        db.collection("usuarios").document(userId).collection("comidas")
                .add(comidaMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Comida guardada exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show());
    }

    private class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ViewHolder> {
        private final List<Map<String, Object>> items;
        private final OnItemClickListener listener;

        public ResultadosAdapter(List<Map<String, Object>> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> item = items.get(position);
            holder.t1.setText((String) item.get("nombre"));
            holder.t2.setText(String.format(Locale.getDefault(), "%.0f kcal / 100g", (Double) item.get("kcal")));
            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;
            ViewHolder(View v) {
                super(v);
                t1 = v.findViewById(android.R.id.text1);
                t2 = v.findViewById(android.R.id.text2);
            }
        }
    }

    private class IngredientesAdapter extends RecyclerView.Adapter<IngredientesAdapter.ViewHolder> {
        private final List<Map<String, Object>> items;
        public IngredientesAdapter(List<Map<String, Object>> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> item = items.get(position);
            holder.t1.setText(String.format("%s (%.0fg)", item.get("nombre"), (Double) item.get("gramos")));
            holder.t2.setText(String.format(Locale.getDefault(), "%.1f kcal | P: %.1fg | C: %.1fg | G: %.1fg",
                    (Double) item.get("kcal"), (Double) item.get("prot"), (Double) item.get("carb"), (Double) item.get("grasas")));
        }

        @Override public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView t1, t2;
            ViewHolder(View v) {
                super(v);
                t1 = v.findViewById(android.R.id.text1);
                t2 = v.findViewById(android.R.id.text2);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(Map<String, Object> item);
    }
}
